package bka.scouting.swing;


import bka.scouting.Member;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


class MemberList {

    
    MemberList(String dataPath) {
        this.dataPath = dataPath;
    }


    void createCsv(java.util.List<Member> members) {
        SortedMap<String, Member> map = new TreeMap<String, Member>();
        for (Member member : members) {
            map.put(member.getSurname() + ", " + member.getFirstName() + " " + member.getSurnamePrefix(), member);
        }
        File file = new File(dataPath + "Akela" + ".csv");
        try {
            PrintStream stream = new PrintStream(file);
            stream.println("Ledenlijst Scouting Jan Baloys Stiphout");
            int i = 0;
            for (String string : map.keySet()) {
                i++;
                stream.println(i + "," + string);
            }
        }
        catch (FileNotFoundException ex) {
            Logger.getLogger(MemberList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    
    void createPdf(java.util.List<Member> members) {
        try {
            java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("d MMMM yyyy");
            PdfDocument document = new PdfDocument(dataPath + "Akela");
            document.setHeaderText(TITLE);
            java.net.URL logoUrl = ClassLoader.getSystemClassLoader().getResource("images/SCOUTING.jpg");
            document.setHeaderImage(logoUrl, 0.75f);
            document.open(TITLE);
            document.addPage();
            int i = 0;
            String[][] table = new String[PDF_MEMBER_LIST_COLUMN_COUNT][members.size()];
            for (Member member : members) {
                table[0][i] = Integer.toString(i + 1) + ".";
                table[1][i] = member.fullName();
                Date birthDate = member.getDateOfBirth();
                if (birthDate != null) {
                    table[2][i] = dateFormat.format(birthDate);
                }
                table[3][i] = member.getDomicile();
                i++;
            }
            document.addTable(table, PDF_MEMBER_LIST_RELATIVE_COLUMN_WIDTHS, new int[] {PdfDocument.ALIGN_RIGHT}, false);
            document.close();
        }
        catch (Exception ex) {
            Logger.getLogger(MemberList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    private final String dataPath;

    private static final String TITLE = "Ledenlijst Scouting Jan Baloys Stiphout";
    private static final float[] PDF_MEMBER_LIST_RELATIVE_COLUMN_WIDTHS = new float[] { 0.09f, 0.35f, 0.28f, 0.28f };
    private static final int PDF_MEMBER_LIST_COLUMN_COUNT = PDF_MEMBER_LIST_RELATIVE_COLUMN_WIDTHS.length;
}
