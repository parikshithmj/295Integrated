
import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONArray;
//import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MongoCRUD {
    MongoClient mongoClient = new MongoClient(new MongoClientURI("<your mongodb url>"));


    public void insert(String json, String dbName, String collectionName, String jsonDataName) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
            ArrayList e = new ArrayList();
            DB db = this.mongoClient.getDB(dbName);
            DBCollection collection = db.getCollection(collectionName);
            if(jsonObject.has(jsonDataName)) {
                JSONArray dbObject = (JSONArray)jsonObject.get(jsonDataName);

                int i;
                for(i = 0; i < dbObject.length(); ++i) {
                    e.add(dbObject.getJSONObject(i).toString());
                }

                System.out.println(e.size());

                for(i = 0; i < e.size(); ++i) {
                    DBObject dbObject1 = (DBObject)JSON.parse((String)e.get(i));
                    collection.insert(new DBObject[]{dbObject1});
                }
            } else {
                DBObject var13 = (DBObject)JSON.parse(jsonObject.toString());
                collection.insert(new DBObject[]{var13});
            }
        } catch (Exception var12) {
            var12.printStackTrace();
        }

    }

    public void insertDbObject(String dbName, String collectionName, DBObject dbObject) {
        Object jsonObject = null;

        try {
            DB e = this.mongoClient.getDB(dbName);
            DBCollection collection = e.getCollection(collectionName);
            collection.insert(new DBObject[]{dbObject});
        } catch (Exception var7) {
            var7.printStackTrace();
        }

    }

    public List<String> retrieveUpcomingMovie(String dbName, String collectionName) {
        ArrayList upcomingMoviesList = new ArrayList();

        try {
            DB e = this.mongoClient.getDB(dbName);
            DBCollection collection = e.getCollection(collectionName);
            DBCursor cursor = collection.find();

            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                String movieName = dbObject.get("title").toString();
                upcomingMoviesList.add(movieName);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return upcomingMoviesList;
    }

    public List<DBObject> retrieve(String dbName, String collectionName) {
        new ArrayList();
        ArrayList dbObjectList = new ArrayList();

        try {
            DB e = this.mongoClient.getDB(dbName);
            DBCollection collection = e.getCollection(collectionName);
            DBCursor cursor = collection.find();

            while(cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                dbObjectList.add(dbObject);
            }
        } catch (Exception var9) {
            var9.printStackTrace();
        }

        return dbObjectList;
    }
}
