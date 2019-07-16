package com.logArchiver.rest;

import org.glassfish.grizzly.http.server.Response;
import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


@Path("/archive")
public class archivePOST extends FileArchiver {

    private String json;
    private File file = new File("archives.json");
    private String url = file.getAbsolutePath();
    private File file_2 = new File("current.json");
    private String url_2 = file_2.getAbsolutePath();
    private JSONParser parser = new JSONParser();
    private String outf = "{ outFile: ";
    private String sourcef = "{ sourceFile: ";
    private String timestamp = "{ archiveDate: ";
    private int counter = 1;
    private String key_counter = "1";
    private String specific_value = "{}";
    private String specific = "Log not found!";


    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    //@Produces(MediaType.TEXT_PLAIN)
    public String getList() {

        try {

            JSONArray data = (JSONArray) parser.parse(
                    new FileReader(url));
            json = data.toJSONString();
        } catch (IOException | ParseException e) {
        }
             return json;
    }



    //type number of index oi json
    @POST
    @Path("/{key}")
    @Consumes(MediaType.APPLICATION_JSON)
    public String getValue(@PathParam("key") String key) throws ParseException {

       key_counter = key;

        try {

            JSONArray data = (JSONArray) parser.parse(
                    new FileReader(url));
            data.forEach( emp -> parseArchiveObject( (JSONObject) emp) );



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


return specific;


    }

   public void parseArchiveObject(JSONObject archive){
        if (Integer.parseInt(key_counter) == counter){

            String outV = (String) archive.get("outFile");
            String sourceV = ( String) archive.get("sourceFile");
            String dateV  = (String) archive.get("archiveDate");

            JSONObject employeeDetails = new JSONObject();
            employeeDetails.put("outFile", outV);
            employeeDetails.put("sourceFile", sourceV);
            employeeDetails.put("archiveDate", dateV);

            try (FileWriter file = new FileWriter(url_2)) {

                file.write(employeeDetails.toJSONString());
                file.flush();

            } catch (IOException e) {
                e.printStackTrace();
            }
            try {

                JSONObject data_1 = (JSONObject) parser.parse(
                        new FileReader(url_2));
                specific = data_1.toJSONString();
            } catch (IOException | ParseException e) {
            }


        }
          counter++;


   }

}
