package net.mmm.survival.events;

import de.pas123.bungeegroupmanager.groups.Group;
import de.pas123.bungeegroupmanager.spigot.BungeeGroupManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * Events, wenn ein Spieler etwas schreibt
 *
 * @see org.bukkit.event.player.AsyncPlayerChatEvent
 */
public class ChatEvents implements Listener {
  /**
   * @param event AsyncPlayerChatEvent => Wenn ein Spieler chattet
   * @see org.bukkit.event.player.AsyncPlayerChatEvent
   */
  @EventHandler
  public void onChat(final AsyncPlayerChatEvent event) {
    final Player chatter = event.getPlayer();
    event.setCancelled(true);

    for (final Player all : Bukkit.getOnlinePlayers()) {
      final BungeeGroupManager groupManager = BungeeGroupManager.getGroupManager();
      final Group group = groupManager.getGroup(all);
      final String prefix = group.getPrefix();
      all.sendMessage(prefix + chatter.getName() + " §7» " + event.getMessage());
    }
  }

}
