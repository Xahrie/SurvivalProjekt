package net.mmm.survival.commands;

import java.util.Objects;

import net.mmm.survival.commands.base.TeleportBase;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.CommandUtils;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Description
 *
 * @author Abgie on 13.10.2018 14:32
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class TeleportWorld extends TeleportBase implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      if (args.length > 0) {
        final String sWorld = args[0];
        if (checkWorldExists(sWorld) && CommandUtils.checkTeleport(survivalPlayer)) {
          final World world = Objects.requireNonNull(SurvivalWorld.getWorld(sWorld)).get();
          teleport((Player) commandSender, world.getSpawnLocation());
        }
      }
    }
    return false;
  }

  private boolean checkWorldExists(final String sWorld) {
    return SurvivalWorld.getWorld(sWorld) != null;
  }
}
