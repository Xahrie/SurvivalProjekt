package net.mmm.survival.commands;

import net.mmm.survival.Survival;
import net.mmm.survival.util.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /home Command
 */
public class Save extends Teleport implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      if (CommandUtils.isOperator((Player) sender)) {
        Survival.getInstance().save();
      }
    }

    return false;
  }

}