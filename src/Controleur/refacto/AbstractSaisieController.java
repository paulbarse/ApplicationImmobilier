package Controleur.refacto;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

// Classe abstraite pour les contrôleurs de saisie

public abstract class AbstractSaisieController<V extends JDialog, T> implements ActionListener {

    protected final V vue;
    protected final String entityName;
    
    // Préfixe des commandes (ex: "BATIMENT" pour VALIDER_BATIMENT)
    protected final String commandPrefix;
    
    // Mode modification ou création
    protected boolean modeModification = false;

    protected AbstractSaisieController(V vue, String entityName, String commandPrefix) {
        this.vue = vue;
        this.entityName = entityName;
        this.commandPrefix = commandPrefix;
    }

    // Constructeur avec mode modification
     
    protected AbstractSaisieController(V vue, String entityName, String commandPrefix, boolean modeModification) {
        this(vue, entityName, commandPrefix);
        this.modeModification = modeModification;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        // Commande ANNULER
        if (isAnnulerCommand(cmd)) {
            handleAnnuler();
            return;
        }

        // Commande VALIDER
        if (isValiderCommand(cmd)) {
            handleValider();
            return;
        }

        // Autres commandes spécifiques
        handleOtherCommand(cmd, e);
    }

    // Vérifie si c'est une commande ANNULER
     
    protected boolean isAnnulerCommand(String cmd) {
        return ("ANNULER_" + commandPrefix).equals(cmd) 
            || "ANNULER".equals(cmd);
    }

    // Vérifie si c'est une commande VALIDER
     
    protected boolean isValiderCommand(String cmd) {
        return ("VALIDER_" + commandPrefix).equals(cmd) 
            || "VALIDER".equals(cmd);
    }

    // Gère l'annulation - Ferme la fenêtre sans sauvegarder
     
    protected void handleAnnuler() {
        setResult(null);
        vue.dispose();
    }

    // Gère la validation - Template Method Pattern
     
    protected void handleValider() {
        try {
            // Valider les champs
            if (!validateFields()) {
                return;
            }

            // Créer l'entité à partir des champs
            T entity = createEntityFromFields();
            if (entity == null) {
                return;
            }

            // Sauvegarder en BDD si nécessaire
            if (shouldSaveToDatabase()) {
                saveToDatabase(entity);
            }

            // Définir le résultat
            setResult(entity);
            
            // Afficher message de succès
            showSuccessMessage();

            // Fermer la fenêtre
            vue.dispose();

        } catch (ValidationException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            handleDatabaseError(ex);
        } catch (Exception ex) {
            showError("Erreur inattendue : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

   
    protected void handleOtherCommand(String cmd, ActionEvent e) {
        // Par défaut, ne rien faire
    }

    protected abstract boolean validateFields() throws ValidationException;

    // Crée l'entité à partir des champs du formulaire
  
    protected abstract T createEntityFromFields();

    // Définit le résultat de la saisie
     
    protected abstract void setResult(T entity);



    // Indique si l'entité doit être sauvegardée en BDD

    protected boolean shouldSaveToDatabase() {
        return false;
    }

    // Sauvegarde l'entité en BDD

    protected void saveToDatabase(T entity) throws SQLException {
        // À surcharger si shouldSaveToDatabase() retourne true
    }

    // Affiche un message de succès après la validation
     
    protected void showSuccessMessage() {
        if (modeModification) {
            showInfo(entityName + " modifié(e) avec succès !");
        } else {
            showInfo(entityName + " créé(e) avec succès !");
        }
    }

    // Gère les erreurs de base de données
     
    protected void handleDatabaseError(SQLException ex) {
        String message = ex.getMessage();
        
        // Messages d'erreur Oracle personnalisés
        if (message != null) {
            if (message.contains("ORA-00001") || message.contains("unique constraint")) {
                showError("Un(e) " + entityName.toLowerCase() + " avec cet identifiant existe déjà.");
                return;
            }
            if (message.contains("ORA-02291")) {
                showError("Référence invalide : une entité liée n'existe pas.");
                return;
            }
            if (message.contains("ORA-02292")) {
                showError("Impossible de supprimer : des données liées existent.");
                return;
            }
        }
        
        showError("Erreur base de données : " + message);
        ex.printStackTrace();
    }


    // UTILITAIRES DE VALIDATION


    // Valide qu'un champ texte n'est pas vide
     
    protected void validateNotEmpty(JTextField field, String fieldName) throws ValidationException {
        if (field.getText().trim().isEmpty()) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' est obligatoire.");
        }
    }

    // Valide qu'un champ texte est un nombre
     
    protected long validateLong(JTextField field, String fieldName) throws ValidationException {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' est obligatoire.");
        }
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' doit être un nombre entier.");
        }
    }

    // Valide qu'un champ texte est un nombre positif
     
    protected long validatePositiveLong(JTextField field, String fieldName) throws ValidationException {
        long value = validateLong(field, fieldName);
        if (value <= 0) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' doit être supérieur à 0.");
        }
        return value;
    }

    // Valide et parse un entier optionnel
     
    protected Integer validateOptionalInt(JTextField field, String fieldName) throws ValidationException {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' doit être un nombre entier.");
        }
    }

    // Valide et parse un double
     
    protected double validateDouble(JTextField field, String fieldName) throws ValidationException {
        String text = field.getText().trim().replace(",", ".");
        if (text.isEmpty()) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' est obligatoire.");
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' doit être un nombre décimal.");
        }
    }

    // Valide et parse un double optionnel
     
    protected Double validateOptionalDouble(JTextField field, String fieldName) throws ValidationException {
        String text = field.getText().trim().replace(",", ".");
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new ValidationException("Le champ '" + fieldName + "' doit être un nombre décimal.");
        }
    }

    // Valide un code postal (5 chiffres)
     
    protected int validateCodePostal(JTextField field) throws ValidationException {
        String text = field.getText().trim();
        if (text.isEmpty()) {
            field.requestFocus();
            throw new ValidationException("Le code postal est obligatoire.");
        }
        try {
            int cp = Integer.parseInt(text);
            if (cp < 1000 || cp > 99999) {
                field.requestFocus();
                throw new ValidationException("Le code postal doit être un nombre entre 1000 et 99999.");
            }
            return cp;
        } catch (NumberFormatException e) {
            field.requestFocus();
            throw new ValidationException("Le code postal doit être un nombre.");
        }
    }

    // Récupère le texte d'un champ ou null si vide
     
    protected String getTextOrNull(JTextField field) {
        String text = field.getText().trim();
        return text.isEmpty() ? null : text;
    }



    // Affiche un message d'erreur
     
    protected void showError(String message) {
        JOptionPane.showMessageDialog(
            vue, message, "Erreur", JOptionPane.ERROR_MESSAGE
        );
    }

    // Affiche un message d'avertissement
     
    protected void showWarning(String message) {
        JOptionPane.showMessageDialog(
            vue, message, "Attention", JOptionPane.WARNING_MESSAGE
        );
    }

    // Affiche un message d'information
     
    protected void showInfo(String message) {
        JOptionPane.showMessageDialog(
            vue, message, "Information", JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Demande une confirmation
     
    protected boolean confirm(String message) {
        return JOptionPane.showConfirmDialog(
            vue, message, "Confirmation", JOptionPane.YES_NO_OPTION
        ) == JOptionPane.YES_OPTION;
    }



    public V getVue() {
        return vue;
    }

    public boolean isModeModification() {
        return modeModification;
    }

    public void setModeModification(boolean modeModification) {
        this.modeModification = modeModification;
    }



    // Exception pour les erreurs de validation
     
    public static class ValidationException extends Exception {
        private static final long serialVersionUID = 1L;
        
        public ValidationException(String message) {
            super(message);
        }
    }
}
