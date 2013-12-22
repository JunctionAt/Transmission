package at.junction.transmission;

import org.bukkit.ChatColor;

class Configuration {
    private final Transmission plugin;
    boolean RATE_LIMIT;
    int MESSAGES;
    int TIME;

    public Configuration(Transmission instance) {
        plugin = instance;
    }

    public void load() {
        RATE_LIMIT = plugin.getConfig().getBoolean("rate-limit", false);
        MESSAGES = plugin.getConfig().getInt("rate-limit-messages", 100);
        TIME = plugin.getConfig().getInt("rate-limit-time", 1);
    }
}