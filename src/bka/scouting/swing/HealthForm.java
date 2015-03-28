package bka.scouting.swing;


import bka.scouting.Member;
import bka.scouting.Section;
import java.util.*;
import java.io.*;


public class HealthForm {
    
    public HealthForm(String dataPath) {
        this.dataPath = dataPath;
    }

    public void createMultiplePdfs(java.util.List<Member> members, boolean signed) {
        try {
            java.net.URL logoUrl = ClassLoader.getSystemClassLoader().getResource("images/SCOUTING.jpg");
            Iterator<Member> it = members.iterator();
            while (it.hasNext()) {
                Member member = it.next();
                String memberName = member.fullName();
                PdfDocument document = new PdfDocument(dataPath + memberName);
                document.open("Gezondheidsformulier " + memberName);
                document.setHeaderImage(logoUrl, 0.75f);
                document.setDrawFooter(false);
                drawPage(document, member, signed);
                document.close();
            }
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    
    public void createPdf(java.util.List<Member> members, String sectionName, boolean signed) {
        String filename = dataPath + legalFilename(sectionName);
        try {
            java.net.URL logoUrl = ClassLoader.getSystemClassLoader().getResource("images/SCOUTING.jpg");
            PdfDocument document = new PdfDocument(filename);
            document.setHeaderImage(logoUrl, 0.75f);
            document.setDrawFooter(false);
            document.open("Gezondheidsformulieren " + sectionName);
            for (Member member : members) {
                drawPage(document, member, signed);
            }
            document.close();
        }
        catch (Exception ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    
    public void createPdf(List<Member> members, Section section, boolean signed) {
        createPdf(members, section.getName(), signed);
    }
    
    
    public void createMultipleHtmls(java.util.List<Member> members, boolean signed) {
        try {
            Iterator<Member> it = members.iterator();
            while (it.hasNext()) {
                Member member = it.next();
                PrintStream file = createHtmlFile(member.fullName());
                writeHtmlPage(file, member, signed);
                closeHtmlFile(file);
            }
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    
    public void createHtml(java.util.List<Member> members, String filename, boolean signed) {
        try {
            PrintStream file = createHtmlFile(filename);
            Iterator<Member> it = members.iterator();
            while (it.hasNext()) {
                Member member = it.next();
                writeHtmlPage(file, member, signed);
                file.println("<HR width=\"100%\">");
            }
            closeHtmlFile(file);
        }
        catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }
    
    
    public void createHtml(List<Member> members, Section section, boolean signed) {
        createHtml(members, section.getName(), signed);
    }
    
    
    private String legalFilename(String filename) {
        filename = filename.replace('\\', '_');
        filename = filename.replace('\"', '_');
        filename = filename.replace('/', '_');
        filename = filename.replace(',', '_');
        filename = filename.replace('*', '_');
        filename = filename.replace('?', '_');
        return filename;
    }
    
    
    private void drawPage(PdfDocument document, Member member, boolean signed) throws Exception {
        document.setHeaderText(member.fullName() + " " + member.getFeature("Lidnummer"));
        document.setSubheaderText("Geboortedatum: " + member.getFeature("Geboortedatum"));
        document.addPage();
        float currentPosition = document.getVerticalPosition();
        float featureHeight = (currentPosition - 60) / 10;
        float featurePosition = currentPosition;
        drawFeature(document, member, "Adres");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Telefoon");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        List<String> insuranceFeatures = new Vector<String>();
        List insuranceList = member.getFeature("Zorgverzekeraar");
        if (insuranceList != null) {
            insuranceFeatures.addAll(insuranceList);
        }
        insuranceList = member.getFeature("Polisnummer");
        if (insuranceList != null) {
            insuranceFeatures.addAll(insuranceList);
        }
        document.addParagraph("Zorgverzekering", insuranceFeatures);
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Huisarts (Naam, adres, telefoon)");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Dieet");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Allergieën");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Medicijngebruik");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Kan zwemmen (diploma's)");
        featurePosition -= featureHeight;
        document.moveToY(featurePosition);
        drawFeature(document, member, "Noodtelefoonnummer(s)");
        if (signed) {
            featurePosition -= featureHeight;
            document.moveToY(featurePosition);
            document.addParagraph("Ondertekening", "Handtekening ouder:                                           Plaats:                                                       Datum: ");
        }
        java.util.List<String> additional = member.getFeature("Overig");
        if (additional != null && additional.size() > 0) {
            String content = additional.get(0);
            if (toIgnore(content)) {
                additional = null;
            }
        }
        else {
            additional = null;
        }
        if (additional == null) {
//            drawString(writer, "Pagina 1 van 1", PAGE_FOOTER_X, PAGE_FOOTER_Y);
        }
        else {
            document.addPage();
            drawFeature(document, member, "Overig");
            //drawString(writer, "Pagina 2 van 2", PAGE_FOOTER_X, PAGE_FOOTER_Y);
        }
    }


    private void drawFeature(PdfDocument document, Member member, String featureName) throws Exception {
        List<String> features = member.getFeature(featureName);
        document.addParagraph(featureName, features);
    }
    
    
    private PrintStream createHtmlFile(String filename) throws IOException {
        PrintStream stream = new PrintStream(dataPath + legalFilename(filename) + ".html");
        stream.println("<HTML>");
        stream.println("\t<HEAD>");
        stream.println("\t\t<TITLE>" + filename + "</TITLE>");
        stream.println("\t</HEAD>");
        stream.println("<BODY>");
        return stream;
    }
    
    
    private void closeHtmlFile(PrintStream stream) {
        stream.println("</BODY> </HTML>");
        stream.close();
    }
    
    
    private void writeHtmlPage(PrintStream file, Member member, boolean signed) {
        file.println("<FONT SIZE=6>Scouting Jan Baloys Stiphout<P></FONT>");
        file.println("<FONT SIZE=5>" + member.fullName() + " " + member.getFeature("Lidnummer") + "<P></FONT>");
        file.println("<FONT SIZE=3>");
        writeHtmlFeature(file, member, "Geboortedatum");
        writeHtmlFeature(file, member, "Adres");
        writeHtmlFeature(file, member, "Telefoon");
        writeHtmlFeature(file, member, "Zorgverzekeraar");
        writeHtmlFeature(file, member, "Polisnummer");
        writeHtmlFeature(file, member, "Huisarts (Naam, adres, telefoon)");
        writeHtmlFeature(file, member, "Dieet");
        writeHtmlFeature(file, member, "Allergieën");
        writeHtmlFeature(file, member, "Medicijngebruik");
        writeHtmlFeature(file, member, "Kan zwemmen (diploma's)");
        writeHtmlFeature(file, member, "Noodtelefoonnummer(s)");
        java.util.List<String> additional = member.getFeature("Overig");
        if (additional != null && additional.size() > 0) {
            file.println("<B>Overig</B>:");
            Iterator<String> it = additional.iterator();
            while (it.hasNext()) {
                String content = it.next();
                if (! toIgnore(content)) {
                    file.println("<BR>" + content);
                }
            }
            file.println("<P>");
        }
        if (signed) {
            file.println("<P><B><I>Handtekening ouder:<P>Plaats:<P>Datum:</I></B>");
        }
        file.println("</FONT>");
    }
    
    
    private void writeHtmlFeature(PrintStream file, Member member, String feature) {
        file.println("<B>" + feature + "</B>:");
        java.util.List<String> content = member.getFeature(feature);
        if (content != null) {
            Iterator<String> it = content.iterator();
            while (it.hasNext()) {
                file.println("<BR>" + it.next());
            }
        }
        file.println("<P>");
    }
    
    
    private boolean toIgnore(String string) {
        boolean ignore = false;
        if (string != null) {
            string = string.trim();
            int i = 0;
            while (! ignore && i < ignoreAdditional.length) {
                ignore = string.equalsIgnoreCase(ignoreAdditional[i]);
                i++;
            }
        }
        return ignore;
    }
    
    
    private static final String[] ignoreAdditional = {"", "..", "-", "?", "n.v.t.", "nvt", "nee", "neen"};

    private String dataPath;
    
}
