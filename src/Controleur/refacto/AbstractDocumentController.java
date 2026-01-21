package Controleur.refacto;

import java.awt.Component;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;

import vue.refacto.DialogUtils;
import vue.refacto.FormatUtils;
import vue.refacto.TableUtils;

// Classe abstraite pour les contrôleurs de documents

public abstract class AbstractDocumentController {

    protected final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");



    // Retourne le composant parent pour les dialogues
    
    protected abstract Component getVue();

    // Charge toutes les données depuis la BDD
    
    protected abstract void chargerToutesDonnees();



    //Formate une date au format dd/MM/yyyy
     
    protected String formatDate(Date date) {
        return FormatUtils.formatDate(date);
    }

    // Formate un montant avec le symbole EUR
     
    protected String formatMontant(Double montant) {
        return FormatUtils.formatMontant(montant);
    }

    // Parse un montant depuis une chaîne
     
    protected double parseMontant(String s) {
        return FormatUtils.parseMontant(s);
    }

    // Convertit un objet en String de manière sûre
     
    protected String str(Object o) {
        return FormatUtils.str(o);
    }



    //Affiche un message d'erreur
     
    protected void showError(String message) {
        DialogUtils.showError(getVue(), message);
    }

    // Affiche un message de succès
     
    protected void showSuccess(String message) {
        DialogUtils.showSuccess(getVue(), message);
    }

    // Affiche un message d'information
     
    protected void showInfo(String message) {
        DialogUtils.showInfo(getVue(), message);
    }

    // Affiche un message simple
     
    protected void showMessage(String message) {
        DialogUtils.showMessage(getVue(), message);
    }

    // Demande confirmation pour une suppression
     
    protected boolean confirmerSuppression(String element) {
        return DialogUtils.confirmerSuppression(getVue(), element);
    }

    // Demande une confirmation générique
     
    protected boolean confirmer(String message) {
        return DialogUtils.confirmer(getVue(), message);
    }



    // Vérifie si une ligne est sélectionnée
     
    protected boolean hasSelection(JTable table) {
        return TableUtils.hasSelection(table);
    }

    
    protected Long getLongValue(JTable table, int row, int column) {
        return TableUtils.getLongValue(table, row, column);
    }

    // Récupère une valeur String depuis une cellule
     
    protected String getStringValue(JTable table, int row, int column) {
        return TableUtils.getStringValue(table, row, column);
    }



    // Affiche une erreur de format de date
     
    protected void showDateFormatError() {
        DialogUtils.showFormatDateError(getVue());
    }

    // Affiche une erreur de format de nombre
     
    protected void showNumberFormatError() {
        DialogUtils.showFormatNumberError(getVue());
    }

    // Affiche une erreur de base de données
     
    protected void showBddError(Exception e) {
        DialogUtils.showBddError(getVue(), e);
    }

    // Affiche un message de sélection requise
     
    protected void showSelectionRequise(String element) {
        DialogUtils.showSelectionRequise(getVue(), element);
    }



    // Parse une date avec gestion d'erreur
     
    protected Date parseDate(String dateStr) throws ParseException {
        return FormatUtils.parseDateStrict(dateStr);
    }

    // Parse un long avec gestion d'erreur
     
    protected long parseLong(String str) throws NumberFormatException {
        return Long.parseLong(str.trim());
    }

    // Parse un double avec gestion d'erreur
     
    protected double parseDouble(String str) throws NumberFormatException {
        return Double.parseDouble(str.replace(",", ".").trim());
    }
}
