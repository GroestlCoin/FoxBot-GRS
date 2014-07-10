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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xawksow on 09.07.14.
 */
public class CommandDgc extends Command {

    private final FoxBot foxbot;
    private final String address = "http://dgc.blockr.io/api/v1/coin/info";
    private final String exchangeAddress = "http://btc.blockr.io/api/v1/exchangerate/current";
    private final String networkHashAddress = "http://www.coinwarz.com/cryptocurrency/coins/digitalcoin";

    /**
     * Displays the current market values of dgc.
     * <p/>
     * Usage: .dgc
     */
    public CommandDgc(FoxBot foxbot)
    {
        super("dgc", "command.dgc");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();

        Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection conn2 = Jsoup.connect(exchangeAddress).ignoreContentType(true).followRedirects(true).timeout(1000);
        //Connection conn3 = Jsoup.connect(networkHashAddress).ignoreContentType(true).followRedirects(true).timeout(1000);
        JSONObject jsonObject;
        JSONObject exchangeObject;
        //JSONObject networkHashObject;

        try
        {
            jsonObject = new JSONObject(conn.get().text());
            exchangeObject = new JSONObject(conn2.get().text());
            //networkHashObject = new JSONObject(conn3.get().text());
        }
        catch (IOException ex)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        JSONObject lastBlock = jsonObject.getJSONObject("data").getJSONObject("last_block");
        JSONObject cryptsyValues = jsonObject.getJSONObject("data").getJSONObject("markets").getJSONObject("cryptsy");
        String[] cw = getCoinwarsInfo().split(":");

        String hashRate = String.format(Locale.US, "%.2f MH/s",Float.parseFloat(cw[0]));
        String volume = String.format(Locale.US, "%.8f BTC",Float.parseFloat(cw[1]));
        String difficulty = String.format(Locale.US,"%.8f", Float.parseFloat(lastBlock.getString("difficulty")));
        Double pricePf = cryptsyValues.getDouble("value");
        String price = String.format(Locale.US,"%.8f BTC", pricePf);
        String usd = String.format(Locale.US, "$%.2f", pricePf * (1 / exchangeObject.getJSONArray("data").getJSONObject(0).getJSONObject("rates").getDouble("BTC")));
        channel.send().message("Difficulty "+ difficulty +" | Price "+usd+"/"+price+" & 24h Volume "+volume+" (Cryptsy) | Network "+hashRate );


    }

    public String getCoinwarsInfo(){
        StringBuffer response = new StringBuffer();
        String hashRate = "0";
        try {

            URL obj = new URL(networkHashAddress);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "DGC Bot");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + networkHashAddress);
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
            //channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
        }
        String responseHTML = response.toString();
        Pattern datePatt = Pattern.compile("([0-9]*?\\.[0-9]*?)\\sMH/s");
        Pattern volumePattern = Pattern.compile("([0-9]*?\\.[0-9]*?)\\sBTC");
        Matcher volumeMatch = volumePattern.matcher(responseHTML);
        Matcher m = datePatt.matcher(responseHTML);

        if(m.find()) {
            hashRate = m.group(1);
        }
        if(volumeMatch.find())
            hashRate += ":"+volumeMatch.group(1);
       // hashRate = m.group(1);
        return hashRate;

    }

}
