package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.io.IOException;
import java.util.Locale;

/**
 * Created by xawksow on 09.07.14.
 */
public class CommandDgc extends Command {

    private final FoxBot foxbot;

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
        String address = "http://dgc.blockr.io/api/v1/coin/info";
        String exchangeAddress = "http://btc.blockr.io/api/v1/exchangerate/current";
        String networkHashAddress = "https://www.miningpool.co/api/currencies";
        Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection conn2 = Jsoup.connect(exchangeAddress).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection conn3 = Jsoup.connect(networkHashAddress).ignoreContentType(true).followRedirects(true).timeout(1000);
        JSONObject jsonObject;
        JSONObject exchangeObject;
        JSONObject networkHashObject;

        try
        {
            jsonObject = new JSONObject(conn.get().text());
            exchangeObject = new JSONObject(conn2.get().text());
            networkHashObject = new JSONObject(conn3.get().text());
        }
        catch (IOException ex)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", Utils.munge(user.getNick()))));
            return;
        }

        JSONObject lastBlock = jsonObject.getJSONObject("data").getJSONObject("last_block");
        JSONObject cryptsyValues = jsonObject.getJSONObject("data").getJSONObject("markets").getJSONObject("cryptsy");

        String hashRate = String.format(Locale.US, "%.2f MH/s",networkHashObject.getJSONArray("return").getJSONObject(1).getInt("network_hashrate")/1024f/1024f);

        String difficulty = String.format(Locale.US,"%.8f", Float.parseFloat(lastBlock.getString("difficulty")));
        Double pricePf = cryptsyValues.getDouble("value");
        String price = String.format(Locale.US,"%.8fbtc", pricePf);
        String usd = String.format(Locale.US, "$%.2f", pricePf * (1 / exchangeObject.getJSONArray("data").getJSONObject(0).getJSONObject("rates").getDouble("BTC")));
        channel.send().message("Difficulty "+ difficulty +" | Price "+usd+"/"+price+" (Cryptsy) | Network "+hashRate);


    }
}
