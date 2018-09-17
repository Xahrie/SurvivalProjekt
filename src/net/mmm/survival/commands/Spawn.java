package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
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
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

      if (!survivalPlayer.isTeleport()) {
        survivalPlayer.setTeleport(true);
        teleport(p, p.getWorld().getSpawnLocation());
      } else {
        p.sendMessage(Messages.PREFIX + " §7Du wirst bereits teleportiert.");
      }
    }
    return false;
  }

}
