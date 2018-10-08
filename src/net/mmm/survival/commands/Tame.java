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
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      editTameStatus(executor);
    }
    return false;
  }

  private void editTameStatus(final SurvivalPlayer executor) {
    executor.setTamed(!executor.isTamed());
    final Player executorPlayer = executor.getPlayer();
    executorPlayer.sendMessage(executor.isTamed() ? Messages.TAME_DISABLE : Messages.TAME_ENABLE);
  }

}
