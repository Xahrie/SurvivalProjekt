package net.mmm.survival.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.Regions;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDFetcher;
import net.mmm.survival.util.UUIDUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /zone Command
 */
public class Zone implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final Player executor = (Player) commandSender;
      if (CommandUtils.checkWorld(executor)) {
        if (args.length == 1) {
          evaluateOneArgument(executor, args);
        } else if (args.length == 2) {
          evaluateTwoArguments(executor, args);
        } else if (args.length == 3) {
          evaluateThreeArguments(executor, args);
        } else {
          sendHelp(executor);
        }
      }
    }
    return false;
  }

  private void evaluateOneArgument(final Player executor, final String[] args) {
    if (args[0].equalsIgnoreCase("create")) {
      createZone(SurvivalPlayer.findSurvivalPlayer(executor));
    } else if (args[0].equalsIgnoreCase("search")) {
      searchZone(SurvivalPlayer.findSurvivalPlayer(executor));
    } else if (args[0].equalsIgnoreCase("delete")) {
      delete(executor);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(executor);
    } else {
      sendHelp(executor);
    }
  }

  private void evaluateTwoArguments(final Player executor, final String[] args) {
    if (args[0].equalsIgnoreCase("add")) {
      add(executor, args);
    } else if (args[0].equalsIgnoreCase("remove")) {
      remove(executor, args);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(executor, args);
    } else {
      sendHelp(executor);
    }
  }

  private void evaluateThreeArguments(final Player executor, final String[] args) {
    if (args[0].equalsIgnoreCase("setlength")) {
      setLength(executor, args);
    } else {
      sendHelp(executor);
    }
  }

  private void delete(final Player executor) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();
    final String uuid = executor.getUniqueId().toString();
    final ProtectedRegion region = Regions.checkExistingRegion(regionManager, uuid, false);
    deleteZone(executor, region);
  }

  private void info(final Player executor) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();
    final ProtectedRegion region = Regions.checkRegionLocationIn(regionManager, executor.getLocation());
    infoZone(region, executor);
  }

  private void add(final Player executor, final String[] args) {
    try {
      regionValidToAdd(executor, args[1]);
    } catch (final Exception ex) {
      executor.sendMessage(Messages.ZONE_NOT_SET);
    }
  }

  private void regionValidToAdd(final Player player, final String arg) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();
    UUIDFetcher.getUUID(arg, uuid -> UUIDFetcher.getName(uuid, name -> {
      final ProtectedRegion region = Regions.checkExistingRegion(regionManager, uuid.toString(), false);
      checkAdd(player, arg, uuid, name, region);
    }));
  }

  private void checkAdd(final Player player, final String arg, final UUID uuid, final String name, final ProtectedRegion region) {
    if (region != null && !region.getMembers().contains(uuid)) {
      region.getMembers().addPlayer(UUIDUtils.getPlayer(arg).getUniqueId());
      memberMessageBuilder(player, name, Messages.PREFIX, " §7Du hast §e", " §7zu deiner Zone hinzugefügt.");
    } else {
      memberMessageBuilder(player, name, Messages.PREFIX, " §e", " §7ist bereits Mitglied deiner Zone.");
    }
  }

  private void remove(final Player player, final String[] args) {
    try {
      regionValidToRemove(player, args[1]);
    } catch (final Exception ex) {
      player.sendMessage(Messages.ZONE_NOT_SET);
    }
  }

  private void regionValidToRemove(final Player player, final String arg) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();
    UUIDFetcher.getUUID(arg, uuid -> UUIDFetcher.getName(uuid, name -> {
      final ProtectedRegion region = Regions.checkExistingRegion(regionManager, uuid.toString(), false);
      if (region != null && region.getMembers().contains(uuid)) {
        region.getMembers().removePlayer(uuid);
        memberMessageBuilder(player, name, Messages.PREFIX, " §7Du hast §e", " von deiner Zone entfernt.");
      } else {
        memberMessageBuilder(player, name, Messages.PREFIX, " §e", " §7ist kein Mitglied deiner Zone.");
      }
    }));
  }

  private void memberMessageBuilder(final Player memeber, final String name, final String prefix, final String message, final String suffix) {
    memeber.sendMessage(prefix + message + name + suffix);
  }

  private void info(final Player executor, final String[] args) {
    if (CommandUtils.isOperator(executor)) {
      try {
        determinePlayerInfo(executor, args[1]);
      } catch (final Exception ex) {
        executor.sendMessage(Messages.PLAYER_NOT_FOUND);
      }
    }
  }

  private void determinePlayerInfo(final Player executor, final String arg) {
    UUIDFetcher.getUUID(arg, uuid -> {
      final Long time = Bukkit.getOfflinePlayer(uuid).getLastPlayed();
      final Long first = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
      final String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
      final String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));

      sendPlayerInfo(executor, lastonline, firstonline);
    });
  }

  private void sendPlayerInfo(final Player executor, final String lastonline, final String firstonline) {
    executor.sendMessage("");
    executor.sendMessage(Messages.PREFIX + " §7Zuletzt online§8: §c" + lastonline);
    executor.sendMessage(Messages.PREFIX + " §7Erstes mal§8: §c" + firstonline);
    executor.sendMessage("");
  }

  private void setLength(final Player executor, final String[] args) {
    if (CommandUtils.isOperator(executor)) {
      try {
        updateLength(executor, args[1], Integer.valueOf(args[2]));
      } catch (final NumberFormatException ignored) {
        executor.sendMessage(Messages.NOT_A_NUMBER);
      }
    }
  }

  private void updateLength(final Player executor, final String arg, final Integer max) {
    UUIDFetcher.getUUID(arg, uuid -> {
      final SurvivalPlayer toUpdate = SurvivalData.getInstance().getPlayers().get(uuid);
      if (toUpdate != null) {
        toUpdate.setMaxzone(max);
        UUIDFetcher.getName(uuid, name -> executor.sendMessage(Messages.PREFIX + " §e" + name +
            " §7kann nun eine Zone mit der Länge §c" + max + " §7erstellen."));
      } else {
        executor.sendMessage(Messages.PLAYER_NOT_FOUND);
      }

    });
  }

  private void sendHelp(final Player executor) {
    executor.sendMessage(Messages.ZONE_HELP);
    if (executor.isOp()) {
      executor.sendMessage(Messages.ZONE_HELP_ADMIN);
    }
  }

  private void createZone(final SurvivalPlayer creator) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();
    if (Regions.checkExistingRegion(regionManager, creator.getUuid().toString(), false) != null) {
      creator.getPlayer().sendMessage(Messages.ZONE_ALREADY_EXIST);
    } else {
      allowCreateZone(creator);
    }
  }

  private void allowCreateZone(final SurvivalPlayer creator) {
    if (!creator.isZonenedit()) {
      creator.getPlayer().sendMessage(Messages.ZONE_EXPLAINATION);
    }
    creator.setZonenedit(!creator.isZonenedit());
  }

  private void searchZone(final SurvivalPlayer finder) {
    if (finder.isZonensearch()) {
      finder.getPlayer().sendMessage(Messages.ZONE_SEARCH_DISABLE);
    } else {
      finder.getPlayer().sendMessage(Messages.ZONE_SEARCH_ENABLE);
    }
    finder.setZonensearch(!finder.isZonensearch());
  }

  private void deleteZone(final Player deleter, final ProtectedRegion region) {
    if (region != null) {
      SurvivalData.getInstance().getDynmap().getRegionManager().removeRegion(region.getId());
      deleter.sendMessage(Messages.ZONE_REMOVED);
    } else {
      deleter.sendMessage(Messages.ZONE_NOT_SET);
    }
  }

  private void infoZone(final ProtectedRegion region, final Player executor) {
    if (region != null) {
      sendZoneInfo(region, executor);
    } else {
      executor.sendMessage(Messages.ZONE_NOT_FOUND);
    }
  }

  private void sendZoneInfo(final ProtectedRegion region, final Player executor) {
    UUIDFetcher.getName(UUID.fromString(region.getId()), name -> {
      memberMessageBuilder(executor, " §7Besitzer§8: ", "\n", Messages.PREFIX, name);
      executor.sendMessage(Messages.PREFIX + " §7Mitglieder§8: " + getZoneInfo(region) + "\n");
    });
  }

  private StringBuilder getZoneInfo(final ProtectedRegion region) {
    final StringBuilder member = new StringBuilder(SurvivalData.getInstance().getAsyncMySQL()
        .getName(region.getOwners().getUniqueIds().iterator().next()));
    region.getOwners().getUniqueIds().forEach(uuid -> member.append(", ").append(SurvivalData
        .getInstance().getAsyncMySQL().getName(uuid)));
    return member;
  }

}
