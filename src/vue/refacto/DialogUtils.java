package vue.refacto;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Classe utilitaire pour les boîtes de dialogue
 */
public final class DialogUtils {

    private DialogUtils() {
        // Classe utilitaire non instanciable
    }

    // MESSAGES SIMPLES

    /**
     * Affiche un message d'erreur
     */
    public static void showError(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Affiche un message de succès
     */
    public static void showSuccess(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche un message d'information
     */
    public static void showInfo(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Affiche un message simple (sans titre spécifique)
     */
    public static void showMessage(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message);
    }

    /**
     * Affiche un message d'avertissement
     */
    public static void showWarning(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Attention", JOptionPane.WARNING_MESSAGE);
    }

    // CONFIRMATIONS

    /**
     * Demande confirmation pour une suppression
     */
    public static boolean confirmerSuppression(Component parent, String element) {
        return JOptionPane.showConfirmDialog(
            parent,
            "Voulez-vous vraiment supprimer ce " + element + " ?",
            "Confirmation de suppression",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    /**
     * Demande une confirmation générique
     */
    public static boolean confirmer(Component parent, String message) {
        return JOptionPane.showConfirmDialog(
            parent,
            message,
            "Confirmation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    // MESSAGES D'ERREUR PRÉDÉFINIS

    /**
     * Affiche une erreur de connexion BDD avec message
     */
    public static void showBddError(Component parent, String details) {
        showError(parent, "Erreur de base de données: " + details);
    }

    /**
     * Affiche une erreur de connexion BDD avec exception
     */
    public static void showBddError(Component parent, Exception ex) {
        showError(parent, "Erreur BDD: " + ex.getMessage());
    }

    /**
     * Affiche une erreur de format de nombre
     */
    public static void showFormatNumberError(Component parent) {
        showError(parent, "Erreur: Les champs numériques doivent contenir des nombres valides");
    }

    /**
     * Affiche une erreur de format de date
     */
    public static void showFormatDateError(Component parent) {
        showError(parent, "Format de date invalide. Utilisez dd/MM/yyyy");
    }

    /**
     * Affiche un message de sélection requise
     */
    public static void showSelectionRequise(Component parent, String element) {
        showInfo(parent, "Veuillez sélectionner un " + element);
    }

    /*
     * Affiche un message de champ obligatoire
     */
    public static void showChampObligatoire(Component parent, String champ) {
        showError(parent, "Le champ '" + champ + "' est obligatoire");
    }
}
