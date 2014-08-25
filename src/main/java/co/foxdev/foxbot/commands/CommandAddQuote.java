package co.foxdev.foxbot.commands;

import co.foxdev.foxbot.FoxBot;
import co.foxdev.foxbot.utils.database.Database;
import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.MessageEvent;

/**
 * Created by xawksow on 30.07.14.
 */
public class CommandAddQuote extends Command {

    private final FoxBot foxbot;


    /**
     * Adds a quote.
     * <p/>
     * Usage: .addquote your_quote
     */
    public CommandAddQuote(FoxBot foxbot)
    {
        super("addquote", "command.addquote");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();

        Channel channel = event.getChannel();
        String result = "Usage: !addquote your_quote";

        if (args.length > 0)
        {
            if (args[0].length() > 0)
            {
                String quote = event.getMessage();
                quote = quote.substring(quote.indexOf(" ")+1);
                quote = quote.replace("'", "");
                if(quote.length() > 3) {
                    int id = Database.addQuote(user.getNick(), quote);
                    result = "Successfully added with id "+id+"!";
                }
                else
                    result = "Quote too short!";
            }


        }

        channel.send().message(result);


    }
}
