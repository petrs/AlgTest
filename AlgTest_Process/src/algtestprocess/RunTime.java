/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package algtestprocess;


import static algtestprocess.JCinfohtml.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rk
 */
public class RunTime {       
    public static void beginRunTimeHTML(FileOutputStream file, String title) throws IOException {
        String toFile = "";
        toFile += "<html lang=\"en\">\n";
        toFile += " <head>\n";
        toFile += "\t<meta charset=\"utf-8\">\n" +
                    "\t<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"+
                    "\t<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n\n";
        
        toFile += "\t<meta name=\"description\" content=\"The JCAlgTest is a tool designed for automatic gathering various performance properties of Java cards. \">\n" +
                    "\t<meta name=\"author\" content=\"JCAlgTest\">\n"  +
                    "\t<title>"+title+"</title>\n";

        toFile += "\t<link href=\"../dist/css/bootstrap.min.css\" rel=\"stylesheet\">\n"
                + "<script type=\"text/javascript\" src=\"../dist/jquery-2.2.3.min.js\"></script>\n"
                + "\t<link href=\"../assets/css/ie10-viewport-bug-workaround.css\" rel=\"stylesheet\">\n"                
                + "\t<link rel=\"stylesheet\" type=\"text/css\" href=\"../dist/style.css\">\n";
        
        toFile += " </head>\n\n";
        toFile += " <body style=\"margin-top:50px; padding:20px\">\n\n";

        toFile += " \t<nav class=\"navbar navbar-inverse navbar-fixed-top\">\n\t\t<div class=\"container\">\n\t\t<script type=\"text/javascript\" src=\"../header-1.js\"></script>\n\t\t</div>\n\t</nav>\n\n";

        file.write(toFile.getBytes());
    }
        
    public static void endRunTimeHTML(FileOutputStream file) throws IOException {
        String toFile = "";
        toFile += "\t<script type=\"text/javascript\" src=\"../footer.js\"></script>\n"+
                "<a href=\"#\" class=\"back-to-top\"></a>" +
                "\t<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js\"></script>\n" +
                "\t<script>window.jQuery || document.write('<script src=\"../assets/js/vendor/jquery.min.js\"><\\/script>')</script>\n" +
                "\t<script src=\"../dist/js/bootstrap.min.js\"></script>\n" +
                "\t<script src=\"../assets/js/ie10-viewport-bug-workaround.js\"></script>\n";   
        
        toFile += " </body>\n";
        toFile += "</html>\n";
        file.write(toFile.getBytes());
    }
    
    public static void topFunction(List<String> lines, FileOutputStream file, Integer lp) throws IOException {
        String toFile;
        Integer tp = 0;
        List<String> topNames = new ArrayList<>();
        loadTopFunctions(topNames, null);

        if (!topNames.isEmpty()) {
            toFile = "<h3 id=\"TOP\">TOP FUNCTIONS</h3>\n";                              //name of table
            toFile += "<p>In the table below you can find results of performance testing for frequently used functions.</p>\n";
            toFile += TABLE_HEAD;
            file.write(toFile.getBytes());
            toFile = "";

            for (String top : topNames) {
                while (lp < lines.size() - 4) {
                    if (lines.get(lp).contains(top)) {
                        lp = parseOne(lines, file, lp, tp);
                    } else {
                        lp++;
                    }
                }
                lp = 0;
            }

            toFile += "</table>\n</br>\n";                                  //end of table
            file.write(toFile.getBytes());
            lp = 0;
        }
    }
    
    public static void tableGenerator(List<String> lines, FileOutputStream file, Integer lp) throws IOException {
        String toFile = "";
        while (lp < lines.size() - 2) {
            lp++;
            for (String cat : category) {
                if (lines.get(lp).equals(cat) && !((lines.get(lp + 1)).contains("END") || (lines.get(lp + 2)).contains("END"))) {
                    toFile += "<h3 id=\"" + cat.replaceAll(" ", "_") + "\">" + cat + "</h3>\n";     //test category name
                    lp++;
                    if (lines.get(lp).contains("data")) {
                        toFile += "<p>" + lines.get(lp) + "</p>\n";           // info about length of data
                        lp++;
                    }

                    toFile += TABLE_HEAD;                                   // head of table 
                    file.write(toFile.getBytes());
                    toFile = "";
                    lp = parse(lines, file, lp);                                 //parsing info of separate tests
                    toFile += "</table>\n</br>\n";                          //end of table
                    file.write(toFile.getBytes());
                    toFile = "";
                }
            }
        }
    }
    
    public static Integer parse(List<String> lines, FileOutputStream file, Integer lp) throws IOException {
        int tp = 0;

        while (lp < lines.size()) {
            for (String cat : category) {
                if ((cat.equals(lines.get(lp))) || (lines.get(lp).contains("END"))) {
                    lp--;
                    return lp;
                }
            }

            lp = parseOne(lines, file, lp, tp);
            tp++;
        }
        return lp;
    }
        
    public static Integer parseOne(List<String> lines, FileOutputStream file, Integer lp, Integer tp) throws IOException {
        String toFile = "";
        String[] prepare;
        String[] operation;
        String[] other;

        if (lines.get(lp + 1).equals("ALREADY_MEASURED")) {
            lp += 2;
            return lp;
        } else {
            toFile += ((tp % 2) == 0) ? "\t<tr>" : "\t<tr class='even'>";
            prepare = lines.get(lp).trim().split(";");
            toFile += "<td><b>"+prepare[1]+"</b></td>";       //classic name without reference to chart
            //toFile += "<td><a class=\"fancybox fancybox.iframe\" href=\"./graphs/" + prepare[1] + ".html\" style=\"font-size:12px;\">" + prepare[1] + "</a></td>";
            lp += 2;
        }

        if ((lines.get(lp).contains("baseline")) && (lines.get(lp + 3).contains("avg op:"))) {
            lp++;
            prepare = lines.get(lp).trim().split(";");
            lp += 2;
            operation = lines.get(lp).trim().split(";");
            lp++;
            other = lines.get(lp).trim().split(";"); 

            toFile += "<td style=\"font-size: 110%; font-weight: bold;\">" + Float.valueOf(operation[2].replace(",", ".")) + "</td>";
            toFile += "<td>" + Float.valueOf(operation[4].replace(",", ".")) + "</td>";
            toFile += "<td>" + Float.valueOf(operation[6].replace(",", ".")) + "</td>";
            toFile += "<td>" + Integer.parseInt(other[2]) + "</td>";
            toFile += "<td class=\"minor\"></td><td class=\"minor\">" + Float.valueOf(prepare[2].replace(",", ".")) + "</td>";
            toFile += "<td class=\"minor\">" + Float.valueOf(prepare[4].replace(",", ".")) + "</td>";
            toFile += "<td class=\"minor\">" + Float.valueOf(prepare[6].replace(",", ".")) + "</td>";
                       
            toFile += "<td class=\"minor\">" + Integer.parseInt(other[4]) + "/" + Integer.parseInt(other[6]) + "</td>";
        } else {
            if (lines.get(lp).contains("baseline") && !(lines.get(lp).contains("error"))) {
                lp++;
                prepare = lines.get(lp).trim().split(";");
                lp++;
                toFile += "<td colspan=\"3\">" + lines.get(lp) + "</td><td> </td><td class=\"minor\"></td>";
                toFile += "<td class=\"minor\">" + Float.valueOf(prepare[2].replace(",", ".")) + "</td>";
                toFile += "<td class=\"minor\">" + Float.valueOf(prepare[4].replace(",", ".")) + "</td>";
                toFile += "<td class=\"minor\">" + Float.valueOf(prepare[6].replace(",", ".")) + "</td>";

            } else if (lines.get(lp).contains("error")) {
                prepare = lines.get(lp).trim().split(";");
                toFile += "<td colspan=\"3\">" + prepare[prepare.length - 1] + "</td>";
            } else {
                toFile += "<td colspan=\"3\">" + lines.get(lp) + "</td>";
            }
        }

        toFile += "</tr>\n";
        file.write(toFile.getBytes());
        lp++;
        return lp;
    }
    
    private static void addCardInfo(FileOutputStream file, String name) throws IOException{
        StringBuilder toFile = new StringBuilder();
        toFile.append("\t<div class=\"container\">\n\t\t<div class=\"row\">\n");
        toFile.append("\t\t\t<h1>Run time results - "+name+"</h1>\n");
        toFile.append("\t\t\t<p>This file was generated by JCAlgTest tool</p>\n");
        file.write(toFile.toString().getBytes());
    }
    
    public static String generateRunTimeFile(String input) throws FileNotFoundException, IOException {
        Integer linePosition = 0;        
        StringBuilder cardName = new StringBuilder();
        String cardNameFile = "noname";
        
        List<String> lines = initalize(input, cardName);                            //load lines to from file to List
        String resultsDir = new File(input).getParentFile().toString();
        if (!(cardName.toString().equals("")) && !(cardName.toString().equals(" "))){
            cardNameFile = cardName.toString().replaceAll(" ", "");
            cardNameFile = cardNameFile.replaceAll("_", "");
        } 
        
        new File(resultsDir+"/run_time").mkdirs(); 
        FileOutputStream file = new FileOutputStream(resultsDir + "/run_time/" + cardNameFile + ".html");       
        beginRunTimeHTML(file, "JCAlgTest - Run time - " + cardName.toString());                     //logo + headline
        addCardInfo(file, cardName.toString());        
        quickLinks(lines, file);
        details(lines, file);                                                       //test details + CPLC info
        file.write("\t\t</div>\n\t\t<div class=\"row\">\n".getBytes());
        topFunction(lines, file, linePosition);
        tableGenerator(lines, file, linePosition);                                  //all tables generator
        file.write("\t\t</div>\n\t</div>".getBytes());
        endRunTimeHTML(file);
        //System.out.println("Make sure that CSS file & JS files (\"Source\" folder) is present in output folder.");
        
        return cardName.toString();
    }
    
    public static List<String> generateRunTimeFolder(String dir) throws IOException {
        List<String> files = listFilesForFolder(new File(dir));
        List<String> namesOfCards = new ArrayList<>();

        //load data from input files (fixed-size perf data) and store name of card
        for(String filePath : files)
            namesOfCards.add(generateRunTimeFile(filePath));
               
        return namesOfCards;
    }
    
    public static void quickLinks(List<String> lines, FileOutputStream file) throws IOException {
        String toFile;
        toFile = "<div class=\"col-md-3 col-xs-3\">\n";
        toFile += "<h3>Quick links</h3>\n<ul style=\"list-style-type: circle;\">\n";
        if (0 != loadTopFunctions(new ArrayList<String>(), null))
            toFile += "\t<li>" + "<a href=\"#TOP\">TOP FUNCTIONS</a>" + "</li>\n";        

        List<String> usedCategories = new ArrayList<>();
        for (int i = 10; i < lines.size(); i++) 
            if ((category.contains(lines.get(i))) && !((lines.get(i + 1)).contains("END") || (lines.get(i + 2)).contains("END")))
                usedCategories.add(lines.get(i));
                    
        for (String cat : usedCategories)
            toFile += "\t<li>" + "<a href=\"#" + cat.replaceAll(" ", "_") + "\">" + cat + "</a>" + "</li>\n";
        
        toFile += "</ul>\n</div>\n";
        file.write(toFile.getBytes());
    }
        
    public static void generateRunTimeMain(String dir, List<String> namesOfCards) throws IOException {
        FileOutputStream page = new FileOutputStream(dir + "/run_time/run_time.html");
        beginRunTimeHTML(page, "JCAlgTest - Algortithm operation time");
        StringBuilder toFile = new StringBuilder();
        toFile.append("\t<div class=\"container\">\n");
        toFile.append("\t\t<div class=\"row\">\n");
        toFile.append("\t\t\t<div class=\"col-md-8 col-xs-8\">\n");
        toFile.append("\t\t<h2>Algortithm operation time tables</h2>\n");
        toFile.append("\t\t<p>XXX</p>\n");
        toFile.append("\t\t<p>XXX</p>\n");
        toFile.append("\t\t<p>XXXX</p><br>\n");
        toFile.append("\t\t<div class=\"alert alert-warning\" role=\"alert\">XXX</div>\n");
        toFile.append("\t\t\t</div>\n");
        toFile.append("\t\t\t<div class=\"col-md-4 col-xs-4\" style=\"overflow:hidden; margin:2em auto;\">\n");
        toFile.append("\t\t\t<img src=\"../pics/run_time_example.png\" alt=\"Run time table example\" class=\"img-responsive\" align=\"right\">\n");
        toFile.append("\t\t\t</div>\n");
        toFile.append("\t\t\t</div>\n");
        toFile.append("\t\t<div>\n");
        toFile.append("\t\t<h3>List of tested Java Cards: </h3>\n");
        toFile.append("\t\t<ul class=\"list-group\">\n");
        for(String name : namesOfCards){
            String cardNameFile = name;
            if (!(cardNameFile.equals("")) && !(cardNameFile.equals(" "))){
                cardNameFile = cardNameFile.replaceAll(" ", "");
                cardNameFile = cardNameFile.replaceAll("_", "");
            }
            toFile.append("\t\t\t<li class=\"list-group-item\"><a href=\""+cardNameFile+".html\">"+name+"</a></li>\n");            
        }
        toFile.append("\t\t</ul>\n");
        
        toFile.append("\t\t</div>\n");
        toFile.append("\t</div>\n");
        page.write(toFile.toString().getBytes());
        
        endRunTimeHTML(page);
        page.close();
    }
    
    public static void runRunTime(String dir) throws FileNotFoundException, IOException{
        List<String> namesOfCards = generateRunTimeFolder(dir);
        generateRunTimeMain(dir, namesOfCards);
      //  System.out.println("ADD all necessary scripts (header-1.js, RadarChart.js) to new generated folder.");        
    }
        
    
    public static void main(String [] args) throws FileNotFoundException, IOException{
        String dir = "D:/JCAlgTest/fixed/";
        List<String> namesOfCards = generateRunTimeFolder(dir);
        generateRunTimeMain(dir, namesOfCards);
      //  System.out.println("ADD all necessary scripts (header-1.js, RadarChart.js) to new generated folder.");        
    }
}

