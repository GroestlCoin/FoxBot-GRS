package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 30.07.14.
 */
public class CommandQuote extends Command {
    private final FoxBot foxbot;


    /**
     * Gets a quote by id.
     * <p/>
     * Usage: .quote 1
     */
    public CommandQuote(FoxBot foxbot)
    {
        super("quote", "command.quote");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "Usage: !quote 35";
        int id = 0;
        if (args.length > 0)
        {

            if(args[0].length() > 0 && StringUtils.isNumeric(args[0])) {
                id = Integer.parseInt(args[0]);
                result = Database.getQuote(id);
            }

        }

        channel.send().message(result);


    }
}
