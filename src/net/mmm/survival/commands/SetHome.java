package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /sethome Command
 */
public class SetHome implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player p = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

      if (CommandUtils.checkWorld(p)) {
        survivalPlayer.setHome(p.getLocation());
        p.sendMessage(Messages.HOME_SET);
      }
    }

    return false;
  }

}
