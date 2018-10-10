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
    if (CommandUtils.checkPlayer(commandSender) && CommandUtils.checkWorld((Player) commandSender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      evaluateHome(executor);
    }
    return false;
  }

  private void evaluateHome(final SurvivalPlayer executor) {
    final Player executorPlayer = executor.getPlayer();
    executor.setHome(executorPlayer.getLocation());
    executorPlayer.sendMessage(Messages.HOME_SET);
  }
}