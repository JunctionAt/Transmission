package at.junction.transmission;

import at.junction.transmission.database.Mail;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
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
    
    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        if(plugin.staffChatters.indexOf(event.getPlayer().getName()) != -1) {
            event.getRecipients().clear();
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                if(p.hasPermission("transmission.staffchat")) {
                    event.getRecipients().add(p);
                }
            }
            event.setFormat(ChatColor.DARK_AQUA + "[STAFF]<" + ChatColor.WHITE + "%1$s" + ChatColor.DARK_AQUA + "> " + ChatColor.RESET + "%2$s");
        }
    }
}