package net.mmm.survival.commands;

import net.mmm.survival.commands.base.TeleportBase;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /spawn Command
 */
public class Spawn extends TeleportBase implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer teleported = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);

      if (CommandUtils.checkTeleport(teleported)) {
        final Player teleportedPlayer = teleported.getPlayer();
        final World targetWorld = teleportedPlayer.getWorld();
        teleport(teleportedPlayer, targetWorld.getSpawnLocation());
      }
    }
    return false;
  }
}
