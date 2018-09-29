package net.mmm.survival.commands;

import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /setspawn Command
 */
public class SetSpawn implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (commandSender instanceof Player) {
      final Player teleported = (Player) commandSender;
      if (CommandUtils.isOperator(teleported)) {
        final World world = teleported.getWorld();
        world.setSpawnLocation(teleported.getLocation());
        teleported.sendMessage(Messages.SPAWN_SET);
      }
    }
    return false;
  }

}
