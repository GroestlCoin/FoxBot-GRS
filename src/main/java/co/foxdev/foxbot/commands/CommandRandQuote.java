package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 31.07.14.
 */
public class CommandRandQuote extends Command {

    private final FoxBot foxbot;


    /**
     * Gets a random Quote.
     * <p/>
     * Usage: .randquote
     */
    public CommandRandQuote(FoxBot foxbot)
    {
        super("randquote", "command.randquote");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "Usage: !randquote";

        result = Database.getRandomQuote();


        channel.send().message(result);


    }
}
