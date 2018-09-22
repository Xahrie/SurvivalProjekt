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

public class Money implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(executor);
      final Group group = BungeeGroupManager.getGroupManager().getGroup(executor);

      checkArgumentLength(strings, executor, survivalPlayer, group);
    }

    return false;
  }

  private void checkArgumentLength(final String[] strings, final Player executor, final SurvivalPlayer survivalPlayer, final Group group) {
    if (strings.length == 0) {
      evaluateZeroArguments(executor, survivalPlayer, executor.getDisplayName());
    } else if (strings.length == 1) {
      evaluateOneArgument(strings, executor, group);
    } else {
      executor.sendMessage((CommandUtils.isOperator(executor, group)) ? Messages.USAGE_MONEY_COMMAND : Messages.USAGE_MONEY_COMMAND_ADMIN);
    }
  }

  private void evaluateZeroArguments(final Player executor, final SurvivalPlayer survivalPlayer, final String displayName) {
    executor.sendMessage(Messages.PREFIX + "Kontostand von §e" + displayName + "§7: §e" + survivalPlayer.getMoney() + "€§7.");
  }

  private void evaluateOneArgument(final String[] strings, final Player executor, final Group group) {
    final SurvivalPlayer survivalPlayer;
    if (CommandUtils.isOperator(executor, group)) {
      final Player target = Bukkit.getPlayer(strings[0]);
      survivalPlayer = SurvivalPlayer.findSurvivalPlayer(target);
      evaluateZeroArguments(executor, survivalPlayer, target.getDisplayName());
    }

  }

}
