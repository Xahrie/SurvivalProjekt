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
        evaluateCommandLength(args, executor);
      }
    }
    return false;
  }

  private void evaluateCommandLength(final String[] strings, final Player executor) {
    if (strings.length == 1) {
      evaluateOneArgument(strings[0], executor);
    } else if (strings.length == 3) {
      evaluateThreeArguments(strings, executor);
    } else {
      executor.sendMessage(Messages.USAGE_ECONOMY_COMMAND);
    }
  }

  private void evaluateOneArgument(final String targetPlayerName, final Player executor) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(executor, targetPlayerName);
    if (survivalPlayer != null) {
      final double money = survivalPlayer.getMoney();
      executor.sendMessage(Messages.PREFIX + "Der Spieler§e " + targetPlayerName + "§7 hat §e" +
          money + Konst.CURRENCY + "§7 auf dem Konto.");
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void evaluateThreeArguments(final String[] strings, final Player executor) {
    final String argument = strings[0];
    final SurvivalPlayer target = SurvivalPlayer.findSurvivalPlayer(UUIDUtils.getUUID(strings[1]));
    if (target != null) {
      final int amount = CommandUtils.stringToNumber(strings[2], executor);

      switch (argument) {
        case "set":  // Geld setzen
          updateMoney(executor, target, amount);
          break;
        case "take":  // Geld wegnehmen
          if (amount <= target.getMoney())
            updateMoney(executor, target, target.getMoney() - amount);
          break;
        case "reset":  // Geld zuruecksetzen
          updateMoney(executor, target, 0);
          break;
        case "add":  // Geld hinzufuegen
          updateMoney(executor, target, target.getMoney() + amount);
          break;
        default:
          executor.sendMessage(Messages.USAGE_ECONOMY_COMMAND);
          break;
      }
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void updateMoney(final Player executor, final SurvivalPlayer target, final double amount) {
    target.setMoney(amount);
    final Player targetPlayer = target.getPlayer();
    if (targetPlayer != null) {
      targetPlayer.sendMessage(Messages.PREFIX + "Dein Kontostand wurde auf §e" +
          target.getMoney() + Konst.CURRENCY + "§7 gesetzt.");
    }
    executor.sendMessage(Messages.PREFIX + "Du hast den Kontostand von " +
        UUIDUtils.getName(target.getUuid()) + " auf " + target.getMoney() + Konst.CURRENCY +
        " gesetzt.");
  }
}
