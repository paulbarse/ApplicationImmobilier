package vue.refacto;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Classe utilitaire pour le formatage des données 
 */
public final class FormatUtils {

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd/MM/yyyy");
    private static final DecimalFormat DF;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRANCE);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        DF = new DecimalFormat("#,##0.00", symbols);
    }

    private FormatUtils() {
        // Classe utilitaire non instanciable
    }

    // NETTOYAGE DES CHAINES 

    /**
     * Nettoie une chaîne de caractères (trim + null si vide)
     */
    public static String clean(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Nettoie une chaîne et retourne une valeur par défaut si null/vide
     */
    public static String cleanOrDefault(String s, String defaultValue) {
        String cleaned = clean(s);
        return cleaned != null ? cleaned : defaultValue;
    }

    // FORMATAGE DES DATES

    /**
     * Formate une date au format dd/MM/yyyy
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        synchronized (SDF) {
            return SDF.format(date);
        }
    }

    /**
     * Parse une date au format dd/MM/yyyy
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            synchronized (SDF) {
                return SDF.parse(dateStr.trim());
            }
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * Parse une date avec exception
     */
    public static Date parseDateStrict(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new ParseException("Date vide", 0);
        }
        synchronized (SDF) {
            return SDF.parse(dateStr.trim());
        }
    }

    // FORMATAGE DES MONTANTS

    /**
     * Formate un montant avec le symbole € (format français)
     */
    public static String formatMontant(Double montant) {
        if (montant == null) return "0,00 €";
        synchronized (DF) {
            return DF.format(montant) + " €";
        }
    }

    /**
     * Formate un montant avec "EUR" au lieu de €
     */
    public static String formatMontantEUR(Double montant) {
        if (montant == null) return "0,00 EUR";
        return String.format("%.2f EUR", montant);
    }

    /**
     * Formate un montant sans symbole €
     */
    public static String formatMontantSansSymbole(Double montant) {
        if (montant == null) return "0,00";
        synchronized (DF) {
            return DF.format(montant);
        }
    }

    /**
     * Parse un montant depuis une chaîne
     */
    public static double parseMontant(String montantStr) {
        if (montantStr == null || montantStr.trim().isEmpty()) return 0.0;
        try {
            String cleaned = montantStr
                .replace("€", "")
                .replace("EUR", "")
                .replace(" ", "")
                .replace(",", ".")
                .trim();
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    // UTILITAIRES POUR STRINGS

    /**
     * Convertit un objet en String de manière sûre
     */
    public static String str(Object obj) {
        return obj != null ? obj.toString() : "";
    }

    /**
     * Vérifie si une chaîne est vide ou null
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Retourne une valeur par défaut si la chaîne est vide
     */
    public static String defaultIfEmpty(String str, String defaultValue) {
        return isEmpty(str) ? defaultValue : str;
    }

    /**
     * Tronque une chaîne à une longueur maximale
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }
}
