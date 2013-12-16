package at.junction.transmission;


import org.bukkit.scheduler.BukkitRunnable;

public class rateLimitRemoverTask extends BukkitRunnable {

    TransmissionListener listener;
    int key;
    public rateLimitRemoverTask(TransmissionListener listener, int key){
        this.listener = listener;
        this.key = key;
    }

    public void run(){
        if (listener.rateLimit.containsKey(key)){
            listener.rateLimit.remove(key);
        }

    }
}
