package at.junction.transmission;

import org.bukkit.ChatColor;

public class Configuration {
    private Transmission plugin;
    boolean RATE_LIMIT;
    int MESSAGES;
    int TIME;
    boolean MOTD;
    ChatColor MOTD_COLOR;
    String MOTD_MESSAGE;

    public Configuration(Transmission instance) {
        plugin = instance;
    }

    public void load() {
        RATE_LIMIT = plugin.getConfig().getBoolean("rate-limit", false);
        MESSAGES = plugin.getConfig().getInt("rate-limit-messages", 100);
        TIME = plugin.getConfig().getInt("rate-limit-time", 1);
        MOTD = plugin.getConfig().getBoolean("motd-enabled", false);
        MOTD_COLOR = ChatColor.valueOf(plugin.getConfig().getString("motd-color"));
        MOTD_MESSAGE = plugin.getConfig().getString("motd-message");
    }
}