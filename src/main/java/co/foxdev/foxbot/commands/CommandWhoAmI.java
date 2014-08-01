package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 01.08.14.
 */
public class CommandWhoAmI extends Command {

    public String url = "http://goo.gl/e9aB8h";
    private final FoxBot foxbot;

    /**
     * Gives information about the bot.
     * <p/>
     * Usage: .whoami
     */
    public CommandWhoAmI(FoxBot foxbot)
    {
        super("whoami", "command.whoami");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "I am Coinelius Digitalis, a lovable little mascot made by the DGC Community. Selfie: "+url;


        channel.send().message(result);


    }
}
