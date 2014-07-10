package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.Utils;
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
public class CommandBtc extends Command {

    private final FoxBot foxbot;
    private final String address = "http://btc.blockr.io/api/v1/coin/info";

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

        JSONObject lastBlock = jsonObject.getJSONObject("data").getJSONObject("last_block");
        JSONObject cryptsyValues = jsonObject.getJSONObject("data").getJSONObject("markets").getJSONObject("coinbase");

        String difficulty = String.format(Locale.US,"%.1f", Float.parseFloat(lastBlock.getString("difficulty")));
        Double pricePf = cryptsyValues.getDouble("value");
        String price = String.format(Locale.US,"$%.2f", pricePf);

        channel.send().message("Difficulty "+ difficulty +" | Price "+price);

    }
}
