/*
** Copyright © Bart Kampers
*/

package bka.scouting.swing;

import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


class Invoice {
    
    
    Invoice(String dataPath, String fileName) {
        this.dataPath = dataPath;
        this.fileName = fileName;
    }
    

    void setTitle(String title) {
        this.title = title;
    }
    
    
    void setDate(long date) {
        this.date = date;
    }
     
    
    void addItem(String description, double sum) {
        Item item = new Item();
        item.description = description;
        item.sum = sum;
        items.add(item);
        total += sum;
    }

    public void setPaymentDeadline(long paymentDeadline) {
        this.paymentDeadline = paymentDeadline;
    }

    
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }

    
    public void setSubject(String subject) {
        this.subject = subject;
    }

    
    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    
    public void setContactTelephone(String contactTelephone) {
        this.contactTelephone = contactTelephone;
    }

    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    
    void setAdditions(String additions) {
        this.additions = additions;
    }
    
    void createPdf() {
        try {
            PdfDocument document = new PdfDocument(dataPath + fileName);
            document.setDrawFooter(false);
            if (title != null) {
                document.setHeaderText(title);
            }
            java.net.URL logoUrl = ClassLoader.getSystemClassLoader().getResource("images/SCOUTING.jpg");
            document.setHeaderImage(logoUrl, 0.75f);
            document.open(fileName);
            document.addPage();
            document.moveToY(725f);
            document.addLine("Datum: " + format.format(new java.util.Date(date)));
            document.addWhiteSpace();
            document.addWhiteSpace();
            document.addTable(new ItemsModel());
            document.addWhiteSpace();
            document.addWhiteSpace();
            document.addTable(new InformationModel());
            if (additions != null) {
                document.addWhiteSpace();
                document.addParagraph(additions);
            }
            if (contactName != null) {
                document.addWhiteSpace();
                document.addParagraph("Indien u vragen heeft neemt u dan contact op met " + contactName + ".");
                document.addWhiteSpace();
                if (contactTelephone != null && ! contactTelephone.isEmpty()) {
                    document.addLine("T: " + contactTelephone);
                }
                if (contactEmail != null && ! contactEmail.isEmpty()) {
                    document.addLine("E: " + contactEmail);
                }
            }
            document.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MemberList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    private class Item {
        String description;
        double sum;
    }
    
    
    private class ItemsModel extends TableModel {

        public int rowCount() {
            return items.size() + 2;
        }

        public int columnCount() {
            return COLUMN_COUNT;
        }

        public float[] relativeColumnWidths() {
            return new float[] { 0.9f, 0.1f };
        }

        public String value(int row, int column) {
            if (row < items.size()) {
                Item item = items.get(row);
                switch (column) {
                    case DESCRIPTION_COLUMN: {
                        return item.description;
                    }
                    case SUM_COLUMN: {
                        Formatter formatter = new Formatter();
                        formatter.format("%.2f", item.sum);
                        return formatter.toString();
                    }
                }
            }
            else if (row == rowCount() - 1) {
                switch (column) {
                    case DESCRIPTION_COLUMN: {
                        return "Totaal in euro's";
                    }
                    case SUM_COLUMN: {
                        Formatter formatter = new Formatter();
                        formatter.format("%.2f", total);
                        return formatter.toString();
                    }
                }
                
            }
            return "";
        }

        public int horizontalAlignment(int row, int column) {
            int rowCount = rowCount();
            if (column == DESCRIPTION_COLUMN && row < rowCount - 1) {
                return com.itextpdf.text.pdf.PdfPCell.ALIGN_LEFT;
            }
            else {
                return com.itextpdf.text.pdf.PdfPCell.ALIGN_RIGHT;
            }
        }

        
        public int border(int row, int column) {
            if (column == SUM_COLUMN && row == rowCount() - 1) {
                return com.itextpdf.text.pdf.PdfPCell.TOP;
            }
            else {
                return com.itextpdf.text.pdf.PdfPCell.NO_BORDER;
            }
    }

        
        private static final int DESCRIPTION_COLUMN = 0;
        private static final int SUM_COLUMN         = 1;
        private static final int COLUMN_COUNT = 2;
        
    }
    

    private class InformationModel extends TableModel {

        public int rowCount() {
            return ROW_COUNT;
        }

        public int columnCount() {
            return COLUMN_COUNT;
        }

        public float[] relativeColumnWidths() {
            return new float[] { 0.2f, 0.8f };
        }

        public String value(int row, int column) {
            switch (column) {
                case DESCRIPTION_COLUMN:
                    switch (row) {
                        case DUE_DATE_ROW       : return "Te voldoen voor";
                        case ACCOUNT_NUMBER_ROW : return "Op rekening";
                        case ACCOUNT_HOLDER_ROW : return "Ten name van";
                        case SUBJECT_ROW        : return "O.v.v";
                    }
                    break;
                case DATA_COLUMN:
                    switch (row) {
                        case DUE_DATE_ROW       : return format.format(new java.util.Date(paymentDeadline));
                        case ACCOUNT_NUMBER_ROW : return accountId;
                        case ACCOUNT_HOLDER_ROW : return accountHolder;
                        case SUBJECT_ROW        : return subject;
                    }
                    break;
            }
            return null; // should not occur
        }
        
        private static final int DESCRIPTION_COLUMN = 0;
        private static final int DATA_COLUMN        = 1;
        private static final int COLUMN_COUNT = 2;
        
        private static final int DUE_DATE_ROW       = 0;
        private static final int ACCOUNT_NUMBER_ROW = 1;
        private static final int ACCOUNT_HOLDER_ROW = 2;
        private static final int SUBJECT_ROW        = 3;
        private static final int ROW_COUNT = 4;        
        
    }
    

    private final String dataPath;
    private final String fileName;
    
    private String title;
    private long date = System.currentTimeMillis();
    private ArrayList<Item> items = new ArrayList<Item>();
    private double total = 0.0;
    private long paymentDeadline;
    private String accountId;
    private String accountHolder;
    private String subject;
    private String additions;
    private String contactName;
    private String contactTelephone;
    private String contactEmail;
    
    
    private java.text.DateFormat format = DateFormat.getDateInstance(DateFormat.LONG);

    
}
