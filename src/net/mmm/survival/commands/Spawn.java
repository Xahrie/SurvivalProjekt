package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /spawn Command
 */
public class Spawn extends Teleport implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);

      if (CommandUtils.checkTeleport(survivalPlayer)) {
        super.teleport(player, player.getWorld().getSpawnLocation());
      }
    }

    return false;
  }

}