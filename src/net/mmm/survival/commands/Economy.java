package net.mmm.survival.commands;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /economy-Command (get, take, set, reset)
 * <p>
 * /eco set Name amount
 * /eco take Name amount
 * /eco reset Name amount
 * /eco Name
 */
public class Economy implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;
      final Group group = BungeeGroupManager.getGroupManager().getGroup(executor);

      if (CommandUtils.isOperator(executor, group)) {
        checkCommandLength(strings, executor);
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

  private void evaluateOneArgument(final String string, final Player executor) { /* /eco <Name> */
    final Player target = Bukkit.getPlayer(string);
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(target);
    if (survivalPlayer != null) {
      final int money = survivalPlayer.getMoney();
      executor.sendMessage(Messages.PREFIX + "Der Spieler§e " + target.getDisplayName() + "§7 hat §e" + money + "€§7 auf dem Konto.");
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void evaluateThreeArguments(final String[] strings, final Player executor) {
    final String argument = strings[0];
    final Player target = Bukkit.getPlayer(strings[1]);
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(target);
    final int amount = CommandUtils.checkNumber(strings[2], executor);

    if (argument.equals("set")) {
      updateMoney(executor, target, survivalPlayer, amount);
    } else if (argument.equals("take")) {
      if (amount <= survivalPlayer.getMoney()) {
        updateMoney(executor, target, survivalPlayer, survivalPlayer.getMoney() - amount);
      }
    } else if (argument.equals("reset")) {
      updateMoney(executor, target, survivalPlayer, 0);
    } else if (argument.equals("add")) {
      updateMoney(executor, target, survivalPlayer, survivalPlayer.getMoney() + amount);
    } else {
      executor.sendMessage(Messages.USAGE_ECONOMY_COMMAND);
    }
  }

  private void updateMoney(final Player executor, final Player target, final SurvivalPlayer survivalPlayer, final int amount) {
    survivalPlayer.setMoney(amount);
    if (target.isOnline()) {
      target.sendMessage(Messages.PREFIX + "Dein Kontostand wurde auf §e" + survivalPlayer.getMoney() + "€§7 gesetzt.");
    }
    executor.sendMessage(Messages.PREFIX + "Du hast den Kontostand von " + target.getDisplayName() + " auf " + survivalPlayer.getMoney() + "€ gesetzt.");
  }
}
