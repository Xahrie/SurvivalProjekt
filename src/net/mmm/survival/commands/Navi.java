package net.mmm.survival.commands;

import java.util.UUID;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;
import net.mmm.survival.regions.Regions;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /navi Command
 */
public class Navi implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player player = (Player) commandSender;
      evaluateArguments(player, args);
    }
    return false;
  }

  private void evaluateArguments(final Player executor, final String[] args) {
    if (args.length == 1) {
      findRegion(args[0], executor);
    } else {
      executor.sendMessage(Messages.USAGE_NAVI_COMMAND);
    }
  }

  private void findRegion(final String arg, final Player executor) {
    final UUID targetUUID = UUIDUtils.getUUID(arg);
    if (targetUUID != null) {
      evaluateRegion(executor, targetUUID, arg);
    } else {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void evaluateRegion(final Player executor, final UUID uuid, final String playerName) {
    final ProtectedRegion region = getRegionWhenExists(uuid, executor);
    if (region != null) {
      editCompassTarget(executor, region);
    }
    executor.sendMessage(Messages.PREFIX + " §7Dein Kompassziel wurde auf die Zone von §e" + playerName +
        " gesetzt.");
  }

  private ProtectedRegion getRegionWhenExists(final UUID uuid, final Player executor) {
    final DynmapWorldGuardPlugin dynmap = SurvivalData.getInstance().getDynmap();
    final ProtectedRegion protectedRegion = Regions.
        evaluateExistingRegion(dynmap.getRegionManager(), uuid.toString(), false);
    if (protectedRegion == null) {
      executor.sendMessage(Messages.ZONE_UNGUELTIG);
    }
    return protectedRegion;
  }

  private void editCompassTarget(final Player executor, final ProtectedRegion region) {
    final BlockVector minimumPoint = region.getMinimumPoint();
    final Location targetLocation = new Location(SurvivalWorld.BAUWELT.get(), minimumPoint.getBlockX(),
        minimumPoint.getBlockY(), minimumPoint.getBlockZ());
    executor.setCompassTarget(targetLocation);
  }
}
