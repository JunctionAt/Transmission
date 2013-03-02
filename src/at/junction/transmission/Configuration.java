package at.junction.transmission;

import org.bukkit.ChatColor;

public class Configuration {
    private Transmission plugin;

    public ChatColor OFFICIAL_COLOR;
    public int MAX_MAILS;

    public Configuration(Transmission instance) {
        plugin = instance;
    }

    public void load() {
        OFFICIAL_COLOR = ChatColor.valueOf(plugin.getConfig().getString("official-color"));
        MAX_MAILS = plugin.getConfig().getInt("max-mail");
    }
}