package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

/**
 * Created by xawksow on 12.07.14.
 */
public class CommandGrs extends Command {

    private final String address = "https://poloniex.com/public?command=returnTicker";
    private final String networkHashAddress = "http://chainz.cryptoid.info/grs/api.dws?q=netmhashps";
    private final String difficultyAddress = "http://chainz.cryptoid.info/grs/api.dws?q=getdifficulty";
    private final FoxBot foxbot;

    public CommandGrs(FoxBot foxbot) {
        super("grs", "command.grs");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();

        Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);

        JSONObject jsonObject;


        try {
            jsonObject = new JSONObject(conn.get().text());

        } catch (IOException ex) {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        jsonObject = jsonObject.getJSONObject("BTC_GRS");
        String difficulty = getGroestlInfo(difficultyAddress);
        String networkHash = getGroestlInfo(networkHashAddress);

        double bid = jsonObject.getDouble("highestBid");
        double ask = jsonObject.getDouble("lowestAsk");
        double vol = jsonObject.getDouble("baseVolume");
        double last = jsonObject.getDouble("last");


        String price = String.format(Locale.US, "%.8f BTC", last);
        channel.send().message("Difficulty " + difficulty + " | Price " + price + " & 24h Volume " + vol + " (Poloniex) | Network " + networkHash + " MH/s");


    }

    public String getGroestlInfo(String url) {
        StringBuffer response = new StringBuffer();
        String resp = "";
        try {

            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "DGC Bot");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + address);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;


            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (Exception e) {
            //channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
        }

        return response.toString();
    }

}
