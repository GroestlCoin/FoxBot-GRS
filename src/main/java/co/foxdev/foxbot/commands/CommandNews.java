package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xawksow on 10.07.14.
 */
public class CommandNews extends Command {
    private final FoxBot foxbot;
    private final String url = "http://digitalcoin.co/forums/SSI.php?ssi_function=recentTopics";

    public CommandNews(FoxBot foxbot){
        super("news", "command.news");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();

        StringBuffer response = new StringBuffer();
        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "DGC Bot");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + url);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
                in.close();
            }
        catch (Exception e) {
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
        }
        String responseHTML = response.toString();
        Pattern datePatt = Pattern.compile("<td valign=\"top\">\\s.*?<a href=\"(.*?)\">(.*?)</a>");
        Matcher m = datePatt.matcher(responseHTML);
        ArrayList<String> topics = new ArrayList<String>(10);
        while(m.find()) {
            topics.add(m.group(2) + " - "+m.group(1));
        }

        for(String msg : topics) {
            if(topics.indexOf(msg) > 5)
                break;
            channel.send().message(msg);
        }


        //print result
    }
}
