package bka.scouting.swing;


abstract class AbstractTableCellEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor, javax.swing.table.TableCellRenderer {

    
    /**
     * Determine if the cell editor should be started. This method will not be called if the table model's 
     * isCellEditable method returns false.
     */
    public boolean isCellEditable(java.util.EventObject evt) {
        boolean editable = true;
        if (evt instanceof java.awt.event.MouseEvent) {
            javax.swing.JTable table = (javax.swing.JTable) evt.getSource();
            //* Start editor only if a cell in the selected row is clicked.
            java.awt.Point point = ((java.awt.event.MouseEvent) evt).getPoint();
            int selectedRow = table.getSelectedRow();
            int selectedColumn = table.getSelectedColumn();
            java.awt.Rectangle rectangle = table.getCellRect(selectedRow, selectedColumn, true);
            editable = rectangle.y < point.y && point.y < rectangle.y + rectangle.height;
        }
        return editable;            
    }


}
