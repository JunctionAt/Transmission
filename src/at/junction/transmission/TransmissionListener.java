package at.junction.transmission;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TransmissionListener implements Listener {
    private Transmission plugin;
    Map<Integer, String> rateLimit;
    public TransmissionListener (Transmission instance) {
        plugin = instance;
        rateLimit = new HashMap<Integer, String>();
    }
    
    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event) {
        if(plugin.staffChatters.contains(ChatColor.stripColor(event.getPlayer().getName()))) {
            event.getRecipients().clear();
            for(Player p : plugin.getServer().getOnlinePlayers()) {
                if(p.hasPermission("transmission.staffchat")) {
                    event.getRecipients().add(p);
                }
            }
            event.setFormat(ChatColor.DARK_AQUA + "[S]<" + ChatColor.WHITE + "%1$s" + ChatColor.DARK_AQUA + "> " + ChatColor.RESET + "%2$s");
            
        } else if ((plugin.getServer().getPluginManager().getPlugin("Tier2") != null) && 
                (event.getPlayer() instanceof Player) &&
                (event.getPlayer().hasMetadata("assistance"))){
        
            if (!event.getPlayer().getDisplayName().equals(event.getPlayer().getName())){
                event.setCancelled(true);
                plugin.getServer().broadcastMessage("<" + event.getPlayer().getName() + "> " + event.getMessage());
            }
        }

        //If player is in muted list, send to console only
        else if (plugin.mutedPlayers.contains(event.getPlayer().getName().toLowerCase())){
            event.getPlayer().sendMessage("You have been muted");
            plugin.getLogger().info(String.format("Muted Message: <%s> %s", event.getPlayer().getName(),  event.getMessage()));
            event.setCancelled(true);
            return;
        }

        if (plugin.config.RATE_LIMIT){
            Integer key = new Random().nextInt();
            rateLimit.put(key, event.getPlayer().getName());
            plugin.getServer().getScheduler().runTaskLater(plugin, new rateLimitRemoverTask(this, key), plugin.config.TIME * 20);
            if (rateLimit.values().contains(event.getPlayer().getName())){
                int rate = Collections.frequency(rateLimit.values(), event.getPlayer().getName());
                if (rate > plugin.config.MESSAGES){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Spam is bad. Don't spam.");
                    for (Player p : plugin.getServer().getOnlinePlayers()){
                        if (p.hasPermission("transmission.staffchat")) {
                            p.sendMessage(ChatColor.GRAY + "<" + event.getPlayer().getName() + "> " + event.getMessage() + " (muted by transmission)");
                        }
                        plugin.getLogger().info("<" + event.getPlayer().getName() + "> " + event.getMessage() + " (muted by transmission)");
                    }
                }

            }
        }
    }


}