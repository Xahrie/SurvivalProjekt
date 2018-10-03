package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Constants;
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
      checkArgumentLength(args, survivalPlayer);
    }

    return false;
  }

  private void checkArgumentLength(final String[] strings, final SurvivalPlayer executor) {
    if (strings.length == 0) {
      evaluateZeroArguments(executor);
    } else if (strings.length == 1) {
      evaluateOneArgument(strings, executor.getPlayer());
    } else {
      executor.getPlayer().sendMessage((CommandUtils.isOperator(executor.getPlayer())) ?
          Messages.USAGE_MONEY_COMMAND : Messages.USAGE_MONEY_COMMAND_ADMIN);
    }
  }

  private void evaluateZeroArguments(final SurvivalPlayer executor) {
    executor.getPlayer().sendMessage(Messages.PREFIX + "Kontostand von §e" + executor.getPlayer()
        .getDisplayName() + "§7: §e" + Math.round(executor.getMoney() * 100) / 100.0 + Constants.CURRENCY + "§7.");
  }

  private void evaluateOneArgument(final String[] strings, final Player executor) {
    final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(executor, strings[0]);
    if (target != null) {
      evaluateZeroArguments(target);
    }
  }

}
