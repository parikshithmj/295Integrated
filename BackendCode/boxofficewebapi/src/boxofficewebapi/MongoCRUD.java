package boxofficewebapi;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MongoCRUD {
    MongoClient mongoClient = new MongoClient(new MongoClientURI("<your mongodb uri>"));

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


    public void insert(String json,String dbName, String collectionName, String jsonDataName){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(json);

            ArrayList<String> list = new ArrayList<String>();
            DB db = mongoClient.getDB( dbName );

            DBCollection collection = db.getCollection(collectionName);

            if(jsonObject.has(jsonDataName)){
                JSONArray results = (JSONArray)jsonObject.get(jsonDataName);
                for(int i=0; i<results.length(); i++){
                    list.add(results.getJSONObject(i).toString());
                }

                System.out.println(list.size());

                for(int i=0; i<list.size(); i++){
                    DBObject dbObject = (DBObject) JSON.parse(list.get(i));
                    collection.insert(dbObject);
                }
            }
            else {
                DBObject dbObject = (DBObject) JSON.parse(jsonObject.toString());
                collection.insert(dbObject);

            }
        }catch(JSONException e) {
            e.printStackTrace();
        }
    }


    public List<String> retrieveUpcomingMovie1(String dbName, String collectionName){

        List<String> upcomingMoviesList = new ArrayList<String>();
        try {
            DB db = mongoClient.getDB( dbName);
            DBCollection collection = db.getCollection(collectionName);

            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                String movieName = dbObject.get("title").toString();
                upcomingMoviesList.add(movieName);
            }

        } catch (Exception e) {
            e.printStackTrace();
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





    public List<String> retrievegenreId(String dbName, String collectionName){

        List<String> genreId = new ArrayList<String>();
        try {
            DB db = mongoClient.getDB( dbName);
            DBCollection collection = db.getCollection(collectionName);

            DBCursor cursor = collection.find();
            while (cursor.hasNext()) {
                DBObject dbObject = cursor.next();
                String genre_ids = dbObject.get("genre_ids").toString();
                genreId.add(genre_ids);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return genreId;

    }
}
