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
 * Created by xawksow on 24.07.14.
 */
public class CommandPool extends Command {

    private final FoxBot foxbot;
    private String address = ".poolerrangers.com/index.php?page=api&action=public&api_key=";

    /**
     * Displays the current market values of dgc.
     * <p/>
     * Usage: .dgc
     */
    public CommandPool(FoxBot foxbot)
    {
        super("pool", "command.pool");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        address = ".poolerrangers.com/index.php?page=api&action=public&api_key=";

        if (args.length > 0)
        {
            if (args[0].length() > 0 && args[0].length() < 5)
            {
                address = "http://"+args[0]+ address;
            }
            else
                address = "http://dgc"+ address;
        }
        else
            address = "http://dgc"+ address;

        Connection conn = Jsoup.connect(address).ignoreContentType(true).followRedirects(true).timeout(1000);

        //Connection conn3 = Jsoup.connect(networkHashAddress).ignoreContentType(true).followRedirects(true).timeout(1000);
        JSONObject jsonObject;


        try
        {
            jsonObject = new JSONObject(conn.get().text());

            //networkHashObject = new JSONObject(conn3.get().text());
        }
        catch (IOException ex)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }
        String poolName = jsonObject.getString("pool_name");
        String hashRate = String.format(Locale.US, "%.2f MH/s", jsonObject.getDouble("hashrate")/1024f);
        String workers = ""+jsonObject.getInt("workers");
        String shares = ""+jsonObject.getInt("shares_this_round");
        String lastblock = ""+jsonObject.getInt("last_block");
        String networkHash = String.format(Locale.US, "%.2f MH/s", jsonObject.getDouble("network_hashrate")/1024f/1024);
        channel.send().message(poolName + " # Hashrate "+hashRate + " # Active Workers "+workers+" # Round Shares "+shares+" # Last Block "+lastblock +" # Network Hashrate "+networkHash + " # "+address.substring(0,address.indexOf("/",10)));


    }

}
