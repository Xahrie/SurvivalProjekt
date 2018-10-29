package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
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
    if (CommandUtils.checkMoney(amount, executor)) {
      evaluatePay(executor, target, amount);
    }
  }

  private void evaluatePay(final SurvivalPlayer executor, final SurvivalPlayer target, final int amount) {
    target.addOrTakeMoney(amount);
    executor.addOrTakeMoney(amount * -1);

    final Player executorPlayer = executor.getPlayer();
    final String targetPlayerName = UUIDUtils.getName(target.getUuid());
    executorPlayer.sendMessage(Messages.PREFIX + "Du hast §e" + targetPlayerName + " " + amount +
        Konst.CURRENCY + "§7 gezahlt.");

    final Player targetPlayer = target.getPlayer();
    if (targetPlayer != null) {
      targetPlayer.sendMessage(Messages.PREFIX + "Du hast von " + executorPlayer.getDisplayName() +
          amount + Konst.CURRENCY + " erhalten.");
    }
  }
}
