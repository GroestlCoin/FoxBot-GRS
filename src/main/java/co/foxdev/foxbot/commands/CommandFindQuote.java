package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by xawksow on 30.07.14.
 */
public class CommandFindQuote extends Command {

    private final FoxBot foxbot;

    /**
     * Searches a quote.
     * <p/>
     * Usage: .findquote your_quote
     */
    public CommandFindQuote(FoxBot foxbot)
    {
        super("findquote", "command.findquote");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "Usage: !findquote user/quote";
        ArrayList<String> list = null;
        if (args.length > 0)
        {
            if (args[0].length() > 0)
            {
                String quote = event.getMessage();
                quote = quote.substring(quote.indexOf(" ")+1);
                if(quote.length() > 3) {
                    list = Database.findQuote(quote);
                    if(list.size() == 0)
                        result = "Nothing found.";
                }
                else
                    result = "Search term too short.";
            }


        }
        if(list == null || list.size() == 0)
            channel.send().message(result);
        else
            for(String q : list) {
                channel.send().message(q);

            }


    }
}
