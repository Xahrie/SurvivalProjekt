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

/**
 * /eco-Command (get, take, set, reset)
 * <p>
 * /eco set Name amount
 * /eco take Name amount
 * /eco reset Name amount
 * /eco Name
 */
public class Economy implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;
      if (CommandUtils.isOperator(executor)) {
        checkCommandLength(args, executor);
      }
    }
    return false;
  }

  private void checkCommandLength(final String[] strings, final Player executor) {
    if (strings.length == 1) {
      evaluateOneArgument(strings[0], executor);
    } else if (strings.length == 3) {
      evaluateThreeArguments(strings, executor);
    } else {
      executor.sendMessage(Messages.USAGE_ECONOMY_COMMAND);
    }
  }

  private void evaluateOneArgument(final String string, final Player executor) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(executor, string);
    if (survivalPlayer != null) {
      final int money = survivalPlayer.getMoney();
      executor.sendMessage(Messages.PREFIX + "Der Spieler§e " + survivalPlayer.getPlayer()
          .getDisplayName() + "§7 hat §e" + money + Konst.CURRENCY + "§7 auf dem Konto.");
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void evaluateThreeArguments(final String[] strings, final Player executor) {
    final String argument = strings[0];
    final Player target = UUIDUtils.getPlayer(strings[1]);
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(executor, strings[1]);
    final int amount = CommandUtils.checkNumber(strings[2], executor);

    if (argument.equals("set")) { // Geld setzen
      updateMoney(executor, target, survivalPlayer, amount);
    } else if (argument.equals("take")) { // Geld wegnehmen
      if (amount <= survivalPlayer.getMoney())
        updateMoney(executor, target, survivalPlayer, survivalPlayer.getMoney() - amount);
    } else if (argument.equals("reset")) { // Geld zuruecksetzen
      updateMoney(executor, target, survivalPlayer, 0);
    } else if (argument.equals("add")) { // Geld hinzufuegen
      updateMoney(executor, target, survivalPlayer, survivalPlayer.getMoney() + amount);
    } else {
      executor.sendMessage(Messages.USAGE_ECONOMY_COMMAND);
    }
  }

  private void updateMoney(final Player executor, final Player target, final SurvivalPlayer survivalPlayer, final int amount) {
    survivalPlayer.setMoney(amount);
    if (target.isOnline()) {
      target.sendMessage(Messages.PREFIX + "Dein Kontostand wurde auf §e" + survivalPlayer.getMoney() +
          Konst.CURRENCY + "§7 gesetzt.");
    }
    executor.sendMessage(Messages.PREFIX + "Du hast den Kontostand von " + target.getDisplayName() +
        " auf " + survivalPlayer.getMoney() + Konst.CURRENCY + " gesetzt.");
  }
}
