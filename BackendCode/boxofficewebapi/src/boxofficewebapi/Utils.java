package boxofficewebapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class Utils {
	
	public static final String FB_ACCESS_TOKEN = "<your fb api key>";

	 public static String httpRequest(Request request){
	        String json= "";
	        OkHttpClient client = new OkHttpClient();

	        try {
	            Response response = client.newCall(request).execute();
	            json = response.body().string();
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return json;
	    }


	//Utilised in Hashing the String to a numeric value
    public static long hashString(String str ){
        long hash = 7;
        int strlen = str.length();
        for (int i = 0; i < strlen; i++) {
            if(hash*31 + str.charAt(i) > 0)
                hash = hash*31 + str.charAt(i);
            else
                break;
        }
        return hash/100000;
    }
    
    public final static HashMap<String, Integer> genreNametoID = new HashMap<String, Integer>()
    {{
                put("Action", 28);
            
                put("Adventure",12 );
            
                put("Animation",16);
            
                put("Comedy",35);
            
                put("Crime", 80);
            
                put("Documentary", 99);
            
                put("Drama", 18);
            
                put("Family", 10751);
            
                put("Fantasy", 14);
            
                put("History",36);
            
                put("Horror",27);
            
                put("Music", 10402);
            
                put("Mystery" ,9648);
            
                put("Romance", 10749);
            
                put("Sci-fi" , 878);
            
                put("TV Movie", 10770);
            
                put("Thriller" , 53);
            
                put("War", 10752);
            
                put("Western" , 37);

    }};
    
    public static int getGenreIDbyName(String genre){
        return genreNametoID.get(genre);
    }
    
    public static String genreColumnFiller(String line){
        String[] attr = line.split(",");
        int[] positions = new int[21];
        StringBuffer sb = new StringBuffer();
        ArrayList<Integer> list = new ArrayList<>();
        Map<String,Integer> genreMap = new HashMap<String, Integer>();
        genreMap.put("Action",1);
        genreMap.put("Adventure",2);
        genreMap.put("Animation",3);
        genreMap.put("Biography",4);
        genreMap.put("Comedy",5);
        genreMap.put("Crime",6);
        genreMap.put("Documentary",7);
        genreMap.put("Drama",8);
        genreMap.put("Family",9);
        genreMap.put("Fantasy",10);
        genreMap.put("History",11);
        genreMap.put("Horror",12);
        genreMap.put("Musical",13);
        genreMap.put("Mystery",14);
        genreMap.put("Romance",15);
        genreMap.put("Sci-Fi",16);
        genreMap.put("Sport",17);
        genreMap.put("Thriller",18);
        genreMap.put("Western",19);
        genreMap.put("War",20);

        int i =0;
        for(String genre : attr){
            if(genreMap.containsKey(genre)){
                positions[i] = genreMap.get(genre);
                i++;
            }
        }

         i = 0;
        for(int k=1; k<=20;k++){
            if(positions[i] == k){
            	//list.add(1);
                sb.append("1,");
                i++;
            }
            else
                sb.append("0,");
            //list.add(0);
        }

        //Action, Adventure,Animation,Biography,Comedy,Crime,Documentary,Drama,Family,Fantasy,History,Horror,Musical,Mystery,Romance,Sci-Fi,Sport,Thriller,Western,War


        return sb.toString();
        //return list;

    }
    
    
    
    public static final HashMap<Integer, String> genreIdtoName = new HashMap<Integer, String>() {
        {
            this.put(Integer.valueOf(28), "Action");
            this.put(Integer.valueOf(12), "Adventure");
            this.put(Integer.valueOf(16), "Animation");
            this.put(Integer.valueOf(35), "Comedy");
            this.put(Integer.valueOf(80), "Crime");
            this.put(Integer.valueOf(99), "Documentary");
            this.put(Integer.valueOf(18), "Drama");
            this.put(Integer.valueOf(10751), "Family");
            this.put(Integer.valueOf(14), "Fantasy");
            this.put(Integer.valueOf(36), "History");
            this.put(Integer.valueOf(27), "Horror");
            this.put(Integer.valueOf(10402), "Music");
            this.put(Integer.valueOf(9648), "Mystery");
            this.put(Integer.valueOf(10749), "Romance");
            this.put(Integer.valueOf(878), "Science Fiction");
            this.put(Integer.valueOf(10770), "TV Movie");
            this.put(Integer.valueOf(53), "Thriller");
            this.put(Integer.valueOf(10752), "War");
            this.put(Integer.valueOf(37), "Western");
        }
    };
    public static String getGenrebyId(int genreId) {
        return (String)genreIdtoName.get(Integer.valueOf(genreId));
    }

    
    
    public static String getPageId(String searchQuery) {
    	Request request = null;

    	String id = "";
    	        JSONArray k = null;

    	        String url = "https://graph.facebook.com/v2.9/search?type=page&q="+searchQuery+"&limit=1&access_token="+Utils.FB_ACCESS_TOKEN + "&fields=name,id";
    	        try {
    	            request = new Request.Builder()
    	                    .url(url)
    	                    .get()
    	                    .build();
    	            OkHttpClient client = new OkHttpClient();
    	            Response response = client.newCall(request).execute();
    	            String json = response.body().string();
    	            JSONObject r = new JSONObject(json);
    	            k = (JSONArray)r.get("data");
//    	            JSONArray results = (JSONArray)k.get("data");
    	            for (int i = 0; i < k.length(); i++) {
    	                id = (String) k.getJSONObject(i).get("id");
    	            }

    	            System.out.println(id);


    	        } catch (Exception e) {
    	            e.printStackTrace();
    	        }
    	return id;

    	    }
    
    
    
    
    public static String  getPageLikes(String movieName){
        String pageId = getPageId(movieName);
        String url = "https://graph.facebook.com/v2.9/"+pageId+"?access_token="+Utils.FB_ACCESS_TOKEN +"&fields=fan_count";
        String pageLikes = "";
        Request request = null;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            JSONObject fanCount = new JSONObject(json);
             int likes = (Integer)fanCount.get("fan_count");
             pageLikes = String.valueOf(likes);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return pageLikes;
    }
    public static String InvokeSparkJob(){

    	 try
         {
             Runtime rt = Runtime.getRuntime();
            // Process proc = rt.exec("javac");
             String command = "/usr/local/share/spark/spark-2.1.1-bin-hadoop2.7/bin/spark-submit --class SparkDecsionTree --master local[2] /opt/tomcat/webapps/boxofficewebapi-0.0.1-SNAPSHOT/WEB-INF/classes/boxoffice-1.0-SNAPSHOT.jar . . > /home/ubuntu/fate.txt";
             String [] commands = { "bash", "-c",command };
             Process proc=null;
             try {
         proc = Runtime.getRuntime().exec(commands);         
  } catch (Exception e) {
  e.printStackTrace();
  }
             InputStream stderr = proc.getErrorStream();
             InputStreamReader isr = new InputStreamReader(stderr);
             BufferedReader br = new BufferedReader(isr);
             FileOutputStream out = new FileOutputStream("/home/ubuntu/workhoja.txt");
             String line = null;
             //System.out.println("<ERROR>");
             while ( (line = br.readLine()) != null){
            	 line = line + "\n";
                 System.out.println(line);
                 out.write(line.getBytes());
             }
             //System.out.println("</ERROR>");
             int exitVal = proc.waitFor();
             System.out.println("Process exitValue: " + exitVal);
             out.close();
         } catch (Throwable t)
           {
             t.printStackTrace();
           }
         String fate = "Average";
    	 try {

             File f = new File("/home/ubuntu/fate.txt");

             BufferedReader b = new BufferedReader(new FileReader(f));

             String readLine = "";


              while ((readLine = b.readLine()) != null) {
                 if(readLine.equals("|3.0           |") || readLine.equals("|           3.0|")){
                 fate = "Hit";
                 break;
             }
                 else if(readLine.equals("|2.0           |") || readLine.equals("|           2.0|")){
                 fate = "Average";
                 break;
             }
                 else if(readLine.equals("|1.0           |") || readLine.equals("|           1.0|")){
                 fate = "Flop";
                 break;
             }


         }
         System.out.println("Fate of the movie is "+fate);
         } catch (IOException e) {
             e.printStackTrace();
         }
    	 return fate;
    }
	

}
