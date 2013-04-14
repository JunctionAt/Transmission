package at.junction.transmission;

import org.bukkit.ChatColor;

public class Configuration {
    private Transmission plugin;

    public int MAX_MAILS;

    public Configuration(Transmission instance) {
        plugin = instance;
    }

    public void load() {
        MAX_MAILS = plugin.getConfig().getInt("max-mail");
    }
}