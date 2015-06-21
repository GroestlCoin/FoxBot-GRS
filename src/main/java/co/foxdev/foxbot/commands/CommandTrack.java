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
 * Created by xawksow on 06.08.14.
 */
public class CommandTrack extends Command {

    private final FoxBot foxbot;
    private String address = "http://chainz.cryptoid.info/grs/api.dws?q=getbalance&a=";


    public CommandTrack(FoxBot foxbot) {
        super("track", "command.track");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        String info = "";
        Channel channel = event.getChannel();
        String dgcAddress = "";
        if (args.length > 0) {
            if (args[0].length() > 4)
                dgcAddress = args[0];
            else
                info = "Address too short!";

        } else
            info = "Usage: !track GRS_ADDRESS";

        try {
            if (info.equals(""))
                info = getBalance(dgcAddress);

            //networkHashObject = new JSONObject(conn3.get().text());
        } catch (Exception e) {
            foxbot.getLogger().error("Error occurred while performing Google search", e);
            channel.send().message(Utils.colourise(String.format("(%s) &cSomething went wrong...", user.getNick())));
            return;
        }

        channel.send().message(info);
    }

    public String getBalance(String dgcAddress) {
        StringBuffer response = new StringBuffer();
        String resp = "";
        try {

            URL obj = new URL(address + dgcAddress);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // optional default is GET
            con.setRequestMethod("GET");

            //add request header
            con.setRequestProperty("User-Agent", "DGC Bot");

            int responseCode = con.getResponseCode();
            System.out.println("\nSending 'GET' request to URL : " + address + dgcAddress);
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

        resp = "Address " + dgcAddress + " is holding " + responseHTML + " GRS.";
        return resp;

    }


}
