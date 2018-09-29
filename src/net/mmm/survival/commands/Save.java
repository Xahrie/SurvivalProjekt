package net.mmm.survival.commands;

import net.mmm.survival.Survival;
import net.mmm.survival.util.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /save Command
 * <p>
 * Nur fuer Testzwecke
 */
public class Save implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.isOperator((Player) commandSender)) {
      Survival.getInstance().save();
    }
    return false;
  }
}
