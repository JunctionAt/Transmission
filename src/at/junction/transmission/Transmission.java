package at.junction.transmission;

import at.junction.transmission.database.Mail;
import at.junction.transmission.database.Mail.MailStatus;
import at.junction.transmission.database.MailTable;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import javax.persistence.PersistenceException;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Transmission extends JavaPlugin {
    MailTable mailTable;
    TransmissionListener listener;
    Configuration config;
    HashMap<String, String> lastMsg = new HashMap<>();

    @Override
    public void onEnable() {
        setupDatabase();
        mailTable = new MailTable(this);
        listener = new TransmissionListener(this);
        config = new Configuration(this);

        File cfile = new File(getDataFolder(), "config.yml");
		if(!cfile.exists()) {
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
        config.load();
    }

    @Override
    public void onDisable() {
    }

    public boolean setupDatabase() {
        try {
            getDatabase().find(Mail.class).findRowCount();
        } catch(PersistenceException ex) {
            getLogger().log(Level.INFO, "First run, initializing database.");
            installDDL();
            return true;
        }
        return false;
    }

    @Override
    public ArrayList<Class<?>> getDatabaseClasses() {
        ArrayList<Class<?>> list = new ArrayList<>();
        list.add(Mail.class);
        return list;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {
        if(command.getName().equalsIgnoreCase("msg")) {
            if(args.length < 2) {
                return false;
            } else {
                if(getServer().getPlayer(args[0]) != null) {
                    String message = args[1];
                    for(int i = 2; i < args.length; i++) {
                        message += " " + args[i];
                    }
                    sendMessage(getServer().getPlayer(args[0]).getName(), sender.getName(), message); // Using args[0] as the first argument for sendMessage may result in confusion.
                    return true;
                } else {
                    sender.sendMessage(ChatColor.RED + "That player is not online!");
                    return true;
                }
            }
        }

        if(command.getName().equalsIgnoreCase("reply")) {
            if(args.length < 1) {
                return false;
            } else {
                if(lastMsg.get(sender.getName()) == null) {
                    sender.sendMessage(ChatColor.RED + "Nobody has messaged you recently!");
                } else {
                    if(getServer().getPlayer(lastMsg.get(sender.getName())) != null || lastMsg.get(sender.getName()).equals("CONSOLE")) {
                        String message = args[0];
                        for(int i = 1; i < args.length; i++) {
                            message += " " + args[i];
                        }
                        sendMessage(lastMsg.get(sender.getName()), sender.getName(), message);
                        return true;
                    } else {
                        sender.sendMessage(ChatColor.RED + "That player is not online!");
                    }
                }
            }
            return true;
        }

        if(command.getName().equalsIgnoreCase("official")) {
            if(args.length < 1) {
                return false;
            } else {
                String message = args[0];
                for(int i = 1; i < args.length; i++) {
                    message += " " + args[i];
                }
                getServer().broadcastMessage("<" + config.OFFICIAL_COLOR + sender.getName() + ChatColor.RESET + "> " + config.OFFICIAL_COLOR + message);
                return true;
            }
        }

        if(command.getName().equalsIgnoreCase("mail")) {
            if(args.length < 2) {
                return false;
            } else {
                if(getServer().getOfflinePlayer(args[0]).hasPlayedBefore()) {
                    
                    String message = args[1];
                    for(int i = 2; i < args.length; i++) {
                        message += " " + args[i];
                    }

                    Mail mail = new Mail();
                    mail.setPlayerFrom(sender.getName());
                    mail.setPlayerTo(args[0]);
                    mail.setMail(message);
                    mail.setStatus(MailStatus.UNREAD);
                    mail.setMailTime(System.currentTimeMillis());

                    mailTable.save(mail);
                    sender.sendMessage(ChatColor.GOLD + "Mail sent!");

                    if(getServer().getPlayerExact(args[0]) != null) {
                        getServer().getPlayerExact(args[0]).sendMessage(ChatColor.GOLD + "You have mail! Type /inbox to read.");
                    }
                    return true;

                } else {
                    sender.sendMessage(ChatColor.RED + "That player doesn't exist or hasn't logged in before!");
                    return true;
                }
            }
        }

        if(command.getName().equalsIgnoreCase("inbox")) {
            List<Mail> mailList = mailTable.getUserMails(sender.getName());
            if(mailList.isEmpty()) {
                sender.sendMessage(ChatColor.RED + "You have no mails!");
                return true;
            }

            if(args.length > 0) { // Specific command.
                if(args[0].equals("read")) {
                    if(args.length == 2) {
                        try {
                            Mail mail = mailList.get(Integer.parseInt(args[1]) - 1);

                            sender.sendMessage(ChatColor.GOLD + "Mail #" + args[1]);
                            sender.sendMessage(ChatColor.GOLD + "From: " + mail.getPlayerFrom());
                            sender.sendMessage(ChatColor.GOLD + mail.getMail());
                            mail.setStatus(MailStatus.READ);
                            mailTable.save(mail);
                        } catch(Exception ex) {
                            sender.sendMessage(ChatColor.RED + "Invalid mail ID!");
                            return true;
                        }
                    } else {
                        return false;
                    }
                }

                if(args[0].equals("clear")) {
                    mailTable.clearReadMails(sender.getName());
                    sender.sendMessage(ChatColor.GOLD + "Cleared your inbox of unread messages.");
                }
            } else { // Show inbox.
                int i = 1;
                for(Mail mail : mailList) {
                    String time = timeStamp(mail.getMailTime());
                    sender.sendMessage(ChatColor.GOLD + "#" + i + " from " + mail.getPlayerFrom() + " on " + time);
                    i++;
                }
                return true;
            }
        }

        if(command.getName().equalsIgnoreCase("broadcast")) {
            if(args.length == 0) {
                return false;
            } else {
                String message = args[0];
                for(int i = 1; i < args.length; i++) {
                    message += " " + args[i];
                }
                getServer().broadcastMessage(config.OFFICIAL_COLOR + "[SERVER] " + message);
                return true;
            }
        }
        return true;
    }

    public void sendMessage(String to, String from, String message) {
        if(to.equals("CONSOLE")) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "From " + ChatColor.GOLD + from + ChatColor.LIGHT_PURPLE + ": " + message);
        } else {
            getServer().getPlayer(to).sendMessage(ChatColor.LIGHT_PURPLE + "From " + ChatColor.GOLD + from + ChatColor.LIGHT_PURPLE + ": " + message);
        }
        if(from.equals("CONSOLE")) {
            getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "To " + ChatColor.GOLD + to + ChatColor.LIGHT_PURPLE + ": " + message);
        } else {
            getServer().getPlayer(from).sendMessage(ChatColor.LIGHT_PURPLE + "To " + ChatColor.GOLD + to + ChatColor.LIGHT_PURPLE + ": " + message);
        }
        lastMsg.put(to, from);
    }

    private String timeStamp(long time) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(time);
        SimpleDateFormat format = new SimpleDateFormat("MMM.d@k.m.s");
        return format.format(cal.getTime());
    }
}