package Fetchers;


import boxofficewebapi.Utils;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class FacebookFetcher {

    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(mediaType, "{}");
    Request request = null;
    String json;


    public String getPageId(String searchQuery) {

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
//            JSONArray results = (JSONArray)k.get("data");
            for (int i = 0; i < k.length(); i++) {
                id = (String) k.getJSONObject(i).get("id");
            }

            System.out.println(id);


        } catch (Exception e) {
            e.printStackTrace();
        }
return id;

    }




public String  getPageLikes(String movieName){
    String pageId = getPageId(movieName);
    String url = "https://graph.facebook.com/v2.9/"+pageId+"?fields=fan_count&access_token='+Utils.FB_ACCESS_TOKEN +'";
    String pageLikes = "";
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



    public String getVideoId(String pageId) {
        //String value = "";
        String id = "";
        JSONArray array = null;
        String url = "https://graph.facebook.com/v2.9/" + pageId + "/videos?access_token="+Utils.FB_ACCESS_TOKEN +"&fields=id";


        try {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String json = response.body().string();
           // System.out.println(json);

            JSONObject r = new JSONObject(json);
            array = (JSONArray) r.get("data");
//            JSONArray results = (JSONArray)k.get("data");
            for (int i = 0; i < array.length(); i++) {
                id = (String) array.getJSONObject(i).get("id");
            }

            System.out.println(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    return id;
    }




    public JSONObject getReactions(String id, String  movieName, String genreIds) {

        String url = "https://graph.facebook.com/v2.9/" + id + "?access_token="+Utils.FB_ACCESS_TOKEN +"&fields=reactions.type(LIKE).limit(0).summary(1).as(like),reactions.type(WOW).limit(0).summary(1).as(wow),reactions.type(SAD).limit(0).summary(1).as(sad),reactions.type(LOVE).limit(0).summary(1).as(love),reactions.type(ANGRY).limit(0).summary(1).as(angry),reactions.type(HAHA).limit(0).summary(1).as(haha)";
        MediaType mediaType = MediaType.parse("application/json");
        //RequestBody body = RequestBody.create(mediaType, "{}");
        //String likecount ="";
        JSONObject result = null;
        Request request = null;
        try {
            request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
            String json = response.body().string();
            System.out.println(json);
            JSONObject object = new JSONObject(json);
            JSONObject like = (JSONObject) object.get("like");
            JSONObject likedata = (JSONObject) like.get("summary");
            int likecount = (Integer)likedata.get("total_count");
            JSONObject haha = (JSONObject) object.get("haha");
            JSONObject hahaData = (JSONObject) haha.get("summary");
            int hahacount = (Integer)hahaData.get("total_count");
            JSONObject sad = (JSONObject) object.get("sad");
            JSONObject saddata = (JSONObject) sad.get("summary");
            int sadcount = (Integer)saddata.get("total_count");
            JSONObject wow = (JSONObject) object.get("wow");
            JSONObject wowdata = (JSONObject) sad.get("summary");
            int wowcount = (Integer)wowdata.get("total_count");
            JSONObject love = (JSONObject) object.get("love");
            JSONObject lovedata = (JSONObject) love.get("summary");
            int lovecount = (Integer)lovedata.get("total_count");
            JSONObject angry = (JSONObject) object.get("angry");
            JSONObject angrydata = (JSONObject) love.get("summary");
            int angrycount = (Integer)angrydata.get("total_count");
            result = new JSONObject();
            result.put("like",likecount);
            result.put("love",lovecount);
            result.put("haha",hahacount);
            result.put("sad",sadcount);
            result.put("angry",angrycount);
            result.put("wow",wowcount);
            result.put("movieName",movieName);
            result.put("genreIds",genreIds);


        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

}






