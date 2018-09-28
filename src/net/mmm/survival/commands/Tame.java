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
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) sender, sender.getName());
      editTameStatus(executor);
    }

    return false;
  }

  private void editTameStatus(final SurvivalPlayer survivalPlayer) {
    survivalPlayer.setTamed(!survivalPlayer.isTamed());
    checkTame(survivalPlayer);
  }

  private void checkTame(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.isTamed()) {
      survivalPlayer.getPlayer().sendMessage(Messages.TAME_DISABLE);
    } else {
      survivalPlayer.getPlayer().sendMessage(Messages.TAME_ENABLE);
    }
  }

}
