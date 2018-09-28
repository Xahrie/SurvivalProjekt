package net.mmm.survival.commands;

import java.util.Objects;
import java.util.UUID;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Regions;
import net.mmm.survival.util.UUIDFetcher;
import org.bukkit.Bukkit;
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
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;
      checkArguments(player, args);
    }

    return false;
  }

  private void checkArguments(final Player executor, final String[] args) {
    if (args.length == 1) {
      findRegion(args[0], executor);
    } else {
      executor.sendMessage(Messages.USAGE_NAVI_COMMAND);
    }
  }

  private void findRegion(final String args, final Player executor) {
    try {
      findPlayer(args, executor);
    } catch (final Exception ex) {
      executor.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

  private void findPlayer(final String args, final Player player) {
    UUIDFetcher.getUUID(args, uuid -> UUIDFetcher.getName(uuid, name -> checkRegion(player, uuid, name)));
  }

  private void checkRegion(final Player executor, final UUID uuid, final String name) {
    final ProtectedRegion region = getRegionWhenExists(uuid, executor);
    editCompassTarget(executor, region);
    executor.sendMessage(Messages.PREFIX + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
  }

  private ProtectedRegion getRegionWhenExists(final UUID uuid, final Player executor) {
    if (Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegionManager()/*.getRegion()*/, uuid.toString(), false) != null) {
      return Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegionManager()/*.getRegion()*/, uuid.toString(), false);
    } else {
      executor.sendMessage(Messages.ZONE_UNGUELTIG);
    }
    return null;
  }

  private void editCompassTarget(final Player executor, final ProtectedRegion region) {
    executor.setCompassTarget(new Location(Bukkit.getWorld("world"), Objects.requireNonNull(region).getMinimumPoint().getBlockX(),
        region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()));
  }

}
