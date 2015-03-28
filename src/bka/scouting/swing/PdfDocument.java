/*
** Copyright © Bart Kampers
*/

package bka.scouting.swing;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;
import java.util.*;


class PdfDocument {


    public static final int ALIGN_LEFT   = Element.ALIGN_LEFT;
    public static final int ALIGN_CENTER = Element.ALIGN_CENTER;
    public static final int ALIGN_RIGHT  = Element.ALIGN_RIGHT;


    PdfDocument(String fileName) {
        this.fileName = fileName;
        if (! this.fileName.endsWith(EXTENSION)) {
            this.fileName += EXTENSION;
        }
    }


    void setHeaderImage(java.net.URL url, float scale) throws Exception {
        if (url != null) {
            headerImage = new Jpeg(url);
            imageHeaderScale = scale;
        }
        else {
            headerImage = null;
        }
    }


    void setHeaderImage(java.net.URL url) throws Exception {
        setHeaderImage(url, 1.0f);
    }


    void setHeaderText(String headerText) {
        this.headerText = headerText;
    }


    void setSubheaderText(String subheaderText) {
        this.subheaderText = subheaderText;
    }


    void setDrawFooter(boolean drawFooter) {
        this.drawFooter = drawFooter;
    }


    void open(String title) throws Exception {
        document = new Document(PageSize.A4);
        document.setMargins(LEFT_MARGIN, RIGHT_MARGIN, TOP_MARGIN, BOTTOM_MARGIN);
        writer = PdfWriter.getInstance(document, new FileOutputStream(fileName));
        writer.setPageEvent(HELPER);
        document.addTitle(title);
        document.addAuthor("Scouting Jan Baloys Stiphout");
        document.addCreator("Akela Java application");
//        document.addSubject("iText test");
//        document.addKeywords("PDF iText java");
        document.addCreationDate();
    }


    void addPage() {
        if (! document.isOpen()) {
            document.open();
        }
        else {
            document.newPage();
        }
    }
    
    
    void addWhiteSpace() throws DocumentException {
        document.add(new Paragraph(" "));
    }
    
    
    void addParagraph(String content) throws DocumentException {
        addParagraph(null, content);
    }


    void addParagraph(String title, Collection<String> content) throws DocumentException {
        if (title != null) {
            document.add(new Paragraph(title, TITLE_FONT));
        }
        if (content != null) {
            for (String text : content) {
                Paragraph paragraph = new Paragraph(text, CONTENT_FONT);
                document.add(paragraph);
            }
        }
    }


    void addParagraph(String title, String content) throws DocumentException {
        Vector<String> vector = new Vector<String>();
        vector.add(content);
        addParagraph(title, vector);
    }


    void addLine(String line) throws DocumentException{
        document.add(new Paragraph(line, CONTENT_FONT));
    }


    void addTable(String[][] content, float[] relativeWidths, int[] alignment, boolean showGrid) throws DocumentException {
        int columnCount = content.length;
        PdfPTable table;
        if (relativeWidths != null) {
            table = new PdfPTable(relativeWidths);
            if (relativeWidths.length < columnCount) {
                columnCount = relativeWidths.length;
            }
        }
        else {
            table = new PdfPTable(content.length);
        }
        for (int row = 0; row < content[0].length; row++) {
            for (int column = 0; column < columnCount; column++) {
                PdfPCell cell = new PdfPCell(new Phrase(content[column][row], CONTENT_FONT));
                if (alignment != null && column < alignment.length) {
                    cell.setHorizontalAlignment(alignment[column]);
                }
                if (! showGrid) {
                    cell.setBorder(PdfPCell.NO_BORDER);// cell.setBorder(PdfPCell.BOTTOM);
                }
                table.addCell(cell);
            }
            table.completeRow();
        }
        table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        document.add(table);
    }


    void addTable(String[][] content) throws DocumentException {
        addTable(content, null, null, true);
    }
    
    
    void addTable(TableModel model) throws DocumentException {
        int columnCount = model.columnCount();
        int rowCount = model.rowCount();
        PdfPTable table = new PdfPTable(columnCount);
        table.setWidths(model.relativeColumnWidths());
        for (int row = 0; row < rowCount; row++) {
            for (int column = 0; column < columnCount; column++) {
                PdfPCell cell = new PdfPCell(new Phrase(model.value(row, column), CONTENT_FONT));
                cell.setBackgroundColor(model.getBackgroundColor(row, column));
                cell.setHorizontalAlignment(model.horizontalAlignment(row, column));
                cell.setBorder(model.border(row, column));
                cell.setFixedHeight(15);
                table.addCell(cell);
            }
            table.completeRow();
        }
        table.setHorizontalAlignment(PdfPTable.ALIGN_LEFT);
        document.add(table);
    }


    void moveToY(float y) throws DocumentException {
        boolean pageAdded = false;
        float position = writer.getVerticalPosition(false);
        while (y < position && ! pageAdded) {
            document.add(new Paragraph(" "));
            float newPosition = writer.getVerticalPosition(false);
            pageAdded = newPosition > position;
            position = newPosition;
        }
    }


    float getPageHeight() {
        Rectangle size = document.getPageSize();
        return size.getHeight();
    }


    float getVerticalPosition() {
        return writer.getVerticalPosition(false);
    }


//    void drawString(String string, int x, int y) throws Exception {
//        PdfContentByte cb = writer.getDirectContent();
//        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
//        cb.setFontAndSize(baseFont, 12);
//        cb.beginText();
//        cb.showTextAligned(PdfContentByte.ALIGN_LEFT, string, x, y, 0);
//        cb.endText();
//    }


    void close() {
        document.close();
    }
    
    
    private PdfPageEventHelper HELPER = new PdfPageEventHelper() {

        public void onOpenDocument(PdfWriter writer, Document document) {
            onStartPage(writer, document);
        }

        public void onStartPage(PdfWriter writer, Document document) {
            Rectangle pageSize = document.getPageSize();
            float pageWidth = pageSize.getWidth();
            float pageHeight = pageSize.getHeight();
            if (headerText != null) {
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                cb.beginText();
                cb.setFontAndSize(HEADER_BASE_FONT, HEADER_FONT_SIZE);
                cb.showTextAligned(PdfContentByte.ALIGN_LEFT, headerText, 50, pageHeight - HEADER_FONT_SIZE - 20, 0);
                cb.endText();
                cb.restoreState();
            }
            if (subheaderText != null) {
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                cb.beginText();
                cb.setFontAndSize(SUBHEADER_BASE_FONT, SUBHEADER_FONT_SIZE);
                cb.showTextAligned(PdfContentByte.ALIGN_LEFT, subheaderText, 50, pageHeight - HEADER_FONT_SIZE - SUBHEADER_FONT_SIZE - 40, 0);
                cb.endText();
                cb.restoreState();
            }
            if (headerImage != null) {
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                float imageWidth = headerImage.getWidth() * imageHeaderScale;
                float imageHeight = headerImage.getHeight() * imageHeaderScale;
                try {
                    cb.addImage(headerImage, imageWidth, 0, 0, imageHeight, pageWidth - imageWidth - 10, pageHeight - imageHeight - 10);
                }
                catch (DocumentException ex) {
                    ex.printStackTrace(System.err);
                }
                cb.restoreState();
            }
        }

        public void onEndPage(PdfWriter writer, Document document) {
            if (drawFooter) {
                float pageWidth = document.getPageSize().getWidth();
                String text = "Pagina " + writer.getPageNumber();
                PdfContentByte cb = writer.getDirectContent();
                cb.saveState();
                cb.beginText();
                cb.setFontAndSize(FOOTER_BASE_FONT, FOOTER_FONT_SIZE);
                cb.showTextAligned(PdfContentByte.ALIGN_CENTER, text, pageWidth / 2, 10, 0);
                cb.endText();
                cb.restoreState();
            }
        }

    };


    private String fileName;

    private Document document = null;
    private PdfWriter writer = null;

    private Image headerImage = null;
    private float imageHeaderScale = 1.0f;

    private String headerText = null;
    private String subheaderText = null;

    private boolean drawFooter = true;

    private static final String EXTENSION = ".pdf";

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font CONTENT_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    private static BaseFont HEADER_BASE_FONT = null;
    private static final int HEADER_FONT_SIZE = 18;

    private static BaseFont SUBHEADER_BASE_FONT = null;
    private static final int SUBHEADER_FONT_SIZE = 12;

    private static BaseFont FOOTER_BASE_FONT = null;
    private static final int FOOTER_FONT_SIZE = 10;

    private static final float LEFT_MARGIN   =  50;
    private static final float RIGHT_MARGIN  =  50;
    private static final float TOP_MARGIN    = 100;
    private static final float BOTTOM_MARGIN =  50;


    static {
        try {
            HEADER_BASE_FONT = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
            SUBHEADER_BASE_FONT = BaseFont.createFont(BaseFont.TIMES_ROMAN, BaseFont.CP1252, BaseFont.EMBEDDED);
            FOOTER_BASE_FONT = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.EMBEDDED);
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }

}



class TableModel {

    public int rowCount() {
        return 0;
    }

    public int columnCount() {
        return 0;
    }

    public float[] relativeColumnWidths() {
        return null;
    }

    public String value(int row, int column) {
        return null;
    }
    
    public BaseColor getBackgroundColor(int row, int column) {
        return BaseColor.WHITE;
    }

    public int horizontalAlignment(int row, int column) {
        return PdfPCell.ALIGN_LEFT;
    }

    public int border(int row, int column) {
        return PdfPCell.NO_BORDER;
    }

}
