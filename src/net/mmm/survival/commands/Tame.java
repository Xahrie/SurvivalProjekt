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
      final Player player = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);

      survivalPlayer.setTamed(!survivalPlayer.isTamed());
      if (survivalPlayer.isTamed()) {
        player.sendMessage(Messages.TAME_DISABLE);
      } else {
        player.sendMessage(Messages.TAME_ENABLE);
      }
    }

    return false;
  }

}
