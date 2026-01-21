package Controleur.refacto;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public final class TableUtils {

    private TableUtils() {
        // Empêche l'instanciation
    }

    public static void injecterLigne(DefaultTableModel m, int row, String[] data) {
        int cols = Math.min(m.getColumnCount(), data.length);
        for (int c = 0; c < cols; c++) {
            m.setValueAt(data[c], row, c);
        }
    }

    public static void supprimerLigne(JTable table, String nomObjet) {
        int row = table.getSelectedRow();
        if (row < 0) {
            System.out.println("Aucun " + nomObjet + " sélectionné pour suppression");
            return;
        }
        DefaultTableModel m = (DefaultTableModel) table.getModel();
        m.removeRow(row);
    }
}
