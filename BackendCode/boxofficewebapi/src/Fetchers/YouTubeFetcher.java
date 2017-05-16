package Fetchers;

import boxofficewebapi.Utils;
//import com.sun.tools.doclets.formats.html.SourceToHTMLConverter;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;



public class YouTubeFetcher {
    public String getComments(String videoId){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/commentThreads?fields=items(id,snippet(topLevelComment(snippet(textDisplay))))&textFormat=plainText&part=snippet&videoId="+videoId+"&maxResults=100&key=<key>")

             .get()
                .build();

ArrayList<String> l = new ArrayList<String>();
        String comment ="";

try {
    String json = Utils.httpRequest(request);

    JSONObject jsonObject = new JSONObject(json);
    JSONArray resultItem = (JSONArray) jsonObject.get("items");

    for (int i = 0; i < resultItem.length(); i++) {
        JSONObject snippet = (JSONObject)resultItem.getJSONObject(i).get("snippet");
        JSONObject topLevelComment = (JSONObject)snippet.get("topLevelComment");
        JSONObject commentSnippet = (JSONObject)topLevelComment.get("snippet");
        comment = (String)commentSnippet.get("textDisplay");

        comment = comment.trim().replaceAll("\\s+", " ");
        l.add(comment);



   }


}//try
        catch(Exception e){
            e.printStackTrace();
        }

        return l.toString();

    }







    public String getTopTenComments(String videoId){
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/commentThreads?fields=items(id,snippet(topLevelComment(snippet(textDisplay))))&textFormat=plainText&part=snippet&videoId="+videoId+"&maxResults=100&key=<key>")

                .get()
                .build();

        ArrayList<String> list = new ArrayList<String>();
        String comment ="";

        try {
            String json = Utils.httpRequest(request);

            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultItem = (JSONArray) jsonObject.get("items");

            for (int i = 0; i < 10; i++) {
                JSONObject type = (JSONObject)resultItem.getJSONObject(i).get("snippet");
                JSONObject type1 = (JSONObject)type.get("topLevelComment");
                JSONObject type2 = (JSONObject)type1.get("snippet");
                comment = (String)type2.get("textDisplay");

                comment = comment.trim().replaceAll("\\s+", " ");
                list.add(comment);



            }


        }//try
        catch(Exception e){
            e.printStackTrace();
        }

        return list.toString();

    }


    public JSONObject getStatistics(String videoId){


        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/videos?id="+videoId+"&key=AIzaSyDTYQP_htr1D7m5IX3cWtAANPoGqMpMYU4&fields=items(id,snippet(channelId,title,categoryId),statistics)&part=snippet,statistics")
                .get()
                .build();

       // ArrayList<String> l = new ArrayList<String>();
       JSONObject stats= null;

        try {
            String json = Utils.httpRequest(request);
            JSONObject jsonObject = new JSONObject(json);
            JSONArray resultItem = (JSONArray) jsonObject.get("items");
            for (int i = 0; i < resultItem.length(); i++) {
                stats = (JSONObject) resultItem.getJSONObject(i).get("statistics");

            }


        }//try
        catch(Exception e){
            e.printStackTrace();
        }

        return stats;

    }







    public JSONObject returnJson( String name, JSONObject statistics, String comments){
        JSONObject obj = new JSONObject();
        try {


            obj.put("name", name );
            obj.put("statistics", statistics);
            obj.put("videocomments", comments);

        }
        catch (Exception e){
            e.printStackTrace();
        }
        return obj;
    }


    public String getVideoId(String searchQuery){

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, "{}");
        Request request = new Request.Builder()
                .url("https://www.googleapis.com/youtube/v3/search?part=id&q="+searchQuery+"%20trailer&key=<key>")
                .get()
                .build();

        String response = Utils.httpRequest(request);
        String videoId = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray results = (JSONArray)jsonObject.get("items");

            JSONObject idObj = new JSONObject(results.get(0).toString());
            System.out.println(results.get(0));
            JSONObject videoIdObj = new JSONObject(idObj.get("id").toString());
            videoId = videoIdObj.get("videoId").toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return videoId;
    }
}
