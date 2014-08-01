package co.foxdev.foxbot.utils.database;


import org.apache.commons.lang3.time.DateUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * Created by xawksow on 30.07.14.
 */
public class Database {

    private static Connection connection = null;

    public Database (){

        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:bot.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");

    }

    public static void initialize(){
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:bot.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public static int addQuote(String user, String text){
        int id = 0;
        if(connection == null)
            initialize();
        try {
            Statement stmt = connection.createStatement();
            String sql = "INSERT INTO quotes (user,text,date) " +
                    "VALUES ('"+user+"', '"+text+"', '"+System.currentTimeMillis()+"' );";

            stmt.executeUpdate(sql);

            stmt.close();

            stmt = connection.createStatement();
            sql = "SELECT MAX(id) from quotes;";
            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
                id = rs.getInt(1);
            stmt.close();
            connection.commit();
        }
        catch (Exception e) {

        }

        return id;
    }

    public static ArrayList<String> findQuote(String searchText){
        if(connection == null)
            initialize();
        ArrayList<String> quoteList = new ArrayList<String>();
           try {
               Statement stmt = connection.createStatement();
               ResultSet rs = stmt.executeQuery( "SELECT * FROM quotes where user LIKE '%"+searchText+"%' OR text LIKE '%"+searchText+"%' ORDER BY id DESC LIMIT 0,5;");
               while ( rs.next() ) {
                   int id = rs.getInt("id");
                   String user = rs.getString("user");
                   String date  = rs.getString("date");
                   String text = rs.getString("text");
                   Long unixTime = Long.parseLong(date);
                   Date realDate = new Date(unixTime);
                   SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");

                   quoteList.add("["+id+"] ["+text+"] by "+user+" at "+sdf.format(realDate));
               }
               rs.close();
               stmt.close();
           }
           catch (Exception e) {

           }
        return quoteList;
    }

    public static String getQuote(int id){
        if(connection == null)
            initialize();
        String quote = "Nothing found.";
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT * FROM quotes where id="+id+" ;";
            if(id == -1)
                sql = "SELECT * from quotes ORDER BY id DESC LIMIT 0,1;";
            ResultSet rs = stmt.executeQuery(sql);
            while ( rs.next() ) {
                int qid = rs.getInt("id");
                String user = rs.getString("user");
                String date  = rs.getString("date");
                String text = rs.getString("text");
                Long unixTime = Long.parseLong(date);
                Date realDate = new Date(unixTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");


                quote = "["+qid+"] ["+text+"] by "+user+" at "+sdf.format(realDate);
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            System.out.println("Get Quote failed with: "+e.getMessage());
        }

        return quote;
    }

    public static void addLastSeen(String user){
        int id = -1;
        if(connection == null)
            initialize();
        try {
            Statement stmt = connection.createStatement();
            String sql = "SELECT id from seen where user='"+user+"';";

            ResultSet rs = stmt.executeQuery(sql);
            while(rs.next())
                id = rs.getInt(1);
            stmt.close();

            stmt = connection.createStatement();
            if(id == -1)
                sql = "INSERT INTO seen (user,date) " +
                    "VALUES ('"+user+"', '"+System.currentTimeMillis()+"' );";
            else
                sql = "UPDATE seen SET date ='"+System.currentTimeMillis()+"' where id="+id+";";
            stmt.executeUpdate(sql);
            stmt.close();
            connection.commit();
        }
        catch (Exception e) {

        }
    }


    public static String getLastSeen(String user) {
        String result = "I have never seen "+user+"!";

        if(connection == null)
            initialize();

        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM seen where user='"+user+"' collate nocase;;");
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String usr = rs.getString("user");
                String date  = rs.getString("date");
                Long unixTime = Long.parseLong(date);
                Date realDate = new Date(unixTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");


                result = "I last saw "+usr+" at "+sdf.format(realDate);
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            System.out.println("Last seen user failed: "+e.getMessage());
        }

        return result;
    }

    public static String getRandomQuote(){
        if(connection == null)
            initialize();
        ArrayList<String> quoteList = new ArrayList<String>();
        String quote = "Nothing found.";
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM quotes;");
            while ( rs.next() ) {
                int id = rs.getInt("id");
                String user = rs.getString("user");
                String date  = rs.getString("date");
                String text = rs.getString("text");
                Long unixTime = Long.parseLong(date);
                Date realDate = new Date(unixTime);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");


                quoteList.add("["+id+"] ["+text+"] by "+user+" at "+sdf.format(realDate));
            }
            rs.close();
            stmt.close();
        }
        catch (Exception e) {
            System.out.println("Get Quote failed with: "+e.getMessage());
        }
        Random rand = new Random();


        if(quoteList.size() > 0)
            quote = quoteList.get(rand.nextInt(quoteList.size()));

        return quote;
    }


}
