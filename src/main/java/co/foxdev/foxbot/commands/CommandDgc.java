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
public class CommandDgc extends Command {

    private final FoxBot foxbot;

    private final String address = "http://dgc.blockr.io/api/v1/coin/info";
    private final String shaHash = "http://dgcsha.mining.wtf/index.php?page=api&action=public&api_key=";
    private final String scryptHash = "http://dgc.mining.wtf/index.php?page=api&action=public&api_key=";
    private final String x11Hash = "http://dgcx11.mining.wtf/index.php?page=api&action=public&api_key=";
    private final String cryptsy_dgc = "https://api.cryptsy.com/api/v2/markets/26";
    private final String cryptsy_btc = "https://api.cryptsy.com/api/v2/markets/2";

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
        Connection sha = Jsoup.connect(shaHash).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection scrypt = Jsoup.connect(scryptHash).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection x11 = Jsoup.connect(x11Hash).ignoreContentType(true).followRedirects(true).timeout(1000);
        Connection btc38DGC = Jsoup.connect(cryptsy_dgc).ignoreContentType(true).followRedirects(true).timeout(3500).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
        Connection btc38BTC = Jsoup.connect(cryptsy_btc).ignoreContentType(true).followRedirects(true).timeout(3500).userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
        JSONObject jsonObject;
        JSONObject cryptsyDGCObject;
        JSONObject cryptsyBTCObject;
        JSONObject shaObject;
        JSONObject scryptObject;
        JSONObject x11Object;

        try
        {
            jsonObject = new JSONObject(conn.get().text());
            shaObject = new JSONObject(sha.get().text());
            x11Object = new JSONObject(x11.get().text());
            scryptObject = new JSONObject(scrypt.get().text());
            cryptsyBTCObject = new JSONObject(btc38BTC.get().text());
            cryptsyDGCObject = new JSONObject(btc38DGC.get().text());
        }
        catch (IOException ex)
        {
            foxbot.getLogger().error("Error occurred while performing Google search", ex);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        JSONObject lastBlock = jsonObject.getJSONObject("data").getJSONObject("last_block");                    // get last block
        Double price = cryptsyDGCObject.getJSONObject("data").getJSONObject("last_trade").getDouble("price");   // get DGC/BTC price
        float volumeInfo = (float) cryptsyDGCObject.getJSONObject("data").getJSONObject("24hr").getDouble("volume_btc"); // get BTC volume
        String hashRate = "SHA: " + String.format(Locale.US, "%.2f", shaObject.getLong("network_hashrate") / 1000f / 1000f / 1000f) + " GHs / X11: " + String.format(Locale.US, "%.2f", x11Object.getLong("network_hashrate") / 1000f / 1000f / 1000f) + " GHs / Scrypt: " + String.format(Locale.US, "%.2f", scryptObject.getLong("network_hashrate") / 1000f / 1000f / 1000f) + " GHs";

        // format values
        String volume = String.format(Locale.US, "%.8f BTC", volumeInfo);
        String difficulty = String.format(Locale.US,"%.8f", Float.parseFloat(lastBlock.getString("difficulty")));
        String priceText = String.format(Locale.US, "%.8f BTC", price);
        String usd = String.format(Locale.US, "$%.2f", price * cryptsyBTCObject.getJSONObject("data").getJSONObject("last_trade").getDouble("price") * 1000f);
        channel.send().message("Difficulty " + difficulty + " | Price " + usd + "/1000 | " + priceText + " & 24h Volume " + volume + " (Cryptsy) | Network " + hashRate);


    }

}
