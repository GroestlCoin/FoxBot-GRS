package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 31.07.14.
 */
public class CommandLastQuote extends Command {

    private final FoxBot foxbot;


    /**
     * Gets the last added Quote;
     * <p/>
     * Usage: .lastquote
     */
    public CommandLastQuote(FoxBot foxbot)
    {
        super("lastquote", "command.lastquote");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "Usage: !lastquote";

        result = Database.getQuote(-1);


        channel.send().message(result);


    }

}
