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
public class CommandBtc extends Command {

    private final FoxBot foxbot;
    private final String address = "http://btc.blockr.io/api/v1/coin/info";
    private String networkHashAddress = "http://www.coinwarz.com/cryptocurrency/coins/bitcoin";

    /**
     * Displays the current market values of btc.
     * <p/>
     * Usage: .btc
     */
    public CommandBtc(FoxBot foxbot)
    {
        super("btc", "command.btc");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();


        Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);

        JSONObject jsonObject;

        try
        {
            jsonObject = new JSONObject(conn.get().text());
        }
        catch (IOException ex)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }
        String[] cw = getCoinwarsInfo().split(":");
        JSONObject lastBlock = jsonObject.getJSONObject("data").getJSONObject("last_block");
        JSONObject cryptsyValues = jsonObject.getJSONObject("data").getJSONObject("markets").getJSONObject("coinbase");
        String hashRate = cw[0];
        String volume = String.format(Locale.US, "%.8f BTC",Float.parseFloat(cw[1]));
        String difficulty = String.format(Locale.US,"%.1f", Float.parseFloat(lastBlock.getString("difficulty")));
        Double pricePf = cryptsyValues.getDouble("value");
        String price = String.format(Locale.US,"$%.2f", pricePf);

        channel.send().message("Difficulty "+ difficulty +" | Price "+price+" & 24h Volume "+volume+" (Bitstamp) | Network "+hashRate );

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
        Pattern datePatt = Pattern.compile("([0-9]*?\\.[0-9]*?)\\s[a-zA-Z]H/s\\s*</td>");
        Pattern volumePattern = Pattern.compile("([0-9]*?\\.[0-9]*?)\\sBTC");
        Matcher volumeMatch = volumePattern.matcher(responseHTML);
        Matcher m = datePatt.matcher(responseHTML);

        if(m.find()) {
            hashRate = m.group(0);
        }
        hashRate = hashRate.substring(0, hashRate.lastIndexOf("s") + 1);
        if(volumeMatch.find())
            hashRate += ":"+volumeMatch.group(1);
        // hashRate = m.group(1);
        return hashRate;

    }


}
