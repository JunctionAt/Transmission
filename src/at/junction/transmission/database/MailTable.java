package at.junction.transmission.database;

import at.junction.transmission.Transmission;
import at.junction.transmission.database.Mail.MailStatus;
import com.avaje.ebean.Query;
import java.util.ArrayList;
import java.util.List;

public class MailTable {

    Transmission plugin;

    public MailTable(Transmission plugin) {
        this.plugin = plugin;
    }

    public List<Mail> getUserMails(String username) {
        List<Mail> retVal = new ArrayList<>();

        Query<Mail> query = plugin.getDatabase().find(Mail.class).where().ieq("playerTo", username).eq("status", MailStatus.UNREAD).query();

        if (query != null) {
            retVal.addAll(query.findList());
        }

        return retVal;
    }

    public int getInboxCount(String username) {
        int retVal = 0;
        Query<Mail> query = plugin.getDatabase().find(Mail.class).where().ieq("playerTo", username).in("status", MailStatus.UNREAD).query();

        if (query != null) {
            retVal = query.findRowCount();
        }

        return retVal;
    }

    public Mail getMail(int id) {
        Mail retVal = null;

        Query<Mail> query = plugin.getDatabase().find(Mail.class).where().eq("id", id).query();

        if (query != null) {
            retVal = query.findUnique();
        }

        return retVal;
    }

    public void clear() {
    }

    public void save(Mail Mail) {
        plugin.getDatabase().save(Mail);
    }

}