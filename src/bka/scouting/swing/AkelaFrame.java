package bka.scouting.swing;

import bka.scouting.*;
import bka.swing.Table;
import java.awt.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.AbstractTableModel;


public class AkelaFrame extends bka.swing.FrameApplication {
    

    public AkelaFrame() {
        java.net.URL logoUrl = ClassLoader.getSystemClassLoader().getResource("images/SCOUTING.jpg");
        Image image = Toolkit.getDefaultToolkit().createImage(logoUrl);
        setIconImage(image);
        initComponents();
        Table.setColumnWidths(membersTable, new int[] {30, 50, -1, 25, 5, 50});
        javax.swing.table.TableRowSorter<AbstractTableModel> sorter = new javax.swing.table.TableRowSorter<AbstractTableModel>(MEMBERS_TABLE_MODEL);
        membersTable.setRowSorter(sorter);
        javax.swing.event.DocumentListener documentListener = new javax.swing.event.DocumentListener() {
            public void insertUpdate(DocumentEvent de) {
                enableInvoiceButton();
            }
            public void removeUpdate(DocumentEvent de) {
                enableInvoiceButton();
            }
            public void changedUpdate(DocumentEvent de) {
                // no action needed
            }
        };
        accountNumberFormattedTextField.getDocument().addDocumentListener(documentListener);
        accountHolderFormattedTextField.getDocument().addDocumentListener(documentListener);
        contactNameFormattedTextField.getDocument().addDocumentListener(documentListener);
        contactPhoneFormattedTextField.getDocument().addDocumentListener(documentListener);
        contactEmailFormattedTextField.getDocument().addDocumentListener(documentListener);
    }
    

    public String applicationName() {
        return "Akela";
    }


    public String manufacturerName() {
        return "Scouting Jan Baloys Stiphout";
    }
    
    
    public static void main(final String arguments[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AkelaFrame frame = new AkelaFrame();
                frame.initialize(arguments);
                frame.setVisible(true);
            }
        });
    }


    protected void initialize(String[] arguments) {
        parseArguments(arguments);
//        loadProperties();
//        try {
//            int x = Integer.parseInt(getProperty("x"));
//            int y = Integer.parseInt(getProperty("y"));
//            int width = Integer.parseInt(getProperty("width"));
//            int height = Integer.parseInt(getProperty("height"));
//            int extended = Integer.parseInt(getProperty("extended"));
//            setSize(width, height);
//            setLocation(x, y);
//            setExtendedState(extended);
//        }
//        catch (NumberFormatException ex) {
//            // ignore
//        }            
        dataPath = getProperty("dataPath", System.getProperty("user.dir"));
        String fileSeparator = System.getProperty("file.separator");
        if (! dataPath.endsWith(fileSeparator)) {
            dataPath += fileSeparator;
        }
        String title = getTitle();
        setTitle(title + " @ " + dataPath);
        akela = new Akela(dataPath);
        sectionsList.setListData(akela.getSections());
        sectionsList.addListSelectionListener(new SectionsListListener());
        sectionComboBox.setModel(new javax.swing.DefaultComboBoxModel(akela.getSections()));
        sectionComboBox.setSelectedItem(null);
        Table.setColumnWidths(invoiceTable, new int[] {10, -1, 30, 10, 10});
        invoiceTable.getTableHeader().setVisible(false);
        invoiceTable.getColumnModel().getColumn(INVOICE_CHECK_COLUMN).setCellRenderer(new ItemCheckCellRenderer());
        invoiceTable.getColumnModel().getColumn(INVOICE_DESCRIPTION_COLUMN).setCellRenderer(new ItemCellRenderer());
        invoiceTable.getColumnModel().getColumn(INVOICE_SUM_COLUMN).setCellRenderer(new SumCellRenderer());
        invoiceTable.getColumnModel().getColumn(INVOICE_SUM_COLUMN).setCellEditor(new RealNumberCell(2));
        ButtonCellRenderer addButton = new ButtonCellRenderer("+", new CellButtonActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceItemRow itemRow = invoiceItemRows.get(rowIndex);
                InvoiceItemRow newItemRow = new InvoiceItemRow();
                newItemRow.check = itemRow.check;
                newItemRow.member = itemRow.member;
                newItemRow.description = "";
                invoiceItemRows.add(rowIndex + 1, newItemRow);
                INVOICE_TABLE_MODEL.fireTableDataChanged();
                enableInvoiceButton();
            }
        });
        invoiceTable.getColumnModel().getColumn(INVOICE_ADD_BUTTON_COLUMN).setCellRenderer(addButton);
        invoiceTable.getColumnModel().getColumn(INVOICE_ADD_BUTTON_COLUMN).setCellEditor(addButton);
        ButtonCellRenderer removeButton = new ButtonCellRenderer("-", new CellButtonActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InvoiceItemRow removedRow = invoiceItemRows.remove(rowIndex);
                int endIndex = rowIndex;
                if (removedRow.description == null) {
                    members.remove(removedRow.member);
                    ArrayList<InvoiceItemRow> rowsToRemove = new ArrayList<InvoiceItemRow>();
                    for (InvoiceItemRow row : invoiceItemRows) {
                        if (row.member == removedRow.member) {
                            rowsToRemove.add(row);
                            endIndex++;
                        }
                    }
                    invoiceItemRows.removeAll(rowsToRemove);
                }
                INVOICE_TABLE_MODEL.fireTableRowsDeleted(rowIndex, endIndex);
                enableInvoiceButton();
            }
        });
        invoiceTable.getColumnModel().getColumn(INVOICE_REMOVE_BUTTON_COLUMN).setCellRenderer(removeButton);
        invoiceTable.getColumnModel().getColumn(INVOICE_REMOVE_BUTTON_COLUMN).setCellEditor(removeButton);
        initializeTabbedPane(mainTabbedPane);
        initializeComboBox(sectionComboBox);
        initializeTextComponent(annualDueFormattedTextField);
        initializeTextComponent(accountNumberFormattedTextField);
        initializeTextComponent(accountHolderFormattedTextField);
        initializeTextComponent(contactNameFormattedTextField);
        initializeTextComponent(contactEmailFormattedTextField);
        initializeTextComponent(contactPhoneFormattedTextField);
        initializeTextComponent(additionsTextArea);
    }
    
    
    protected void closing() {
        setSetting(mainTabbedPane.getName(), mainTabbedPane.getSelectedIndex());
        storeComboBox(sectionComboBox);
        storeTextComponent(annualDueFormattedTextField);
        storeTextComponent(accountNumberFormattedTextField);
        storeTextComponent(accountHolderFormattedTextField);
        storeTextComponent(contactNameFormattedTextField);
        storeTextComponent(contactEmailFormattedTextField);
        storeTextComponent(contactPhoneFormattedTextField);
        storeTextComponent(additionsTextArea);
    }
    
    
    private void initializeTabbedPane(javax.swing.JTabbedPane tabbedPane) {
        Object setting = getSetting(tabbedPane.getName());
        if (setting instanceof Integer) {
            tabbedPane.setSelectedIndex((Integer) setting);
        }
    }
    
    
    private void initializeComboBox(javax.swing.JComboBox comboBox) {
        Object setting = getSetting(comboBox.getName());
        if (setting != null) {
            String string = setting.toString();
            for (int i = 0; i < comboBox.getItemCount(); i++) {
                if (string.equals(comboBox.getItemAt(i).toString())) {
                    comboBox.setSelectedIndex(i);
                    break; // No need to complete iteration.
                }
            }
        }
    }
    
    
    private void initializeTextComponent(javax.swing.text.JTextComponent component) {
        Object setting = getSetting(component.getName());
        if (setting != null) {
            component.setText(setting.toString());
        }
    }
    
    
    private void storeComboBox(javax.swing.JComboBox comboBox) {
        Object selectedItem = comboBox.getSelectedItem();
        if (selectedItem != null) {
            setSetting(comboBox.getName(), selectedItem.toString());
        }
    }
    
    
    private void storeTextComponent(javax.swing.text.JTextComponent component) {
        setSetting(component.getName(), component.getText());
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainTabbedPane = new javax.swing.JTabbedPane();
        membersOverviewTab = new javax.swing.JPanel();
        membersScrollPane = new javax.swing.JScrollPane();
        membersTable = new javax.swing.JTable();
        membersOverviewButtonPanel = new javax.swing.JPanel();
        createMemberListButton = new javax.swing.JButton();
        healthFormTab = new javax.swing.JPanel();
        healthFormOptionsPanel = new javax.swing.JPanel();
        multipleCheckBox = new javax.swing.JCheckBox();
        signedCheckBox = new javax.swing.JCheckBox();
        sectionsScrollPane = new javax.swing.JScrollPane();
        sectionsList = new javax.swing.JList();
        healthFormButtonPanel = new javax.swing.JPanel();
        pdfButton = new javax.swing.JButton();
        htmlButton = new javax.swing.JButton();
        invoiceTab = new javax.swing.JPanel();
        invoiceControlPanel = new javax.swing.JPanel();
        sectionComboBox = new javax.swing.JComboBox();
        annualDueLabel = new javax.swing.JLabel();
        annualDueFormattedTextField = new javax.swing.JFormattedTextField();
        annualDuesButton = new javax.swing.JButton();
        createInvoicesButton = new javax.swing.JButton();
        invoiceScrollPane = new javax.swing.JScrollPane();
        invoiceTable = new javax.swing.JTable();
        accountPanel = new javax.swing.JPanel();
        accountNumberLabel = new javax.swing.JLabel();
        accountNumberFormattedTextField = new javax.swing.JFormattedTextField();
        accountHolderLabel = new javax.swing.JLabel();
        accountHolderFormattedTextField = new javax.swing.JFormattedTextField();
        subjectLabel = new javax.swing.JLabel();
        subjectFormattedTextField = new javax.swing.JFormattedTextField();
        contactNameLabel = new javax.swing.JLabel();
        contactNameFormattedTextField = new javax.swing.JFormattedTextField();
        contactPhoneLabel = new javax.swing.JLabel();
        contactPhoneFormattedTextField = new javax.swing.JFormattedTextField();
        contactEmailLabel = new javax.swing.JLabel();
        contactEmailFormattedTextField = new javax.swing.JFormattedTextField();
        separator1 = new javax.swing.JSeparator();
        additionsLabel = new javax.swing.JLabel();
        additionsScrollPane = new javax.swing.JScrollPane();
        additionsTextArea = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Akela");
        setName("name = Akela"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                form_windowClosing(evt);
            }
        });

        mainTabbedPane.setName("MainTabbedPane"); // NOI18N

        membersOverviewTab.setLayout(new java.awt.BorderLayout());

        membersTable.setModel(MEMBERS_TABLE_MODEL);
        membersTable.setFillsViewportHeight(true);
        membersScrollPane.setViewportView(membersTable);

        membersOverviewTab.add(membersScrollPane, java.awt.BorderLayout.CENTER);

        createMemberListButton.setText("Create member list");
        createMemberListButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createMemberListButton_actionPerformed(evt);
            }
        });
        membersOverviewButtonPanel.add(createMemberListButton);

        membersOverviewTab.add(membersOverviewButtonPanel, java.awt.BorderLayout.PAGE_END);

        mainTabbedPane.addTab("Members Overview", membersOverviewTab);

        healthFormTab.setLayout(new java.awt.BorderLayout());

        multipleCheckBox.setText("Multiple documents");
        multipleCheckBox.setBorderPainted(true);
        healthFormOptionsPanel.add(multipleCheckBox);

        signedCheckBox.setText("Signed");
        signedCheckBox.setBorderPainted(true);
        healthFormOptionsPanel.add(signedCheckBox);

        healthFormTab.add(healthFormOptionsPanel, java.awt.BorderLayout.NORTH);

        sectionsScrollPane.setPreferredSize(new java.awt.Dimension(200, 200));
        sectionsScrollPane.setViewportView(sectionsList);

        healthFormTab.add(sectionsScrollPane, java.awt.BorderLayout.CENTER);

        pdfButton.setText("Create PDF");
        pdfButton.setActionCommand("pdf");
        pdfButton.setEnabled(false);
        pdfButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pdfButtonActionPerformed(evt);
            }
        });
        healthFormButtonPanel.add(pdfButton);

        htmlButton.setText("Create HTML");
        htmlButton.setEnabled(false);
        htmlButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                htmlButtonActionPerformed(evt);
            }
        });
        healthFormButtonPanel.add(htmlButton);

        healthFormTab.add(healthFormButtonPanel, java.awt.BorderLayout.SOUTH);

        mainTabbedPane.addTab("Health Forms", healthFormTab);

        invoiceTab.setLayout(new java.awt.BorderLayout());

        invoiceControlPanel.setVerifyInputWhenFocusTarget(false);

        sectionComboBox.setName("SectionComboBox"); // NOI18N
        sectionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sectionComboBox_actionPerformed(evt);
            }
        });
        invoiceControlPanel.add(sectionComboBox);

        annualDueLabel.setText("Annual due: €");
        invoiceControlPanel.add(annualDueLabel);

        annualDueFormattedTextField.setMinimumSize(new java.awt.Dimension(50, 28));
        annualDueFormattedTextField.setName("AnnualDue"); // NOI18N
        annualDueFormattedTextField.setPreferredSize(new java.awt.Dimension(50, 28));
        invoiceControlPanel.add(annualDueFormattedTextField);

        annualDuesButton.setText("Insert Annual Dues Items");
        annualDuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                annualDuesButton_actionPerformed(evt);
            }
        });
        invoiceControlPanel.add(annualDuesButton);

        createInvoicesButton.setText("Create Invoices");
        createInvoicesButton.setEnabled(false);
        createInvoicesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createInvoicesButton_actionPerformed(evt);
            }
        });
        invoiceControlPanel.add(createInvoicesButton);

        invoiceTab.add(invoiceControlPanel, java.awt.BorderLayout.PAGE_START);

        invoiceTable.setModel(INVOICE_TABLE_MODEL);
        invoiceTable.setShowGrid(false);
        invoiceScrollPane.setViewportView(invoiceTable);

        invoiceTab.add(invoiceScrollPane, java.awt.BorderLayout.CENTER);

        accountNumberLabel.setText("Account Number");

        accountNumberFormattedTextField.setName("AccountNumber"); // NOI18N
        accountNumberFormattedTextField.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                accountNumberFormattedTextField_caretUpdate(evt);
            }
        });
        accountNumberFormattedTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                accountNumberFormattedTextField_focusLost(evt);
            }
        });
        accountNumberFormattedTextField.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                accountNumberFormattedTextField_inputMethodTextChanged(evt);
            }
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                accountNumberFormattedTextField_caretPositionChanged(evt);
            }
        });

        accountHolderLabel.setText("Account Holder");

        accountHolderFormattedTextField.setName("AccountHolder"); // NOI18N

        subjectLabel.setText("Subject");

        contactNameLabel.setText("Contact");

        contactNameFormattedTextField.setName("ContactName"); // NOI18N

        contactPhoneLabel.setText("Phone Number");

        contactPhoneFormattedTextField.setName("ContactPhone"); // NOI18N

        contactEmailLabel.setText("E-mail");

        contactEmailFormattedTextField.setName("ContactEmail"); // NOI18N

        separator1.setOrientation(javax.swing.SwingConstants.VERTICAL);

        additionsLabel.setText("Additions");

        additionsTextArea.setColumns(20);
        additionsTextArea.setRows(5);
        additionsTextArea.setName("Additions"); // NOI18N
        additionsScrollPane.setViewportView(additionsTextArea);

        org.jdesktop.layout.GroupLayout accountPanelLayout = new org.jdesktop.layout.GroupLayout(accountPanel);
        accountPanel.setLayout(accountPanelLayout);
        accountPanelLayout.setHorizontalGroup(
            accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(accountPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(contactPhoneLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contactNameLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(accountHolderLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(subjectLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(contactEmailLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 114, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(accountNumberLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, contactPhoneFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(contactNameFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(accountHolderFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(org.jdesktop.layout.GroupLayout.TRAILING, contactEmailFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(subjectFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 86, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(accountNumberFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 152, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(separator1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(additionsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                    .add(additionsLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        accountPanelLayout.linkSize(new java.awt.Component[] {accountHolderLabel, accountNumberLabel, contactEmailLabel, contactNameLabel, contactPhoneLabel, subjectLabel}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        accountPanelLayout.linkSize(new java.awt.Component[] {accountHolderFormattedTextField, accountNumberFormattedTextField, contactEmailFormattedTextField, contactNameFormattedTextField, contactPhoneFormattedTextField, subjectFormattedTextField}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        accountPanelLayout.setVerticalGroup(
            accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(accountPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(accountPanelLayout.createSequentialGroup()
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(accountNumberFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(accountNumberLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(accountHolderFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(accountHolderLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(subjectFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(subjectLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(contactNameFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(contactNameLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(contactPhoneFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(contactPhoneLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(accountPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(contactEmailFormattedTextField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(contactEmailLabel)))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, accountPanelLayout.createSequentialGroup()
                        .add(additionsLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(additionsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, separator1)
        );

        invoiceTab.add(accountPanel, java.awt.BorderLayout.SOUTH);

        mainTabbedPane.addTab("Invoices", invoiceTab);

        getContentPane().add(mainTabbedPane, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void createInvoicesButton_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createInvoicesButton_actionPerformed
        Invoice invoice = null;
        for (InvoiceItemRow row : invoiceItemRows) {
            if (row.check) {
                if (row.description == null) {
                    if (invoice != null) {
                        invoice.createPdf();
                    }
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.MONTH, Calendar.DECEMBER);
                    calendar.set(Calendar.DATE, 31);
                    invoice = new Invoice(dataPath, "Contributie " + row.member.fullName());
                    int year = calendar.get(Calendar.YEAR);
                    String term = Integer.toString(year);
                    if (calendar.get(Calendar.MONTH) < Calendar.AUGUST) {
                        term = Integer.toString(year - 1) + " / " + term;
                    }
                    else {
                        term = term + " / " + Integer.toString(year + 1);
                    }
                    invoice.setTitle("Contributie " + sectionComboBox.getSelectedItem().toString() + " " + term);
                    invoice.setPaymentDeadline(calendar.getTimeInMillis());
                    invoice.setAccountId(accountNumberFormattedTextField.getText());
                    invoice.setAccountHolder(accountHolderFormattedTextField.getText());
                    invoice.setSubject(row.member.fullName() + " " + term);
                    invoice.setAdditions(additionsTextArea.getText() + "\n\n" + DEFAULT_INVOICE_ADDITION);
                    invoice.setContactName(contactNameFormattedTextField.getText());
                    invoice.setContactTelephone(contactPhoneFormattedTextField.getText());
                    invoice.setContactEmail(contactEmailFormattedTextField.getText());
                }
                else if (row.sum != null) {
                    invoice.addItem(row.description, row.sum);
                }
            }
        }
        if (invoice != null) {
            invoice.createPdf();
        }
    }//GEN-LAST:event_createInvoicesButton_actionPerformed

    
    private void sectionComboBox_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sectionComboBox_actionPerformed
        invoiceItemRows.clear();
        members.clear();
        INVOICE_TABLE_MODEL.fireTableDataChanged();
        Section section = (Section) sectionComboBox.getSelectedItem();
        if (section != null) {
            for (Member member : akela.getMembers(section, Akela.YOUTH_MEMBER_FUNCTION)) {
                members.add(member);
                InvoiceItemRow row = new InvoiceItemRow();
                row.member = member;
                invoiceItemRows.add(row);
            }
            INVOICE_TABLE_MODEL.fireTableDataChanged();
            enableInvoiceButton();
        }
    }//GEN-LAST:event_sectionComboBox_actionPerformed

    
    private void htmlButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_htmlButtonActionPerformed
        HealthForm healthForm = new HealthForm(dataPath);
        for (Object section : sectionsList.getSelectedValues()) {
            java.util.List<Member> memberList = Arrays.asList(akela.getMembers((Section) section, Akela.YOUTH_MEMBER_FUNCTION));
            if (memberList.isEmpty()) {
                JOptionPane.showConfirmDialog(this, "No members found for section.", "No members", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                if (multipleCheckBox.isSelected()) {
                    healthForm.createMultipleHtmls(memberList, signedCheckBox.isSelected());
                }
                else {
                    healthForm.createHtml(memberList, (Section) section, signedCheckBox.isSelected());
                }
            }
        }
    }//GEN-LAST:event_htmlButtonActionPerformed

    
    private void pdfButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pdfButtonActionPerformed
        HealthForm healthForm = new HealthForm(dataPath);
        for (Object section : sectionsList.getSelectedValues()) {
            java.util.List<Member> memberList = Arrays.asList(akela.getMembers((Section) section, Akela.YOUTH_MEMBER_FUNCTION));
            if (memberList.isEmpty()) {
                JOptionPane.showConfirmDialog(this, "No members found for section.", "No members", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
            }
            else {
                if (multipleCheckBox.isSelected()) {
                    healthForm.createMultiplePdfs(memberList, signedCheckBox.isSelected());
                }
                else {
                    healthForm.createPdf(memberList, (Section) section, signedCheckBox.isSelected());
                }
            }
        }
    }//GEN-LAST:event_pdfButtonActionPerformed

    
    private void createMemberListButton_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createMemberListButton_actionPerformed
        MemberList list = new MemberList(dataPath);
        list.createPdf(Arrays.asList(akela.getMembers()));
    }//GEN-LAST:event_createMemberListButton_actionPerformed

    
    private void accountNumberFormattedTextField_focusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_accountNumberFormattedTextField_focusLost
        System.out.println("focusLost: " + evt.paramString());
    }//GEN-LAST:event_accountNumberFormattedTextField_focusLost

    
    private void accountNumberFormattedTextField_caretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_accountNumberFormattedTextField_caretUpdate
        System.out.println("caretUpdate: " + evt.toString());
    }//GEN-LAST:event_accountNumberFormattedTextField_caretUpdate

    
    private void accountNumberFormattedTextField_caretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_accountNumberFormattedTextField_caretPositionChanged
        System.out.println("caretPositionChanged: " + evt.paramString());
    }//GEN-LAST:event_accountNumberFormattedTextField_caretPositionChanged

    
    private void accountNumberFormattedTextField_inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_accountNumberFormattedTextField_inputMethodTextChanged
        System.out.println("inputMethodTextChanged: " + evt.paramString());
    }//GEN-LAST:event_accountNumberFormattedTextField_inputMethodTextChanged

    
    private void annualDuesButton_actionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_annualDuesButton_actionPerformed
        try {
            java.util.Scanner scanner = new java.util.Scanner(annualDueFormattedTextField.getText());
            double due = scanner.nextDouble();
            annualDueFormattedTextField.setBackground(Color.WHITE);
            ArrayList<InvoiceItemRow> memberRows = new ArrayList<InvoiceItemRow>();
            for (InvoiceItemRow row : invoiceItemRows) {
               if (row.check && row.description == null) {
                   memberRows.add(row);
               }
            }
            for (InvoiceItemRow memberRow : memberRows) {
                InvoiceItemRow annualDueRow = new InvoiceItemRow();
                annualDueRow.member = memberRow.member;
                annualDueRow.description = "Jaarcontributie";
                annualDueRow.sum = due;
                int index = invoiceItemRows.indexOf(memberRow) + 1;
                invoiceItemRows.add(index, annualDueRow);
            }
            INVOICE_TABLE_MODEL.fireTableDataChanged();
            enableInvoiceButton();
        }
        catch (NumberFormatException ex) {
            annualDueFormattedTextField.setBackground(Color.RED);
        }
    }//GEN-LAST:event_annualDuesButton_actionPerformed

    
    private void form_windowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_form_windowClosing
//        try {
//            storeSettings();
//        }
//        catch (Exception ex) {
//            ex.printStackTrace(System.err);
//        }
    }//GEN-LAST:event_form_windowClosing

    
    private void enableInvoiceButton() {
        boolean enabled =
            ! accountNumberFormattedTextField.getText().isEmpty() &&
            ! accountHolderFormattedTextField.getText().isEmpty() &&
            ! contactNameFormattedTextField.getText().isEmpty() &&
            (! contactPhoneFormattedTextField.getText().isEmpty() || ! contactEmailFormattedTextField.getText().isEmpty());
        if (enabled) {
            boolean allUnchecked = true;
            for (InvoiceItemRow row : invoiceItemRows) {
                if (row.check && row.description != null && row.description.isEmpty()) {
                    enabled = false;
                }
                if (row.check) {
                    allUnchecked = false;
                }
            }
            enabled &= ! allUnchecked;
        }
        createInvoicesButton.setEnabled(enabled);
    }
    

    private int age(Member member) {
        int age = -1;
        java.util.List birthDateList = member.getFeature("Geboortedatum");
        if (birthDateList != null && ! birthDateList.isEmpty()) {
            String birthDateString = birthDateList.get(0).toString();
            java.text.DateFormat format = new java.text.SimpleDateFormat("yyyy-MM-dd");
            try {
                Calendar birthDate = Calendar.getInstance();
                birthDate.setTime(format.parse(birthDateString));
                Calendar now = Calendar.getInstance();
                age = now.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
                    age--;
                }
            }
            catch (java.text.ParseException ex) {
                ex.printStackTrace(System.err);
            }
        }
        return age;
    }
    
    
    private String sections(Member member) {
        String string = "";
        Function[] functions = akela.getFunctions(member);
        for (int i = 0; i < functions.length; i++) {
            if (i > 0) {
                string += "; ";
            }
            string += functions[i].getSection();
        }
        return string;
    }
    
    
    private Color invoiceRowColor(int rowIndex) {
        InvoiceItemRow row = invoiceItemRows.get(rowIndex);
        int memberIndex = members.indexOf(row.member);
        return (memberIndex % 2 == 0) ? new Color(0, 210, 252) : Color.CYAN;
    }


    private class SectionsListListener implements javax.swing.event.ListSelectionListener {

        public void valueChanged(ListSelectionEvent e) {
            boolean enabled = sectionsList.getSelectedIndices().length > 0;
            pdfButton.setEnabled(enabled);
            htmlButton.setEnabled(enabled);
        }
    }
    
    
    private class InvoiceItemRow {
        
        void setCheck(Boolean check) {
            this.check = check;
            if (description == null) {
                for (InvoiceItemRow row : invoiceItemRows) {
                    if (row != this && row.member == member) {
                        row.check = check;
                    }
                }
            }
            else if (check){
                for (InvoiceItemRow row : invoiceItemRows) {
                    if (row.member == member && row.description == null) {
                        row.check = check;
                    }
                }                
            }
            INVOICE_TABLE_MODEL.fireTableDataChanged();
        }
        
        Boolean check = Boolean.TRUE;
        Member member;
        String description;
        Double sum;
    }


    class ItemCheckCellRenderer extends javax.swing.JCheckBox implements javax.swing.table.TableCellRenderer {
        
        ItemCheckCellRenderer() {
        }

        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setSelected(Boolean.TRUE.equals(value));
            setMargin(new Insets(0, 16, 0, 0));
            this.setHorizontalAlignment(javax.swing.JCheckBox.CENTER);
            setIconTextGap(0);
            setBackground(invoiceRowColor(row));
            return this;
        }
    
    }
    
    private class ItemCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBackground(invoiceRowColor(row));
            setForeground(Color.BLACK);
            InvoiceItemRow itemRow = invoiceItemRows.get(row);
            if (itemRow.description == null) {
                setText("<html><b>" + value.toString() + "</b></html>");
            }
            return this;
        }
        
    }
    
    
    private class SumCellRenderer extends RealNumberCell {
        
        SumCellRenderer() {
            super(2);
        }
        
        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            field.setBackground(invoiceRowColor(row));
            field.setForeground(Color.BLACK);
            return field;
        }
        
    }
    
    
    private abstract class CellButtonActionListener implements java.awt.event.ActionListener {
        int rowIndex;
    }
    
    
    private class ButtonCellRenderer extends javax.swing.table.DefaultTableCellRenderer
        implements javax.swing.table.TableCellEditor {
        
        
        ButtonCellRenderer(String text, CellButtonActionListener actionListener) {
            this.text = text;
            this.actionListener = actionListener;
        }
        

        public java.awt.Component getTableCellRendererComponent(javax.swing.JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            javax.swing.JButton button = new javax.swing.JButton(text);
            button.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    System.out.println("Not supported yet.");
                }
            });
            return button;
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
             actionListener.rowIndex = row;
             javax.swing.JButton button = new javax.swing.JButton(text);
             button.addActionListener(actionListener);
             return button;
        }

        public Object getCellEditorValue() {
            return text;
        }

        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        public boolean stopCellEditing() {
            return true;
        }

        public void cancelCellEditing() {
            
        }

        public void addCellEditorListener(CellEditorListener l) {
            
        }

        public void removeCellEditorListener(CellEditorListener l) {
            
        }
        
        private final String text;
        private final CellButtonActionListener actionListener;
        
    }

    
    private AbstractTableModel MEMBERS_TABLE_MODEL = new AbstractTableModel() {

        public int getRowCount() {
            return (akela != null) ? akela.getMembers().length : 0;
        }

        public int getColumnCount() {
            return NUMBER_OF_COLUMNS;
        }

        public Class getColumnClass(int column) {
            switch (column) {
                case AGE_COLUMN : return Integer.class;
                default         : return String.class;
            }
        }

        public String getColumnName(int column) {
            switch (column) {
                case FIRST_NAME_COLUMN : return "First Name";
                case SURNAME_COLUMN    : return "Surname";
                case ADDRESS_COLUMN    : return "Address";
                case BIRTH_DATE_COLUMN : return "Date of Birth";
                case AGE_COLUMN        : return "Age";
                case SECTION_COLUMN    : return "Section";
                default                : return null;
            }
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            Member member = akela.getMembers()[rowIndex];
            switch (columnIndex) {
                case FIRST_NAME_COLUMN : return member.getFirstName();
                case SURNAME_COLUMN    : return member.alphabeticalSurname();
                case ADDRESS_COLUMN    : return member.getFeature("Adres").toString();
                case BIRTH_DATE_COLUMN : return member.getFeature("Geboortedatum").toString();
                case AGE_COLUMN        : return age(member);
                case SECTION_COLUMN    : return sections(member);
                default                : return null;
            }
        }

        private static final int FIRST_NAME_COLUMN = 0;
        private static final int SURNAME_COLUMN    = 1;
        private static final int ADDRESS_COLUMN    = 2;
        private static final int BIRTH_DATE_COLUMN = 3;
        private static final int AGE_COLUMN        = 4;
        private static final int SECTION_COLUMN    = 5;
        private static final int NUMBER_OF_COLUMNS = 6;

    };
    
    
    private AbstractTableModel INVOICE_TABLE_MODEL = new AbstractTableModel() {

        public int getRowCount() {
            return (invoiceItemRows != null) ? invoiceItemRows.size() : 0;
        }

        public int getColumnCount() {
            return INVOICE_COLUMN_COUNT;
        }
        
        public Class getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case INVOICE_CHECK_COLUMN : return Boolean.class;
                case INVOICE_SUM_COLUMN   : return Double.class;
                default                   : return String.class;
            }
        }
        
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            InvoiceItemRow row = invoiceItemRows.get(rowIndex);
            switch (columnIndex) {
                case INVOICE_DESCRIPTION_COLUMN    :
                case INVOICE_SUM_COLUMN            : return row.description != null;
                case INVOICE_CHECK_COLUMN          :
                case INVOICE_ADD_BUTTON_COLUMN     : return true;
                case INVOICE_REMOVE_BUTTON_COLUMN  : return true;
                default                            : return false;
            }
        }
        
        public Object getValueAt(int rowIndex, int columnIndex) {
            InvoiceItemRow row = invoiceItemRows.get(rowIndex);
            switch (columnIndex) {
                case INVOICE_CHECK_COLUMN       : return row.check;
                case INVOICE_DESCRIPTION_COLUMN : return (row.description != null) ? row.description : row.member.fullName();
                case INVOICE_SUM_COLUMN         : return row.sum;
                default                         : return null;
            }
        }
        
        public void setValueAt(Object value, int rowIndex, int columnIndex) {
            InvoiceItemRow row = invoiceItemRows.get(rowIndex);
            switch (columnIndex) {
                case INVOICE_CHECK_COLUMN       : row.setCheck((Boolean) value); break;
                case INVOICE_DESCRIPTION_COLUMN : if (row.description != null) { row.description = value.toString(); } break;
                case INVOICE_SUM_COLUMN         : row.sum = (Double) value; break;
            }
            enableInvoiceButton();
        }
        
    };
    
    
    private final ArrayList<Member> members = new ArrayList<Member>();
    private final ArrayList<InvoiceItemRow> invoiceItemRows = new ArrayList<InvoiceItemRow>();
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField accountHolderFormattedTextField;
    private javax.swing.JLabel accountHolderLabel;
    private javax.swing.JFormattedTextField accountNumberFormattedTextField;
    private javax.swing.JLabel accountNumberLabel;
    private javax.swing.JPanel accountPanel;
    private javax.swing.JLabel additionsLabel;
    private javax.swing.JScrollPane additionsScrollPane;
    private javax.swing.JTextArea additionsTextArea;
    private javax.swing.JFormattedTextField annualDueFormattedTextField;
    private javax.swing.JLabel annualDueLabel;
    private javax.swing.JButton annualDuesButton;
    private javax.swing.JFormattedTextField contactEmailFormattedTextField;
    private javax.swing.JLabel contactEmailLabel;
    private javax.swing.JFormattedTextField contactNameFormattedTextField;
    private javax.swing.JLabel contactNameLabel;
    private javax.swing.JFormattedTextField contactPhoneFormattedTextField;
    private javax.swing.JLabel contactPhoneLabel;
    private javax.swing.JButton createInvoicesButton;
    private javax.swing.JButton createMemberListButton;
    private javax.swing.JPanel healthFormButtonPanel;
    private javax.swing.JPanel healthFormOptionsPanel;
    private javax.swing.JPanel healthFormTab;
    private javax.swing.JButton htmlButton;
    private javax.swing.JPanel invoiceControlPanel;
    private javax.swing.JScrollPane invoiceScrollPane;
    private javax.swing.JPanel invoiceTab;
    private javax.swing.JTable invoiceTable;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JPanel membersOverviewButtonPanel;
    private javax.swing.JPanel membersOverviewTab;
    private javax.swing.JScrollPane membersScrollPane;
    private javax.swing.JTable membersTable;
    private javax.swing.JCheckBox multipleCheckBox;
    private javax.swing.JButton pdfButton;
    private javax.swing.JComboBox sectionComboBox;
    private javax.swing.JList sectionsList;
    private javax.swing.JScrollPane sectionsScrollPane;
    private javax.swing.JSeparator separator1;
    private javax.swing.JCheckBox signedCheckBox;
    private javax.swing.JFormattedTextField subjectFormattedTextField;
    private javax.swing.JLabel subjectLabel;
    // End of variables declaration//GEN-END:variables
        
    private Akela akela;
    private String dataPath = "";
    
    
    private static final int INVOICE_CHECK_COLUMN         = 0;
    private static final int INVOICE_DESCRIPTION_COLUMN   = 1;
    private static final int INVOICE_SUM_COLUMN           = 2;
    private static final int INVOICE_ADD_BUTTON_COLUMN    = 3;
    private static final int INVOICE_REMOVE_BUTTON_COLUMN = 4;

    private static final int INVOICE_COLUMN_COUNT = 5;
    
    
    private static final String DEFAULT_INVOICE_ADDITION = "Door deze factuur te betalen verklaart u tevens het bijgevoegd huishoudelijk reglement van Scouting Nederland gelezen te hebben.";

}
