package net.mmm.survival.commands;

import net.mmm.survival.commands.base.TeleportBase;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /home Command
 */
public class Home extends TeleportBase implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      if (checkHome(survivalPlayer) && CommandUtils.checkTeleport(survivalPlayer)) {
        teleport((Player) commandSender, survivalPlayer.getHome());
      }
    }
    return false;
  }

  private boolean checkHome(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.getHome() == null) {
      final Player player = survivalPlayer.getPlayer();
      player.sendMessage(Messages.NO_HOME_SET);
      return false;
    }
    return true;
  }
}
