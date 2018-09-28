package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Pay implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;
      final SurvivalPlayer survivalExecutor = SurvivalPlayer.findSurvivalPlayer(executor, executor.getName());
      checkArgumentLength(strings, executor, survivalExecutor);
    }

    return false;
  }

  private void checkArgumentLength(final String[] strings, final Player executor, final SurvivalPlayer survivalExecutor) {
    if (strings.length == 2) {
      evaluateTwoArguments(strings, executor, survivalExecutor);
    } else {
      executor.sendMessage(Messages.USAGE_PAY_COMMAND);
    }
  }

  private void evaluateTwoArguments(final String[] strings, final Player executor, final SurvivalPlayer survivalExecutor) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(executor, strings[0]);
    final int amount = CommandUtils.checkNumber(strings[1], executor);

    checkMoney(executor, survivalExecutor, survivalPlayer.getPlayer(), survivalPlayer, amount);
  }

  private void checkMoney(final Player executor, final SurvivalPlayer survivalExecutor, final Player target,
                          final SurvivalPlayer survivalPlayer, final int amount) {
    if (amount <= survivalExecutor.getMoney()) {
      survivalPlayer.setMoney(survivalPlayer.getMoney() + amount);
      survivalExecutor.setMoney(survivalExecutor.getMoney() - amount);
      executor.sendMessage(Messages.PREFIX + "Du hast " + target.getDisplayName() + amount + Konst.CURRENCY + " gezahlt.");
    } else {
      executor.sendMessage(Messages.NOT_ENOUGH_MONEY);
    }
  }
}
