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
      final SurvivalPlayer executor = SurvivalPlayer
          .findSurvivalPlayer((Player) commandSender, commandSender.getName());
      checkArgumentLength(args, executor);
    }
    return false;
  }

  private void checkArgumentLength(final String[] strings, final SurvivalPlayer executor) {
    if (strings.length == 2) {
      evaluateTwoArguments(strings, executor);
    } else {
      executor.getPlayer().sendMessage(Messages.USAGE_PAY_COMMAND);
    }
  }

  private void evaluateTwoArguments(final String[] strings, final SurvivalPlayer executor) {
    final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(executor.getPlayer(), strings[0]);
    final int amount = CommandUtils.checkNumber(strings[1], executor.getPlayer());

    checkMoney(executor, target, amount);
  }

  private void checkMoney(final SurvivalPlayer executor, final SurvivalPlayer target, final int amount) {
    if (amount <= executor.getMoney()) {
      target.setMoney(target.getMoney() + amount);
      executor.setMoney(executor.getMoney() - amount);
      executor.getPlayer().sendMessage(Messages.PREFIX + "Du hast " + target.getPlayer()
          .getDisplayName() + amount + Konst.CURRENCY + " gezahlt.");
    } else {
      executor.getPlayer().sendMessage(Messages.NOT_ENOUGH_MONEY);
    }
  }
}
