package net.mmm.survival.events;

import net.mmm.survival.Survival;
import net.mmm.survival.util.Messages;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;

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
    final Player executor = event.getPlayer();
    final String message = event.getMessage().split(" ")[0];

    if (message.equalsIgnoreCase("/info")) {
      evaluateInfoCommand(executor);
    }
  }

  private void evaluateInfoCommand(final CommandSender commandSender) {
    final PluginDescriptionFile pluginDescription = Survival.getInstance().getDescription();
    commandSender.sendMessage(Messages.PREFIX + " Name: §8" + pluginDescription.getName());
    commandSender.sendMessage(Messages.PREFIX + " Version: §8" + pluginDescription.getVersion());
    commandSender.sendMessage(Messages.PREFIX + " Autoren: §8" + pluginDescription.getAuthors().get(0));
    commandSender.sendMessage(Messages.PREFIX + " MC-Build: §8" + pluginDescription.getAPIVersion());
  }
}
