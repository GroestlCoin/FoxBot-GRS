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
public class CommandSeen extends Command {

    private final FoxBot foxbot;


    /**
     * Gets the last seen time of a user.
     * <p/>
     * Usage: .seen user
     */
    public CommandSeen(FoxBot foxbot)
    {
        super("seen", "command.seen");
        this.foxbot = foxbot;
    }

    @Override
    public void execute(MessageEvent event, String[] args) {
        User user = event.getUser();
        Channel channel = event.getChannel();
        String result = "Usage: !seen user";
        if (args.length > 0)
        {
            if(args[0].length() > 2) {
                String usrNick = args[0];
                boolean inChannel = false;
                for(User usr : channel.getUsers()) {
                    if(usr.getNick().equalsIgnoreCase(usrNick)) {
                        result = usrNick+" is currently in this channel!";
                        inChannel = true;
                        break;
                    }
                }
                if(!inChannel)
                    result = Database.getLastSeen(usrNick);
            }

        }

        channel.send().message(result);


    }

}
