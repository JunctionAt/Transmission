package at.junction.transmission;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;


public class Transmission extends JavaPlugin {

    TransmissionListener listener = new TransmissionListener(this);
    Configuration config = new Configuration(this);
    List<String> staffChatters = new ArrayList<>();
    HashMap<String, String> replyList = new HashMap<String, String>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(listener, this);

        File cfile = new File(getDataFolder(), "config.yml");
        if (!cfile.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        config.load();


        BukkitTask task = getServer().getScheduler().runTaskTimer(
                this, new BukkitRunnable() {
            @Override
            public void run() {
                if (config.ALERTS.size() > 0){
                    getServer().broadcastMessage(String.format("%s[%s] %s", config.ALERT_COLOR, config.ALERT_PREFIX, config.ALERTS.get()));
                }
            }
        }, config.ALERT_DELAY * 20, config.ALERT_PERIOD * 20);


    }

    @Override
    public void onDisable() {
        config.save();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if (command.getName().equalsIgnoreCase("staffchat")) {
            if (args.length == 0) { //Switch into StaffChat Mode
                if (staffChatters.contains(sender.getName())) { //Leave staff chat
                    sender.sendMessage(ChatColor.GOLD + "You are no longer talking in staff chat.");
                    staffChatters.remove(sender.getName());
                } else { //Enter staff chat
                    sender.sendMessage(ChatColor.GOLD + "You are now chatting in staff! Use /staffchat to swap back.");
                    staffChatters.add(sender.getName());
                }
            } else { //Send a single message to staff chat
                StringBuilder message = new StringBuilder();
                String playerName = (sender instanceof Player) ? ((Player) sender).getDisplayName() : sender.getName();
                for (int i = 0; i < args.length; i++) {
                    message.append(args[i]);
                    if (i != args.length - 1) {
                        message.append(" ");
                    }
                }
                String format = String.format(ChatColor.DARK_AQUA + "[S]<" + ChatColor.WHITE + "%1$s" + ChatColor.DARK_AQUA + "> " + ChatColor.RESET + "%2$s", playerName, message);
                for (Player p : getServer().getOnlinePlayers()) {
                    if (p.hasPermission("transmission.staffchat")) {
                        p.sendMessage(format);
                    }
                }
                getServer().getConsoleSender().sendMessage(format);
            }
        } else if (command.getName().equalsIgnoreCase("broadcast")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /o <message>. For official server business only.");
                return true;
            }
            StringBuilder message = new StringBuilder();
            for (String t : args) {
                message.append(t).append(" ");
            }
            getServer().broadcastMessage(ChatColor.WHITE + "<" + ChatColor.RED + sender.getName() + ChatColor.WHITE + "> " + ChatColor.GREEN + message.toString());
        } else if (command.getName().equalsIgnoreCase("ircsay")) {
            if (sender instanceof Player) {
                sender.sendMessage(ChatColor.RED + "ERROR:  " + ChatColor.MAGIC + "-------------------------------");
                return true;
            }
            StringBuilder message = new StringBuilder();
            for (String t : args) {
                message.append(t).append(" ");
            }
            getServer().broadcastMessage(ChatColor.GREEN + "[IRC] " + ChatColor.WHITE + message);

        } else if (command.getName().equalsIgnoreCase("reply")) {
            if (args.length < 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /r <message>");
                return true;
            }
            //They have recieved a message, send this message to the last person
            if (replyList.containsKey(sender.getName())) {
                Player reciever;
                if ((reciever = getServer().getPlayer(replyList.get(sender.getName()))) == null) { //If reciever is null, send message and return.
                    sender.sendMessage(ChatColor.RED + "That player is no longer online");
                    return true;
                }
                StringBuilder message = new StringBuilder();
                for (String t : args) {
                    message.append(t).append(" ");
                }
                sendMessage(sender, reciever, message.toString().substring(0, message.length() - 1));
                replyList.put(reciever.getName(), sender.getName());
            } else { //They have recieved messages, return. 
                sender.sendMessage(ChatColor.RED + "You haven't recieved any messages, therefore you can't reply.");
            }
        } else if (command.getName().equalsIgnoreCase("msg")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "Usage: /msg <player> <message>");
                return true;
            }
            Player reciever;
            if ((reciever = getServer().getPlayer(args[0])) == null) {
                sender.sendMessage("That player is not online");
                return true;
            }
            StringBuilder message = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            sendMessage(sender, reciever, message.toString().substring(0, message.length() - 1));
            replyList.put(reciever.getName(), sender.getName());
        } else if (command.getName().equalsIgnoreCase("list")) {
            StringBuilder players = new StringBuilder();
            for (Player p : getServer().getOnlinePlayers()) {
                players.append(p.getName()).append(", ");

            }
            sender.sendMessage("There are " + getServer().getOnlinePlayers().length + "/" + getServer().getMaxPlayers() + " players online:");
            sender.sendMessage(players.substring(0, players.length() - 2));
            sender.sendMessage(ChatColor.GREEN + "Please type /staff to see online staff members.");

        } else if (command.getName().equalsIgnoreCase("mute")) {
            if (args.length == 0) {
                String message = "Muted players: ";
                for (String pname : config.MUTED_PLAYERS) {
                    message += pname;
                    message += " ";
                }
                sender.sendMessage(ChatColor.RED + "Usage: /mute <playername>");
                sender.sendMessage(message);
            } else if (args.length == 1) {
                if (config.MUTED_PLAYERS.contains(args[0].toLowerCase())) {
                    sender.sendMessage(ChatColor.RED + "Player is already muted");
                } else {
                    config.MUTED_PLAYERS.add(args[0].toLowerCase());
                    sender.sendMessage(ChatColor.RED + "Player has been muted");
                    if (getServer().getPlayer(args[0]) instanceof Player) {
                        getServer().getPlayer(args[0]).sendMessage(ChatColor.RED + "You have been muted by " + sender.getName());
                    }
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Usage: /mute <playername>");
            }
        } else if (command.getName().equalsIgnoreCase("unmute")) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Usage: /unmute <player>");
            } else if (!config.MUTED_PLAYERS.contains(args[0].toLowerCase())) {
                sender.sendMessage(ChatColor.RED + "Player is not muted");
            } else {
                config.MUTED_PLAYERS.remove(args[0].toLowerCase());
                sender.sendMessage(ChatColor.RED + "Player was unmuted");
                if (getServer().getPlayer(args[0]) instanceof Player) {
                    getServer().getPlayer(args[0]).sendMessage(ChatColor.RED + "You have been unmuted by " + sender.getName());
                }
            }

        } else if (command.getName().equalsIgnoreCase("me")) {
            StringBuilder message = new StringBuilder();
            for (String word : args) {
                message.append(word).append(" ");
            }
            if (!config.MUTED_PLAYERS.contains(sender.getName().toLowerCase())) {
                getServer().broadcastMessage(String.format("* %s %s", sender.getName(), message.toString()));
            } else {
                sender.sendMessage(config.MUTE_MESSAGE);
            }
        } else if (command.getName().equalsIgnoreCase("transmission-reload")) {
            config.save();
            config.load();
            sender.sendMessage(ChatColor.RED + "Configuration reloaded");
        }
        return true;
    }

    public void sendMessage(CommandSender from, CommandSender to, String message) {
        if (config.MUTED_PLAYERS.contains(from.getName().toLowerCase())) {
            if (!to.hasPermission("Transmission.staff")) {
                from.sendMessage(config.MUTE_MESSAGE);
                return;
            }
        }
        String niceMessage = ChatColor.GRAY + "["
                + ChatColor.RED + "%s"
                + ChatColor.GRAY + " -> "
                + ChatColor.GOLD + "%s"
                + ChatColor.GRAY + "] "
                + ChatColor.WHITE + message.replace("%", "%%");
        from.sendMessage(String.format(niceMessage, "Me", to.getName()));
        to.sendMessage(String.format(niceMessage, from.getName(), "Me"));
    }
}
