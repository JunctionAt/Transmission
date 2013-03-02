package at.junction.transmission.database;

import com.avaje.ebean.validation.NotEmpty;
import com.avaje.ebean.validation.NotNull;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity()
@Table(name = "transmission")
public class Mail {

    public enum MailStatus {
        UNREAD, READ
    }

    @Id
    private int id;

    @NotNull
    private String playerFrom;

    @NotNull
    private String playerTo;

    @NotEmpty
    private String mail;

    @NotNull
    private long mailTime;

    @NotNull
    private MailStatus status;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void setPlayerFrom(String playerFrom) {
        this.playerFrom = playerFrom;
    }

    public String getPlayerFrom() {
        return this.playerFrom;
    }

    public void setPlayerTo(String playerTo) {
        this.playerTo = playerTo;
    }

    public String getPlayerTo() {
        return this.playerTo;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMailTime(long mailTime) {
        this.mailTime = mailTime;
    }

    public long getMailTime() {
        return this.mailTime;
    }

    public void setStatus(MailStatus status) {
        this.status = status;
    }

    public MailStatus getStatus() {
        return this.status;
    }
}