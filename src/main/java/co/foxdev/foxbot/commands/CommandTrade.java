package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
import org.apache.commons.lang3.StringUtils;
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
 * Created by xawksow on 24.07.14.
 */
public class CommandTrade extends Command {
    private final FoxBot foxbot;
    private String address = "https://www.cryptsy.com";
    private String currency = "DGC";
    private String amount = "1";

    /**
     * Displays the current market values of dgc.
     * <p/>
     * Usage: .dgc
     */
    public CommandTrade(FoxBot foxbot)
    {
        super("trade", "command.trade");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        String info = "";
        Channel channel = event.getChannel();
        currency = "DGC";
        amount = "1";
        if (args.length > 0)
        {
            if(args[0].length() > 0 && StringUtils.isNumeric(args[0]))
                amount = args[0];

            if(args.length > 1)
                if (args[1].length() > 0 && args[1].length() < 5)
                {
                    currency = args[1];
                }
        }
        else
            info = "Usage: !trade 10 DGC";

        try
        {
            if(info.equals(""))
                info = getCryptsyInfo();

            //networkHashObject = new JSONObject(conn3.get().text());
        }
        catch (Exception e)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", e);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        channel.send().message(info);


    }

    public String getCryptsyInfo(){
        StringBuffer response = new StringBuffer();
        String resp = "";
        try {

            URL obj = new URL(address);
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
        }
        catch (Exception e) {
            //channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
        }
        String responseHTML = response.toString();
        Pattern btcUsd = Pattern.compile("BTC/USD .*?>([0-9]*?\\.[0-9]*?)</span>");
        Pattern curBtc = Pattern.compile(currency.toUpperCase()+"/BTC .*?>([0-9]*?\\.[0-9]*?)</span>");

        Matcher m = btcUsd.matcher(responseHTML);
        Matcher m2 = curBtc.matcher(responseHTML);
        String usdprice = "";
        String btcprice = "";
        if(m.find()) {
            usdprice = m.group(1);
        }
       if(m2.find())
           btcprice = m2.group(1);
        resp = amount + " " + currency.toUpperCase() + " equals " + String.format(Locale.US,"%.8f BTC",Float.parseFloat(btcprice)*Float.parseFloat(amount)) + " or "+ String.format(Locale.US,"%.2f$",(Float.parseFloat(btcprice)*Float.parseFloat(usdprice))*Float.parseFloat(amount));
        return resp;

    }


}
