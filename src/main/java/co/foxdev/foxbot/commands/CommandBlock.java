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

/**
 * Created by xawksow on 14.12.14.
 */
public class CommandBlock extends Command {

    private final FoxBot foxbot;
    private String address = "http://chainz.cryptoid.info/dgc/api.dws?q=getblockcount";

    public CommandBlock(FoxBot foxbot) {
        super("block", "command.block");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        String info = "";
        Channel channel = event.getChannel();

        try {
            if (info.equals(""))
                info = getBlockCount();

            //networkHashObject = new JSONObject(conn3.get().text());
        } catch (Exception e) {
            foxbot.getLogger().error("Error occurred while performing Google search", e);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        channel.send().message(info);
    }

    public String getBlockCount() {
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
        } catch (Exception e) {
            //channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
        }
        String responseHTML = response.toString();

        resp = "Digitalcoin is currently on block " + responseHTML + ". :)";
        return resp;

    }

}
