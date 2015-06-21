package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.util.Locale;

/**
 * Created by xawksow on 24.07.14.
 */
public class CommandTrade extends Command {
    private final FoxBot foxbot;
    private String address = "https://poloniex.com/public?command=returnTicker";
    private String currency = "GRS";
    private float amount = 1000f;

    /**
     * Calculates the current market value of given coin.
     * <p/>
     * Usage: .trade 10 dgc
     */
    public CommandTrade(FoxBot foxbot) {
        super("trade", "command.trade");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        String info = "";
        Channel channel = event.getChannel();
        currency = "BTC_GRS";
        amount = 1000f;
        if (args.length > 0) {
            try {
                if (args[0].length() > 0)
                    amount = Float.parseFloat(args[0]);

            if (args.length > 1)
                if (args[1].length() > 0 && args[1].length() < 9) {
                    currency = args[1].replace("/", "_");
                }
            } catch (Exception e) {
                info = "Usage: !trade 1000 BTC/GRS";
            }
        } else
            info = "Usage: !trade 1000 BTC/GRS";

        try {
            if (info.equals(""))
                info = getPoloniexInfo();

            //networkHashObject = new JSONObject(conn3.get().text());
        } catch (Exception e) {
            foxbot.getLogger().error("Error occurred while performing Google search", e);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        channel.send().message(info);


    }

    public String getPoloniexInfo() {
        JSONObject jsonObject;

        try {
            Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);
            jsonObject = new JSONObject(conn.get().text());


        } catch (Exception e) {
            //channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return "";
        }

        final JSONObject pairJsonObject = jsonObject.getJSONObject(currency);

        double bid = pairJsonObject.getDouble("highestBid");
        double ask = pairJsonObject.getDouble("lowestAsk");
        double vol = pairJsonObject.getDouble("baseVolume");
        double last = pairJsonObject.getDouble("last");

        String cur1 = "";
        String cur2 = "";
        String[] curs = currency.toUpperCase().split("_");
        cur1 = curs[0];
        cur2 = curs[1];


        String resp = amount + " " + cur2 + " equals " + String.format(Locale.US, "%.8f " + cur1, last * amount);
        return resp;

    }


}
