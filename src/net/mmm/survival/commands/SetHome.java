package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /sethome Command
 */
public class SetHome implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      if (CommandUtils.checkWorld(executor.getPlayer())) {
        executor.setHome(executor.getPlayer().getLocation());
        executor.getPlayer().sendMessage(Messages.HOME_SET);
      }
    }
    return false;
  }

}
