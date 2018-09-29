package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /tame Command
 */
public class Tame implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender, commandSender.getName());
      editTameStatus(executor);
    }

    return false;
  }

  private void editTameStatus(final SurvivalPlayer executor) {
    executor.setTamed(!executor.isTamed());
    checkTame(executor);
  }

  private void checkTame(final SurvivalPlayer executor) {
    if (executor.isTamed()) {
      executor.getPlayer().sendMessage(Messages.TAME_DISABLE);
    } else {
      executor.getPlayer().sendMessage(Messages.TAME_ENABLE);
    }
  }

}
