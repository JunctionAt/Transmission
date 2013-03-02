package at.junction.transmission;

import at.junction.transmission.database.Mail;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class TransmissionListener implements Listener {
    private Transmission plugin;

    public TransmissionListener (Transmission instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        List<Mail> mails = plugin.mailTable.getUserMails(event.getPlayer().getName());
        if(!mails.isEmpty()) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "You have mail! Type /inbox to read.");
        }
    }
}