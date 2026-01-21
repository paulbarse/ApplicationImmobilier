package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;

import javax.swing.JOptionPane;

import modele.dao.ChargesDao;
import vue.Saisie.pageSaisieChargesLocataire;

// Contrôleur pour la saisie des charges d'immeuble

public class gestionSaisieChargesLocataire implements ActionListener {

    private pageSaisieChargesLocataire vue;

    public gestionSaisieChargesLocataire(pageSaisieChargesLocataire vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        
        if ("VALIDER_CHARGES".equals(cmd) || "VALIDER_SAISIE".equals(cmd)) {
            enregistrerCharges();
        }
    }
    

    public void chargerDonneesExistantes() {
        long idBatiment = vue.getIdBatiment();
        int annee = vue.getAnnee();
        
        if (idBatiment <= 0) {
            System.out.println("[gestionSaisieChargesLocataire] Pas de bâtiment défini, pas de chargement");
            return;
        }
        
        try {
            ChargesDao dao = new ChargesDao();
            
            // Récupérer les montants existants par type
            double eau = dao.getMontantByType(idBatiment, annee, ChargesDao.TYPE_EAU);
            double om = dao.getMontantByType(idBatiment, annee, ChargesDao.TYPE_OM);
            double elec = dao.getMontantByType(idBatiment, annee, ChargesDao.TYPE_ELEC);
            double entretien = dao.getMontantByType(idBatiment, annee, ChargesDao.TYPE_ENTRETIEN);
            
            // Récupérer le pourcentage récupérable (prend celui de l'eau comme référence)
            double pctRecuperable = dao.getPctRecuperableByType(idBatiment, annee, ChargesDao.TYPE_EAU);
            
            dao.close();
            
            // Pré-remplir les champs s'il y a des valeurs
            vue.setMontants(eau, om, elec, entretien);
            if (pctRecuperable > 0 && pctRecuperable != 100) {
                vue.setPctRecuperable(pctRecuperable);
            }
            
            System.out.println("[gestionSaisieChargesLocataire] Données chargées pour bâtiment " + idBatiment + 
                               ", année " + annee + " : EAU=" + eau + ", OM=" + om + 
                               ", ELEC=" + elec + ", ENTRETIEN=" + entretien);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("[gestionSaisieChargesLocataire] Erreur lors du chargement : " + ex.getMessage());
        }
    }
    

    private void enregistrerCharges() {
        long idBatiment = vue.getIdBatiment();
        int annee = vue.getAnnee();
        
        // VÉRIFICATION DU CONTEXTE 
        if (idBatiment <= 0) {
            JOptionPane.showMessageDialog(vue, 
                "Erreur : aucun bâtiment sélectionné.\n\n" +
                "Veuillez sélectionner un locataire dans le tableau principal\n" +
                "avant d'ouvrir cette fenêtre de saisie.",
                "Erreur de contexte", JOptionPane.ERROR_MESSAGE);
            return;
        }
        

        double montantEau = parseDouble(vue.getTxtMontantEau().getText());
        double montantOM = parseDouble(vue.getTxtMontantOM().getText());
        double montantElec = parseDouble(vue.getTxtMontantElec().getText());
        double montantEntretien = parseDouble(vue.getTxtMontantEntretien().getText());
        double pctRecuperable = parseDouble(vue.getTxtPctRecuperable().getText());
        
        // Valeurs par défaut
        if (pctRecuperable <= 0 || pctRecuperable > 100) {
            pctRecuperable = 100;
        }
        

        // Au moins un montant doit être saisi
        if (montantEau <= 0 && montantOM <= 0 && montantElec <= 0 && montantEntretien <= 0) {
            JOptionPane.showMessageDialog(vue,
                "Veuillez saisir au moins un montant de charge.",
                "Saisie incomplète", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Vérifier les montants négatifs
        if (montantEau < 0 || montantOM < 0 || montantElec < 0 || montantEntretien < 0) {
            JOptionPane.showMessageDialog(vue,
                "Les montants ne peuvent pas être négatifs.",
                "Erreur de saisie", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // confirme
        double total = montantEau + montantOM + montantElec + montantEntretien;
        String message = String.format(
            "Enregistrer les charges pour l'année %d ?\n\n" +
            "Bâtiment : %s\n\n" +
            "Eau : %.2f €\n" +
            "TEOM/Ordures ménagères : %.2f €\n" +
            "Électricité communes : %.2f €\n" +
            "Entretien/Nettoyage : %.2f €\n" +
            "───────────────────────────\n" +
            "TOTAL : %.2f €\n\n" +
            "Pourcentage récupérable : %.0f %%",
            annee, 
            vue.getAdresseBatiment() != null && !vue.getAdresseBatiment().isEmpty() 
                ? vue.getAdresseBatiment() : "Bâtiment #" + idBatiment,
            montantEau, montantOM, montantElec, montantEntretien, total, pctRecuperable);
        
        int confirm = JOptionPane.showConfirmDialog(vue,
            message,
            "Confirmation d'enregistrement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        

        try {
            ChargesDao dao = new ChargesDao();
            int nbChargesEnregistrees = 0;
            
            // Enregistrer chaque type de charge si montant > 0
            if (montantEau > 0) {
                dao.createOrUpdate(idBatiment, annee, ChargesDao.TYPE_EAU, montantEau, pctRecuperable);
                nbChargesEnregistrees++;
                System.out.println("[gestionSaisieChargesLocataire] EAU enregistré : " + montantEau);
            }
            
            if (montantOM > 0) {
                dao.createOrUpdate(idBatiment, annee, ChargesDao.TYPE_OM, montantOM, pctRecuperable);
                nbChargesEnregistrees++;
                System.out.println("[gestionSaisieChargesLocataire] OM enregistré : " + montantOM);
            }
            
            if (montantElec > 0) {
                dao.createOrUpdate(idBatiment, annee, ChargesDao.TYPE_ELEC, montantElec, pctRecuperable);
                nbChargesEnregistrees++;
                System.out.println("[gestionSaisieChargesLocataire] ELEC enregistré : " + montantElec);
            }
            
            if (montantEntretien > 0) {
                dao.createOrUpdate(idBatiment, annee, ChargesDao.TYPE_ENTRETIEN, montantEntretien, pctRecuperable);
                nbChargesEnregistrees++;
                System.out.println("[gestionSaisieChargesLocataire] ENTRETIEN enregistré : " + montantEntretien);
            }
            
            dao.close();
            
            // Succès
            JOptionPane.showMessageDialog(vue,
                "Charges enregistrées avec succès !\n\n" +
                nbChargesEnregistrees + " type(s) de charge mis à jour.\n" +
                "Total : " + String.format("%.2f", total) + " €",
                "Succès", JOptionPane.INFORMATION_MESSAGE);
            
            // Marquer comme valide et fermer
            vue.setValide(true);
            vue.dispose();
            
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(vue,
                "Erreur lors de l'enregistrement des charges :\n\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    

    private double parseDouble(String s) {
        if (s == null || s.trim().isEmpty()) {
            return 0;
        }
        try {
            // Supprimer les espaces et remplacer la virgule par un point
            String cleaned = s.trim()
                              .replace(" ", "")
                              .replace(",", ".");
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}