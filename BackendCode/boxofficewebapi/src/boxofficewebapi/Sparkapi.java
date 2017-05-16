package boxofficewebapi;

import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.json.JSONObject;

import Fetchers.FacebookFetcher;
import Fetchers.YouTubeFetcher;
import facebook4j.Summary;

@Path("/sparkapi")
public class Sparkapi {
@POST
@Consumes("application/json")
@Produces("application/json")
@Path("/post")
public Response CreateSparkJob (PostData pd)
{
    MongoCRUD mongoCRUD = new MongoCRUD();

	System.out.println("Within POST");
	String color = "color";
	int cast_total_fb_likes= 50;
	String  movie = pd.getMovie();
	int year = pd.getYear();
	String month =  pd.getMonth();
	String genre = pd.getGenre();
	String actor1 = pd.getActor1();
	String actor2 = pd.getActor2();
	String actor3 = pd.getActor3();
	String director_name = pd.getDirector_name();
	String producer = pd.getProducer();
	int budget = pd.getBudget();
	String language = pd.getLanguage();
	String country = pd.getCountry();
	String content_rating = pd.getContent_rating();
	int duration = pd.getDuration();
//	String actor2_fb_likes = "";
    int num_user_reviews = 500;
    String password = pd.getPassword();
    String email = pd.getEmail();
//	String dir_fb_likes = "";
//	String actor3_fb_likes = "";
//	String actor1_fb_likes="";
    String summary = pd.getSummary();
    String poster = pd.getPoster();
    String trailerLink = pd.getTrailerLink();
	
	String[] attributes = genre.split(",");
	String fate = "";
	
	System.out.println("Fetching FB likes");

	int dir_fb_likes = Integer.parseInt(Utils.getPageLikes(director_name));
	int actor1_fb_likes = Integer.parseInt(Utils.getPageLikes(actor1));
	int actor2_fb_likes = Integer.parseInt(Utils.getPageLikes(actor2));
	int actor3_fb_likes = Integer.parseInt(Utils.getPageLikes(actor3));
	int Likes = Integer.parseInt(Utils.getPageLikes(director_name));
	
	JSONObject jsonObject = new JSONObject();
	jsonObject.put("movie", movie);
	jsonObject.put("year", year);
	jsonObject.put("month", month);
	jsonObject.put("summary", summary);
	jsonObject.put("genre", genre);
	jsonObject.put("actor1", actor1);
	jsonObject.put("actor2", actor2);
	jsonObject.put("actor3", actor3);
	jsonObject.put("director", director_name);
	jsonObject.put("producer", producer);
	jsonObject.put("budget", budget);
	jsonObject.put("language", language);
	jsonObject.put("poster", poster);
	jsonObject.put("trailerLink", trailerLink);
	jsonObject.put("email", email);
	jsonObject.put("password", password);

    mongoCRUD.insert(jsonObject.toString(),"boxoffice", "registration","results");
	
	System.out.println("Dir_FB_Like "+dir_fb_likes+ "Actor 1 FB likes"+actor1_fb_likes+ "Actor 2 FB likes"+actor2_fb_likes+ "Actor 3 FB likes"+actor3_fb_likes);
//	HashMap<String,Integer> map = new HashMap<String,Integer>();
			
//	for(int i =0;i<attributes.length;i++){
//		map.put(attributes[i], 1);
//	}
//	for(Map.Entry<String, Integer> entry: map.entrySet()){
//		
//	}
	

	
	
	StringBuffer inputBuffer = new StringBuffer();
	
	inputBuffer.append("2");
	inputBuffer.append(" 1:");
	inputBuffer.append(Utils.hashString(color));
    inputBuffer.append(" 2:");
    inputBuffer.append(Utils.hashString(director_name));
    inputBuffer.append(" 3:");
    inputBuffer.append(Utils.hashString(actor2));
    inputBuffer.append(" 4:");
    inputBuffer.append(Utils.hashString(actor1));
    inputBuffer.append(" 5:");
    inputBuffer.append(Utils.hashString(actor3));
    inputBuffer.append(" 6:");
    inputBuffer.append(Utils.hashString(language));
    inputBuffer.append(" 7:");
    inputBuffer.append(Utils.hashString(country));
    inputBuffer.append(" 8:");
    inputBuffer.append(Utils.hashString(content_rating));
    inputBuffer.append(" 9:");
    inputBuffer.append( duration);
    inputBuffer.append(" 10:");
    inputBuffer.append( dir_fb_likes );
    inputBuffer.append(" 11:");
    inputBuffer.append( actor1_fb_likes );
    inputBuffer.append(" 12:");
    inputBuffer.append( actor3_fb_likes );
    inputBuffer.append(" 13:");
    inputBuffer.append( cast_total_fb_likes );
    inputBuffer.append(" 14:");
    inputBuffer.append( num_user_reviews );
    inputBuffer.append(" 15:");
    inputBuffer.append( budget );
    inputBuffer.append(" 16:");
    inputBuffer.append( year );
    inputBuffer.append(" 17:");
    inputBuffer.append( actor2_fb_likes );
    inputBuffer.append(" 18:");
    inputBuffer.append(0);
    String genreString = Utils.genreColumnFiller(genre);
    String[] genreList = genreString.split(",");
	int k=19;
	for(int  i = 0; i<genreList.length;i++){
		inputBuffer.append(" "+k+":");
		inputBuffer.append(genreList[i]);
		k++;
	}
    inputBuffer.append(" 39:");
    inputBuffer.append(Utils.hashString(month));
    
    
    String inputStr = inputBuffer.toString();
    System.out.println("String to be written to file "+inputStr);
    
    String pathFile = "/opt/tomcat/webapps/boxofficewebapi-0.0.1-SNAPSHOT/WEB-INF/classes/testSvm.txt";
    FileOutputStream fileOut;
	try {
		fileOut = new FileOutputStream(pathFile);
	    fileOut.write(inputStr.getBytes());
	    fileOut.close();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

	System.out.println("Invoking the Spark Job");
	String resultFate = Utils.InvokeSparkJob();
	
    FacebookFetcher fb = new FacebookFetcher();
    YouTubeFetcher yt = new YouTubeFetcher();
    String pageIdId = fb.getPageId(movie);
  String videoId = fb.getVideoId(pageIdId);
   
    String genreIds = "";
    for(String str : genre.split(",")){
    	int in = Utils.getGenreIDbyName(str);
    	genreIds = genreIds + ","+ String.valueOf(in);
    }
    mongoCRUD.insert(fb.getReactions(videoId, movie, genreIds).toString(), "boxoffice", "fbreactions", "results");
  String youtubeVideoId = yt.getVideoId(movie);
  JSONObject stats = yt.getStatistics(youtubeVideoId);
  String comments = yt.getComments(youtubeVideoId);
  System.out.println(stats);
  System.out.println(comments);
  

   JSONObject result = yt.returnJson(movie,stats,comments);
   mongoCRUD.insert(result.toString(), "boxoffice", "youtubeStats", "results");
   
   
   DataAggregrator dataAggregrator = new DataAggregrator();
    dataAggregrator.getMovieDetails(movie,resultFate);
    
    return Response.status(201).entity(fate).build();


}


}
