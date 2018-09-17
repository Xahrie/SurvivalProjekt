package net.mmm.survival.events;

import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
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
   * Wenn ein Spieler chattet
   *
   * @param e AsyncPlayerChatEvent
   * @see org.bukkit.event.player.AsyncPlayerChatEvent
   */
  @EventHandler
  public void onChat(final AsyncPlayerChatEvent e) {
    final Player p = e.getPlayer();
    e.setCancelled(true);
    Bukkit.getOnlinePlayers().forEach(all -> all.sendMessage(BungeeGroupManager.getGroupManager().getPrefix(p) + p
        .getName() + " §7» §7" + e.getMessage()));

  }

}
