package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 01.08.14.
 */
public class CommandWhoAmI extends Command {

    public String url = "http://imgur.com/lrhEYml";
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
        String result = "I am GroestlBot and Jackielove4u said i look like this: " + url;


        channel.send().message(result);


    }
}
