package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Money implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      evaluateArgumentLength(args, survivalPlayer);
    }
    return false;
  }

  private void evaluateArgumentLength(final String[] strings, final SurvivalPlayer executor) {
    if (strings.length == 0) {
      evaluateZeroArguments(executor);
    } else if (strings.length == 1) {
      evaluateOneArgument(strings, executor.getPlayer());
    } else {
      final Player executorPlayer = executor.getPlayer();
      executorPlayer.sendMessage(CommandUtils.isOperator(executorPlayer) ? Messages.USAGE_MONEY_COMMAND
          : Messages.USAGE_MONEY_COMMAND_ADMIN);
    }
  }

  private void evaluateZeroArguments(final SurvivalPlayer executor) {
    final Player executorPlayer = executor.getPlayer();
    executorPlayer.sendMessage(Messages.PREFIX + "Kontostand von §e" + executorPlayer.getDisplayName() +
        "§7: §e" + Math.round(executor.getMoney() * 100) / 100.0 + Konst.CURRENCY + "§7.");
  }

  private void evaluateOneArgument(final String[] strings, final Player executor) {
    final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(executor, strings[0]);
    if (target != null) {
      evaluateZeroArguments(target);
    }
  }

}
