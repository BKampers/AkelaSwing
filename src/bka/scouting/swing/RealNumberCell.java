package bka.scouting.swing;


class RealNumberCell extends AbstractTableCellEditor 
    implements javax.swing.table.TableCellRenderer, javax.swing.table.TableCellEditor, java.awt.event.KeyListener {//, java.awt.event.ActionListener {

    
    RealNumberCell(int decimals) {
        this.decimals = decimals;
    }
    
    
    public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        createField((Double) value);
        field.setBackground(bka.swing.Table.background(table, isSelected, row));
        field.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        field.setBorder(null);
        return field;
    }

    public java.awt.Component getTableCellEditorComponent(javax.swing.JTable table, Object value, boolean isSelected, int row, int column) {
        createField((Double) value);
        field.setBackground(bka.swing.Table.background(table, isSelected, row));
        field.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
        field.setBorder(new javax.swing.border.LineBorder(java.awt.Color.BLACK));
        //field.addActionListener(this);
        field.addKeyListener(this);
        return field;
    }
    
    
    public Object getCellEditorValue() {
        try {
            java.util.Scanner scanner = new java.util.Scanner(field.getText());
            return scanner.nextDouble();
        }
        catch (Exception ex) {
            return null;
        }
    }

    
    public void keyTyped(java.awt.event.KeyEvent evt) {
    }

    
    public void keyPressed(java.awt.event.KeyEvent evt) {
        if (evt.getKeyChar() == java.awt.event.KeyEvent.VK_ESCAPE) {
            cancelCellEditing();
        }
    }

    public void keyReleased(java.awt.event.KeyEvent evt) {
    }

//    public void actionPerformed(java.awt.event.ActionEvent evt) {
//        if (table.isEditing()) {
//            stopCellEditing();
//        }
//    }

    private void createField(Double value) {
        field = new javax.swing.JFormattedTextField();
        field.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        if (value != null) {
            java.util.Formatter formatter = new java.util.Formatter();
            formatter.format("%%.%df", decimals);
            String format = formatter.toString();
            formatter = new java.util.Formatter();
            formatter.format(format, value);
            field.setText(formatter.toString());
        }
//        bka.swing.validators.RealValidator validator = new bka.swing.validators.RealValidator(field, 0.0, Double.MAX_VALUE, decimals);
//        validator.verify();
    }

    
    
    protected javax.swing.JFormattedTextField field;


    private int decimals;
    
}

