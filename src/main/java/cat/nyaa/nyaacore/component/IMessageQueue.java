package cat.nyaa.nyaacore.component;

import cat.nyaa.nyaacore.Message;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.DateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Send a message to a offline player so he will receive it when he login
 */
public interface IMessageQueue extends IComponent {

    /**
     * Send a message to a offline player
     *
     * @param player  recipient of the message
     * @param message content of the message
     */
    void send(OfflinePlayer player, Message message);

    /**
     * Send a message to a offline player, with a set
     *
     * @param player  recipient of the message
     * @param message content of the message
     */
    void send(OfflinePlayer player, Message message, long time);

    class DefaultMessageQueue implements IMessageQueue, Listener {

        static final Map<OfflinePlayer, Multimap<Long, Message>> messageStorage = new HashMap<>();

        @Override
        public boolean unload(IComponent successor) {
            HandlerList.unregisterAll(this);
            messageStorage.forEach(
                    (player, msgs) -> msgs.forEach(
                            (time, message) -> ((IMessageQueue) successor).send(player, message, time)
                    )
            );
            return true;
        }

        @Override
        public void send(OfflinePlayer player, Message message) {
            send(player, message, System.currentTimeMillis());
        }

        @Override
        public void send(OfflinePlayer player, Message message, long time) {
            messageStorage.compute(player, (uuid, msgs) -> {
                if (msgs == null) msgs = LinkedListMultimap.create();
                msgs.put(time, message);
                return msgs;
            });
        }

        @EventHandler
        public void onPlayerJoin(PlayerJoinEvent event) {
            Multimap<Long, Message> msgs = messageStorage.remove(event.getPlayer());
            if (msgs == null) return;
            msgs.forEach((time, msgJson) -> {
                Message message = new Message("").append(DateFormat.getDateTimeInstance().format(time) + ": {message}", Collections.singletonMap("{message}", msgJson.inner));
                message.send(event.getPlayer());
            });
        }
    }
}