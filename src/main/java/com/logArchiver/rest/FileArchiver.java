package com.logArchiver.rest;

import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import javax.ws.rs.Path;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


public class FileArchiver {

    private String dir_name;
    private String dir_path;
    private double interval;
    private Scanner input = new Scanner(System.in);
    //private String config = "C:\\Users\\User\\IdeaProjects\\PFAutoLogArchiver\\jersey-service\\src\\main\\java\\com\\logArchiver\\rest\\config.properties";
    private File file = new File("config.properties");
    private String config = file.getAbsolutePath();
    private int sleepTime = 14400000;
    private int file_counter = 1;
    private boolean last_write = false;
    private String date;
    private String source;
    private String out;
    private File file_2 = new File("archives.json");
    private String url = file_2.getAbsolutePath();
    //String url = "C:\\Users\\User\\IdeaProjects\\PFAutoLogArchiver\\jersey-service\\src\\main\\java\\com\\logArchiver\\rest\\archives.json";

    void welcome() throws Exception {

        int userChoice;
        // Main Menu
        System.out.println("\n\nChoose an option:\n1. Add directory.\n2. Add file exceptions\n3. Run program .\n4.Set sleep time\n5. Stop program");
        userChoice = input.nextInt();

        switch(userChoice) {
            case 1:
                write_config(true);
                break;
            //get confirmation time from TxID
            case 2:
                write_config(false);
            case 3:
                iterate_files();

                System.out.println("\n\nARCHIVE DONE, resuming in "+Integer.toString(sleepTime / 3600000)+" hours");
               // Thread.sleep(sleepTime);

                break;
            case 3:
                System.out.println("\n\nEnter the sleep time in hours: (default 4 hours)");
                userChoice = input.nextInt();

                while(userChoice < 1){

                    System.out.println("\n\nEnter the sleep time in hours: (minimun 1 hour)");
                    userChoice = input.nextInt();

                }
                sleepTime = userChoice * 3600000;
                System.out.println("\n\nSleep time set to "+Integer.toString(sleepTime / 3600000) +" hours");

                break;

            case 4:

                System.out.println("\n\nBye");
               System.exit(0);
                break;
            default:
                System.out.println("\n\nChoice must be between 1 and 6!");
                welcome();
        }
        // input.nextLine();
        welcome();
    }

    private void write_config(boolean type) throws IOException {
        //type = 0 -> directory, type = 1 -> exception
        Properties prop=new Properties();
        Scanner sc = new Scanner(System.in);

        try (FileInputStream ip = new FileInputStream(config)) {
            prop.load(ip);
        }
        boolean
        switch(type) {
            case True:
                write_config(true);
                break;
            //get confirmation time from TxID
            case 2:
                write_config(false);
            case 3:
                iterate_files();

                System.out.println("\n\nARCHIVE DONE, resuming in "+Integer.toString(sleepTime / 3600000)+" hours");
                // Thread.sleep(sleepTime);

                break;
            case 3:
                System.out.println("\n\nEnter the sleep time in hours: (default 4 hours)");
                userChoice = input.nextInt();

                while(userChoice < 1){

                    System.out.println("\n\nEnter the sleep time in hours: (minimun 1 hour)");
                    userChoice = input.nextInt();

                }
                sleepTime = userChoice * 3600000;
                System.out.println("\n\nSleep time set to "+Integer.toString(sleepTime / 3600000) +" hours");

                break;

            case 4:

                System.out.println("\n\nBye");
                System.exit(0);
                break;
            default:
                System.out.println("\n\nChoice must be between 1 and 6!");
                welcome();
        }

        System.out.println("Write the dir name: ");
        dir_name = sc.nextLine();
        System.out.println("Write the dir path: ");
        dir_path = sc.nextLine();
        try {
            //set the properties value
            dir_path=dir_path.replaceAll("\\u202A","");
            prop.setProperty(dir_name, dir_path);
//            prop.setProperty("dbuser", "myuser");
//            prop.setProperty("dbpassword", "mypwd");

            //save properties to project root folder
            prop.store(new FileOutputStream(config), null);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        System.out.println("\n\nSet new dir as:\nName: "+dir_name+"\nPath: "+dir_path);


    }

    private String fileToString(){

    StringBuilder contentBuilder = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new FileReader(url)))
    {

        String sCurrentLine;
        while ((sCurrentLine = br.readLine()) != null)
        {
            contentBuilder.append(sCurrentLine).append("\n");
        }
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    return contentBuilder.toString();
}
  private void lastBracketAdd() throws IOException, ParseException {

        String without_bracket = fileToString();

        String replacement = without_bracket.substring(0, without_bracket.length() - 2);
        FileWriter fileWriter = new FileWriter(url);

              fileWriter.write(replacement +']');
              fileWriter.close();

  }

    private void lastBracketRemove() throws IOException, ParseException {

        String without_bracket = fileToString();

        String replacement = without_bracket.substring(0, without_bracket.length() - 2);
        FileWriter fileWriter = new FileWriter(url);

        fileWriter.write(replacement);
        fileWriter.close();

    }
    private void fixJSON() throws IOException {
        FileWriter fileFix = new FileWriter(url);
        String fileStr = fileToString();

        if(fileToString().endsWith(" ]")){
            fileStr = fileStr.substring(0, fileStr.length() - 1) + ",";

            fileFix.write(fileStr);
            fileFix.close();

        }
    }
    private void writeJSON(String archiveDate, String sourceFile, String outFile) throws ParseException, IOException {

        FileWriter fileWriter = new FileWriter(url, true);
        File exception = new File(url);

        if (exception.length() == 0) {
            fileWriter.write('[');
            fileWriter.close();
        }
        else {


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("archiveDate", archiveDate);
            jsonObject.put("sourceFile", sourceFile);
            jsonObject.put("outFile", outFile);

            fileWriter.write(jsonObject.toJSONString() + ',');;
            fileWriter.close();


        }

    }

        private Boolean readJSON(){

            JSONParser jsonParser = new JSONParser();

            try (FileReader reader = new FileReader(url))
            {
                //Read JSON file
                Object obj = jsonParser.parse(reader);

                JSONArray archiveList = (JSONArray) obj;


                if (archiveList.isEmpty()) {
                    return true;
                }


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
 return false;
    }



    private String getTimestamp() throws java.text.ParseException {
       /* SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = new Date(System.currentTimeMillis());
        String strDate = (formatter.format(date)).toString();*/

        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));

//Local time zone
        SimpleDateFormat dateFormatLocal = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");

//Time in GMT
        return (dateFormatLocal.parse( dateFormatGmt.format(new Date()))).toString() + " GMT";
       /* LocalDate local = LocalDate.now();
        Date currentDate = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(currentDate);*/

       // return strDate;

    }

    private void iterate_files() throws IOException, ParseException {
        fixJSON();
        Properties prop=new Properties();
        Scanner sc = new Scanner(System.in);
        String[] files = {};

        try (FileInputStream ip = new FileInputStream(config)) {
            prop.load(ip);
        }
        catch (IOException ex) {
            ex.printStackTrace();
        };

        // get all keys
        prop.keySet();
        prop.forEach((key, value) -> {
        try {


            archive(String.valueOf(value));
            file_counter++;

        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }


    });
        lastBracketAdd();
last_write = false;
file_counter = 1;

    }


    private void archive(String value) throws IOException, ParseException, java.text.ParseException {

        LocalDate local = LocalDate.now();
        Date currentDate = Date.from(local.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Long tempCurrent = currentDate.getTime() / 1000 / 60 / 60 / 24;
        Integer currentD = ((int) (long) tempCurrent);



        File path = new File(value);
        System.out.println(path);
        File [] files = path.listFiles();
        for (int i = 0; i < files.length; i++){

            if (files[i].isFile()) { //this line weeds out other directories/folders
                String exc = files[i].toString();
                if (!exc.contains("sys")){
                    Long modified = files[i].lastModified() / 1000 / 60 / 60 / 24;
                    Integer modifiedD = ((int) (long) modified);
                    Integer Diff = currentD - modifiedD;

                    if (Diff > 2) {
                        zipFile(files[i]);
                    }

                }
            }

        }


    }

    private void zipFile(File file) throws IOException, ParseException, java.text.ParseException {

        FileOutputStream fos = new FileOutputStream(file.getName().toString()+".zip");
        String sourceFile = file.toString();
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File fileToZip = new File(sourceFile);
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }

        String source = String.valueOf(file.getName());
        String out = convertFileName(source);

        writeJSON(getTimestamp(), source , out);


        file.delete();
        zipOut.close();
        fis.close();
        fos.close();
    }

    private String convertFileName(String fileName){
        if (fileName.indexOf(".") > 0) {
            return fileName.substring(0, fileName.lastIndexOf(".")) + ".zip";
        } else {
            return fileName;
        }

    }






}
