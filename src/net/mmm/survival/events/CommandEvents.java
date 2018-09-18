package net.mmm.survival.events;

import net.mmm.survival.Survival;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 * Klasse CommandEvents ueberschreibt den Command:
 * <p>
 * /info
 *
 * @see org.bukkit.event.player.PlayerCommandPreprocessEvent
 */
public class CommandEvents implements Listener {
  @EventHandler(priority = EventPriority.HIGHEST)
  public void onCommandExecute(final PlayerCommandPreprocessEvent event) {
    final Player p = event.getPlayer();
    final String msg = event.getMessage().split(" ")[0];

    if (msg.equalsIgnoreCase("/info")) {
      Commands.info(p);
    }
  }

  private static class Commands {
    static void info(final CommandSender commandSender) {
      if (CommandUtils.checkPlayer(commandSender)) {
        commandSender.sendMessage(Messages.PREFIX + " Name: §8" + Survival.getInstance().getDescription().getName());
        commandSender.sendMessage(Messages.PREFIX + " Version: §8" + Survival.getInstance().getDescription().getVersion());
        commandSender.sendMessage(Messages.PREFIX + " Autoren: §8" + Survival.getInstance().getDescription().getAuthors().get(0));
        commandSender.sendMessage(Messages.PREFIX + " MC-Build: §8" + Survival.getInstance().getDescription().getAPIVersion());
      }
    }
  }
}
