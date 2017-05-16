package boxofficewebapi;

import com.mongodb.DBObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import boxofficewebapi.Utils;
import boxofficewebapi.MongoCRUD;
import com.mongodb.DBObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import org.json.JSONException;
import org.json.JSONObject;

public class DataAggregrator {

    public void getMovieDetails(String movieName,String fate) {
        MongoCRUD mongoCRUD = new MongoCRUD();
        new StringBuffer();
        HashMap map = new HashMap();
        System.out.println("-----------Mongocrud"+mongoCRUD);
        List fbReactionDbObjectList = mongoCRUD.retrieve("boxoffice", "fbreactions");
        DBObject dbObjectResult = null;
        Iterator itr = fbReactionDbObjectList.iterator();

        while(itr.hasNext()) {
            DBObject ytReactionDbObjectList = (DBObject)itr.next();
            if(ytReactionDbObjectList.get("movieName").toString().trim().equals(movieName)) {
                dbObjectResult = ytReactionDbObjectList;
                break;
            }
        }

        Set itr1 = dbObjectResult.keySet();
        Iterator ytReactionDbObjectList1 = itr1.iterator();

        while(ytReactionDbObjectList1.hasNext()) {
            String socialStats = (String)ytReactionDbObjectList1.next();
            map.put(socialStats, dbObjectResult.get(socialStats).toString());
        }

        List ytReactionDbObjectList2 = mongoCRUD.retrieve("boxoffice", "youtubeStats");
        Iterator socialStats1 = ytReactionDbObjectList2.iterator();

        while(socialStats1.hasNext()) {
            DBObject tmpStr = (DBObject)socialStats1.next();
            if(tmpStr.get("name").toString().trim().equals(movieName)) {
                dbObjectResult = tmpStr;
                break;
            }
        }

        itr1 = dbObjectResult.keySet();
        socialStats1 = itr1.iterator();

        while(socialStats1.hasNext()) {
            String tmpStr1 = (String)socialStats1.next();
            map.put(tmpStr1, dbObjectResult.get(tmpStr1).toString());
        }

        dbObjectResult.putAll(map);
        double[] socialStats2 = this.obtainSocialStats(map);
        dbObjectResult.put("socialScore", Double.valueOf(socialStats2[0]));
        System.out.println(socialStats2 + "For movie:" + movieName);
        dbObjectResult.put("pos", Double.valueOf(socialStats2[2]));
        dbObjectResult.put("neg", Double.valueOf(socialStats2[1]));
        dbObjectResult.put("neu", Double.valueOf(socialStats2[3]));
        dbObjectResult.put("fate", fate);
        mongoCRUD.insertDbObject("boxoffice", "movieDashBoard", dbObjectResult);
    }

    public double[] obtainSocialStats(Map<String, String> map) {
        DataAggregrator dataAggregrator = new DataAggregrator();
        double numFbLike = 0.0D;
        double numFbSad = 0.0D;
        double numFbAnger = 0.0D;
        double numFbWow = 0.0D;
        double numFbLaugh = 0.0D;
        double numFbLove = 0.0D;
        int numYbLikes = 0;
        int numYbDislikes = 0;
        int numYbViewCount = 0;
        double numYbCommentCount = 0.0D;
        double numYbFavoriteCount = 0.0D;
        double commentSentimentScore = 0.0D;
        int[] label = new int[3];
        HashSet genres = new HashSet();
        Set keyset = map.keySet();
        Iterator fb = keyset.iterator();

        while(true) {
            while(fb.hasNext()) {
                String tmpStr = (String)fb.next();
                if(tmpStr.equals("like")) {
                    numFbLike += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("love")) {
                    numFbLove += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("wow")) {
                    numFbWow += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("haha")) {
                    numFbLaugh += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("anger")) {
                    numFbAnger += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("sad")) {
                    numFbSad += (double)Integer.parseInt((String)map.get(tmpStr));
                } else if(tmpStr.equals("statistics")) {
                    try {
                        JSONObject var41 = new JSONObject((String)map.get(tmpStr));
                        numYbLikes += Integer.parseInt(var41.get("likeCount").toString());
                        numYbDislikes += Integer.parseInt(var41.get("dislikeCount").toString());
                        numYbViewCount += Integer.parseInt(var41.get("viewCount").toString());
                        if(var41.has("commentCount")) {
                            numYbCommentCount += (double)Integer.parseInt(var41.get("commentCount").toString());
                        }

                        numYbFavoriteCount += (double)Integer.parseInt(var41.get("favoriteCount").toString());
                    } catch (JSONException var37) {
                        var37.printStackTrace();
                    }
                } else {
                    int e;
                    if(tmpStr.equals("genreIds")) {
                        StringTokenizer var40 = new StringTokenizer((String)map.get("genreIds"), "[,]");

                        while(var40.hasMoreTokens()) {
                            e = Integer.parseInt(var40.nextToken().toString().trim());
                            genres.add(Utils.getGenrebyId(e));
                        }
                    } else if(tmpStr.equals("videocomments")) {
                        String yt = (String)map.get("videocomments");

                        try {
                            e = yt.split("\\$").length;
                            int sentiScore = 0;
                            String[] var32 = yt.split("\\$");
                            int var33 = var32.length;

                            for(int var34 = 0; var34 < var33; ++var34) {
                                String str = var32[var34];
                                int score = dataAggregrator.getSentimentAnalysisScore(str);
                                if(score == 1) {
                                    ++label[1];
                                } else if(score == -1) {
                                    ++label[0];
                                } else {
                                    ++label[2];
                                }

                                sentiScore += dataAggregrator.getSentimentAnalysisScore(str);
                            }

                            System.out.println("Sent for" + sentiScore);
                        } catch (Exception var38) {
                            System.out.println("Exception while sentiment analysis " + var38.getMessage());
                        }
                    }
                }
            }

            numFbLike += numFbLove * 1.1D;
            numFbLike += numFbWow * 1.2D;
            numFbLike -= numFbAnger * 1.2D;
            if(genres.contains("Comedy")) {
                numFbLike += numFbLaugh * 2.0D;
            } else if(!genres.contains("Drama") && !genres.contains("Documentary") && !genres.contains("History") && !genres.contains("War")) {
                if(!genres.contains("Comedy") && !genres.contains("Drama") && !genres.contains("Documentary") && !genres.contains("History") && genres.contains("War")) {
                    ;
                }
            } else {
                numFbSad *= 1.2D;
            }

            numFbLike -= numFbLaugh * 1.2D;
            double var39 = numFbLike * 100.0D / (numFbAnger + numFbLaugh + numFbLike + numFbLove + numFbWow + numFbLove + numFbSad);
            double var42 = ((double)numYbLikes + numYbFavoriteCount * 1.2D) * 90.0D / (double)(numYbLikes + numYbDislikes) + this.ytViewScore(numYbViewCount);
            return new double[]{(var39 + var42) / 2.0D, (double)label[0], (double)label[1], (double)label[2]};
        }
    }

    private double ytViewScore(int numYtviews) {
        double score = 0.0D;
        if(numYtviews < 800000) {
            score = 0.0D;
        } else if(numYtviews < 1600000) {
            score = 1.0D;
        } else if(numYtviews < 3200000) {
            score = 2.0D;
        } else if(numYtviews < 4800000) {
            score = 3.0D;
        } else if(numYtviews < 6400000) {
            score = 4.0D;
        } else if(numYtviews < 8000000) {
            score = 5.0D;
        } else if(numYtviews < 9600000) {
            score = 6.0D;
        } else if(numYtviews < 11200000) {
            score = 7.0D;
        } else if(numYtviews < 12800000) {
            score = 8.0D;
        } else if(numYtviews < 1440000) {
            score = 9.0D;
        } else if(numYtviews < 1600000) {
            score = 10.0D;
        }

        return score;
    }

    private int getSentimentAnalysisScore(String comments) throws IOException {
        String text = "text=" + comments;
        int score = 0;
        double result = 0.0D;
        URL obj = new URL("http://text-processing.com/api/sentiment/");
        HttpURLConnection con = (HttpURLConnection)obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", "Mozilla/5.0");
        con.setDoOutput(true);
        OutputStream os = con.getOutputStream();
        os.write(text.getBytes());
        os.flush();
        os.close();
        int responseCode = con.getResponseCode();

        try {
            if(responseCode == 200) {
                BufferedReader e = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuffer response = new StringBuffer();

                String inputLine;
                while((inputLine = e.readLine()) != null) {
                    response.append(inputLine);
                }

                e.close();
                new JSONObject(response.toString());
                score = this.returnLabelValue((new JSONObject(response.toString())).get("label").toString());
            } else {
                Thread.sleep(2000L);
                System.out.println("POST request not worked");
            }
        } catch (InterruptedException var14) {
            var14.printStackTrace();
        } catch (JSONException var15) {
            var15.printStackTrace();
        }

        return score;
    }

    private int returnLabelValue(String sentiment) {
        return sentiment.equals("neg")?-1:(sentiment.equals("pos")?1:0);
    }
}

