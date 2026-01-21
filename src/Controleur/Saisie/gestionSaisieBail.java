package Controleur.Saisie;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.Date;

import javax.swing.JOptionPane;

import modele.Locataire;
import modele.dao.BailDao;
import modele.dao.LocataireDao;
import modele.dao.UtOracleDataSource;
import vue.Saisie.pageSaisieBail;

/**
 * Contrôleur pour la page de saisie d'un nouveau bail
 */
public class gestionSaisieBail implements ActionListener {

    private final pageSaisieBail vue;

    public gestionSaisieBail(pageSaisieBail vue) {
        this.vue = vue;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "VALIDER_BAIL":
                creerBail();
                break;

            case "ANNULER_BAIL":
                vue.setBailCree(false);
                vue.dispose();
                break;

            default:
                break;
        }
    }

    private void creerBail() {
        String idBailStr = vue.getTxtIdBail().getText().trim();
        if (idBailStr.isEmpty()) {
            afficherErreur("L'ID du bail est obligatoire.");
            vue.getTxtIdBail().requestFocus();
            return;
        }

        long idBail;
        try {
            idBail = Long.parseLong(idBailStr);
        } catch (NumberFormatException ex) {
            afficherErreur("L'ID du bail doit être un nombre.");
            vue.getTxtIdBail().requestFocus();
            return;
        }

        Date dateDebut = vue.getDateDebut();
        if (dateDebut == null) {
            afficherErreur("La date de début est obligatoire.");
            return;
        }

        String loyerStr = vue.getTxtLoyerInitial().getText().trim().replace(",", ".");
        if (loyerStr.isEmpty()) {
            afficherErreur("Le loyer est obligatoire.");
            vue.getTxtLoyerInitial().requestFocus();
            return;
        }

        double loyerInitial;
        try {
            loyerInitial = Double.parseDouble(loyerStr);
            if (loyerInitial <= 0) {
                afficherErreur("Le loyer doit être supérieur à 0.");
                return;
            }
        } catch (NumberFormatException ex) {
            afficherErreur("Le loyer doit être un nombre valide.");
            vue.getTxtLoyerInitial().requestFocus();
            return;
        }

        String provisionsStr = vue.getTxtProvisions().getText().trim().replace(",", ".");
        if (provisionsStr.isEmpty()) {
            afficherErreur("Les provisions pour charges sont obligatoires.");
            vue.getTxtProvisions().requestFocus();
            return;
        }

        double provisions;
        try {
            provisions = Double.parseDouble(provisionsStr);
            if (provisions < 0) {
                afficherErreur("Les provisions ne peuvent pas être négatives.");
                return;
            }
        } catch (NumberFormatException ex) {
            afficherErreur("Les provisions doivent être un nombre valide.");
            vue.getTxtProvisions().requestFocus();
            return;
        }

        Double caution = null;
        String cautionStr = vue.getTxtCaution().getText().trim().replace(",", ".");
        if (!cautionStr.isEmpty()) {
            try {
                caution = Double.parseDouble(cautionStr);
            } catch (NumberFormatException ex) {
                afficherErreur("La caution doit être un nombre valide.");
                vue.getTxtCaution().requestFocus();
                return;
            }
        }

        Integer jourPaiement = vue.getJourPaiement();

        Long idLocataire = vue.getSelectedLocataireId();
        if (idLocataire == null) {
            afficherErreur("Veuillez sélectionner un locataire.");
            return;
        }

        // Vérification coordonnées locataire
        try {
            Locataire loc = null;

            pageSaisieBail.LocataireComboItem item =
                    (pageSaisieBail.LocataireComboItem) vue.getCbLocataire().getSelectedItem();

            if (item != null) loc = item.getLocataire();

            // Fallback DAO si jamais
            if (loc == null) {
                LocataireDao locataireDao = null;
                try {
                    UtOracleDataSource.creerAcces();
                    locataireDao = new LocataireDao();
                    locataireDao.setConnection(UtOracleDataSource.getConnection());
                    loc = locataireDao.findById(idLocataire.toString());
                } finally {
                    if (locataireDao != null) try { locataireDao.close(); } catch (SQLException ex) { /* ignore */ }
                }
            }

            if (loc == null) {
                afficherErreur("Locataire non trouvé (ID: " + idLocataire + ").");
                return;
            }

            StringBuilder erreurs = new StringBuilder();
            boolean ok = true;

            if (loc.getNomLoc() == null || loc.getNomLoc().trim().isEmpty()) {
                erreurs.append("• Le nom est obligatoire\n");
                ok = false;
            }
            if (loc.getPrenomLoc() == null || loc.getPrenomLoc().trim().isEmpty()) {
                erreurs.append("• Le prénom est obligatoire\n");
                ok = false;
            }

            boolean hasContact = false;
            if (loc.getDateNaissance() != null) hasContact = true;
            if (loc.getTelLoc() != null && !loc.getTelLoc().trim().isEmpty()) hasContact = true;
            if (loc.getMailLoc() != null && !loc.getMailLoc().trim().isEmpty()) hasContact = true;

            if (!hasContact) {
                erreurs.append("• Au moins un moyen de contact requis (adresse, téléphone ou email)\n");
                ok = false;
            }

            if (!ok) {
                JOptionPane.showMessageDialog(
                        vue,
                        "Le locataire sélectionné n'a pas les coordonnées minimales requises :\n\n"
                                + erreurs.toString()
                                + "\nLocataire : " + loc.getNomComplet(),
                        "Coordonnées insuffisantes",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        int anneeLoyers = vue.getAnneeLoyers();

        Long idLogement = null;
        Long idGarage = null;

        if (vue.isLogementSeul()) {
            idLogement = vue.getSelectedLogementId();
            if (idLogement == null) {
                afficherErreur("Veuillez sélectionner un logement.");
                return;
            }
        } else if (vue.isGarageSeul()) {
            idGarage = vue.getSelectedGarageId();
            if (idGarage == null) {
                afficherErreur("Veuillez sélectionner un garage.");
                return;
            }
        } else if (vue.isLogementEtGarage()) {
            idLogement = vue.getSelectedLogementId();
            idGarage = vue.getSelectedGarageId();
            if (idLogement == null) {
                afficherErreur("Veuillez sélectionner un logement.");
                return;
            }
            if (idGarage == null) {
                afficherErreur("Veuillez sélectionner un garage.");
                return;
            }
        }

        BailDao bailDao = null;
        try {
            UtOracleDataSource.creerAcces();
            bailDao = new BailDao();
            bailDao.setConnection(UtOracleDataSource.getConnection());

            String typeLocation;

            if (vue.isLogementSeul()) {
                bailDao.creerBailComplet(
                        idBail, dateDebut, loyerInitial, provisions, caution, jourPaiement,
                        idLogement, idLocataire
                );
                typeLocation = "Logement";

            } else if (vue.isGarageSeul()) {
                bailDao.creerBailGarage(
                        idBail, dateDebut, loyerInitial, provisions, caution, jourPaiement,
                        idGarage, idLocataire
                );
                typeLocation = "Garage";

            } else {
                bailDao.creerBailComplet(
                        idBail, dateDebut, loyerInitial, provisions, caution, jourPaiement,
                        idLogement, idLocataire
                );
                bailDao.lierGarageAuBail(idBail, idGarage);
                typeLocation = "Logement + Garage";
            }

            bailDao.genererLoyersAnnuels(idBail, anneeLoyers);

            vue.setBailCree(true);

            JOptionPane.showMessageDialog(
                    vue,
                    "Bail créé avec succès !\n\n"
                            + "• Bail N° " + idBail + "\n"
                            + "• Type : " + typeLocation + "\n"
                            + "• Loyer : " + loyerInitial + " €\n"
                            + "• Provisions : " + provisions + " €\n"
                            + "• Loyers générés pour " + anneeLoyers + "\n\n"
                            + "Le locataire passe au statut ACTIF.",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE
            );

            vue.dispose();

        } catch (Exception ex) {
            ex.printStackTrace();
            String message = ex.getMessage();

            if (message == null) message = "";

            if (message.contains("ORA-00001") || message.contains("unique constraint")) {
                afficherErreur("Un bail avec cet ID existe déjà.");
            } else if (message.contains("-20201")) {
                afficherErreur("Logement non trouvé.");
            } else if (message.contains("-20202")) {
                afficherErreur("Le bail n'est pas actif.");
            } else if (message.contains("-20206")) {
                afficherErreur("Garage non trouvé.");
            } else if (message.contains("-20205")) {
                afficherErreur("Garage déjà loué.");
            } else if (message.contains("-20301")) {
                afficherErreur("Le nom du locataire est obligatoire pour signer un bail.\nVeuillez compléter les informations du locataire.");
            } else if (message.contains("-20302")) {
                afficherErreur("Le prénom du locataire est obligatoire pour signer un bail.\nVeuillez compléter les informations du locataire.");
            } else if (message.contains("-20303")) {
                afficherErreur("Le locataire doit avoir au moins un moyen de contact (adresse, téléphone ou email).\nVeuillez compléter les informations du locataire.");
            } else if (message.contains("-20304")) {
                afficherErreur("Locataire non trouvé.");
            } else {
                afficherErreur("Erreur lors de la création du bail :\n" + message);
            }
        } finally {
            if (bailDao != null) try { bailDao.close(); } catch (Exception ex) { /* ignore */ }
        }
    }

    private void afficherErreur(String message) {
        JOptionPane.showMessageDialog(vue, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}
