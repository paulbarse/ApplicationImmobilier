package vue.refacto;

import java.util.Date;
import java.util.List;
import java.util.function.Function;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
 * Classe utilitaire pour la gestion des JTable
 */
public final class TableUtils {

    private TableUtils() {
        // Classe utilitaire non instanciable
    }

    // CHARGEMENT DE DONNÉES

    /**
     * Charge une liste de données dans une table
     */
    public static void chargerTable(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(row);
            }
        }
    }

    /**
     * Charge une liste de données dans une table avec transformation
     */
    public static void chargerTable(JTable table, List<Object[]> data, Function<Object[], Object[]> mapper) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(mapper.apply(row));
            }
        }
    }

    /**
     * Charge une liste de données dans une table avec formatage automatique
     * des dates et montants
     */
    public static void chargerTableFormatee(JTable table, List<Object[]> data) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        if (data != null) {
            for (Object[] row : data) {
                model.addRow(formatRow(row));
            }
        }
    }

    /**
     * Formate une ligne en convertissant les dates et montants
     */
    public static Object[] formatRow(Object[] row) {
        Object[] formatted = new Object[row.length];
        for (int i = 0; i < row.length; i++) {
            if (row[i] instanceof Date) {
                formatted[i] = FormatUtils.formatDate((Date) row[i]);
            } else if (row[i] instanceof Double) {
                formatted[i] = FormatUtils.formatMontant((Double) row[i]);
            } else {
                formatted[i] = row[i];
            }
        }
        return formatted;
    }

    /**
     * Vide une table
     */
    public static void viderTable(JTable table) {
        ((DefaultTableModel) table.getModel()).setRowCount(0);
    }

    /**
     * Ajoute une ligne à une table
     */
    public static void ajouterLigne(JTable table, Object[] row) {
        ((DefaultTableModel) table.getModel()).addRow(row);
    }

    /**
     * Supprime la ligne sélectionnée
     */
    public static boolean supprimerLigneSelectionnee(JTable table) {
        int row = table.getSelectedRow();
        if (row >= 0) {
            ((DefaultTableModel) table.getModel()).removeRow(row);
            return true;
        }
        return false;
    }

    // RÉCUPÉRATION DE VALEURS

    /**
     * Récupère une valeur Long depuis une cellule
     */
    public static Long getLongValue(JTable table, int row, int column) {
        Object value = table.getValueAt(row, column);
        if (value == null) return null;
        if (value instanceof Long) return (Long) value;
        if (value instanceof Number) return ((Number) value).longValue();
        try {
            return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Récupère une valeur String depuis une cellule
     */
    public static String getStringValue(JTable table, int row, int column) {
        Object value = table.getValueAt(row, column);
        return value != null ? value.toString() : "";
    }

    /**
     * Récupère une valeur Double depuis une cellule
     */
    public static Double getDoubleValue(JTable table, int row, int column) {
        Object value = table.getValueAt(row, column);
        if (value == null) return null;
        if (value instanceof Double) return (Double) value;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return FormatUtils.parseMontant(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    // SÉLECTION

    /**
     * Vérifie si une ligne est sélectionnée
     */
    public static boolean hasSelection(JTable table) {
        return table.getSelectedRow() >= 0;
    }

    /**
     * Récupère l'index de la ligne sélectionnée
     */
    public static int getSelectedRow(JTable table) {
        return table.getSelectedRow();
    }

    /**
     * Sélectionne une ligne
     */
    public static void selectRow(JTable table, int row) {
        if (row >= 0 && row < table.getRowCount()) {
            table.setRowSelectionInterval(row, row);
        }
    }

    /**
     * Crée un modèle de table non éditable
     */
    public static DefaultTableModel createNonEditableModel(String[] columnNames) {
        return new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }
}
