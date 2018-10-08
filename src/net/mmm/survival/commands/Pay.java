package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      evaluateArgumentLength(args, executor);
    }
    return false;
  }

  private void evaluateArgumentLength(final String[] strings, final SurvivalPlayer executor) {
    if (strings.length == 2) {
      evaluateTwoArguments(strings, executor);
    } else {
      final Player executorPlayer = executor.getPlayer();
      executorPlayer.sendMessage(Messages.USAGE_PAY_COMMAND);
    }
  }

  private void evaluateTwoArguments(final String[] strings, final SurvivalPlayer executor) {
    final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(executor.getPlayer(), strings[0]);
    final int amount = CommandUtils.stringToNumber(strings[1], executor.getPlayer());
    if (checkPayIsValid(executor, amount)) {
      evaluatePay(executor, target, amount);
    }
  }

  private boolean checkPayIsValid(final SurvivalPlayer executor, final int amount) {
    if (amount <= executor.getMoney()) {
      return true;
    } else {
      executor.getPlayer().sendMessage(Messages.NOT_ENOUGH_MONEY);
    }

    return false;
  }

  private void evaluatePay(SurvivalPlayer executor, SurvivalPlayer target, int amount) {
    target.addOrTakeMoney(amount);
    executor.addOrTakeMoney(amount * -1);
    final Player executorPlayer = executor.getPlayer();
    final Player targetPlayer = target.getPlayer();
    executorPlayer.sendMessage(Messages.PREFIX + "Du hast " + targetPlayer.getDisplayName() +
        amount + Konst.CURRENCY + " gezahlt.");
    targetPlayer.sendMessage(Messages.PREFIX + "Du hast von " + executorPlayer.getDisplayName() +
        amount + Konst.CURRENCY + " erhalten.");
  }
}
