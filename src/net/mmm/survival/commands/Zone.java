package net.mmm.survival.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Regions;
import net.mmm.survival.util.UUIDFetcher;
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
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;
      if (CommandUtils.checkWorld(player)) {
        if (args.length == 1) {
          evaluateOneArgument(player, args);
        } else if (args.length == 2) {
          evaluateTwoArguments(player, args);
        } else if (args.length == 3) {
          evaluateThreeArguments(player, args);
        } else {
          sendHelp(player);
        }
      }
    }
    return false;
  }

  private void evaluateOneArgument(final Player player, final String[] args) {
    if (args[0].equalsIgnoreCase("create")) {
      createZone(SurvivalPlayer.findSurvivalPlayer(player));
    } else if (args[0].equalsIgnoreCase("search")) {
      searchZone(SurvivalPlayer.findSurvivalPlayer(player));
    } else if (args[0].equalsIgnoreCase("delete")) {
      delete(player);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(player);
    } else {
      sendHelp(player);
    }
  }

  private void evaluateTwoArguments(final Player player, final String[] args) {
    if (args[0].equalsIgnoreCase("add")) {
      add(player, args);
    } else if (args[0].equalsIgnoreCase("remove")) {
      remove(player, args);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(player, args);
    } else {
      sendHelp(player);
    }
  }

  private void evaluateThreeArguments(final Player player, final String[] args) {
    if (args[0].equalsIgnoreCase("setlength")) {
      setLength(player, args);
    } else {
      sendHelp(player);
    }
  }

  private void delete(final Player player) {
    final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegion(), player.getUniqueId().toString(), false);

    deleteZone(player, region);
  }

  private void info(final Player player) {
    final ProtectedRegion region = Regions.checkRegionLocationIn(SurvivalData.getInstance().getDynmap().getRegion(), player.getLocation());

    infoZone(region, player);
  }

  private void add(final Player player, final String[] args) {
    try {
      regionValidToAdd(player, args[1]);
    } catch (final Exception ex) {
      player.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  private void regionValidToAdd(final Player player, final String arg) {
    UUIDFetcher.getUUID(arg, uuid -> UUIDFetcher.getName(uuid, name -> {
      final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegion(), uuid.toString(), false);
      if (region != null && !region.getMembers().contains(uuid)) {
        region.getMembers().addPlayer(Bukkit.getPlayer(arg).getUniqueId());
        isAlreadyMember(player, name, Messages.PREFIX, " §7Du hast §e", " §7zu deiner Zone hinzugefügt.");
      } else {
        isAlreadyMember(player, name, Messages.PREFIX, " §e", " §7ist bereits Mitglied deiner Zone.");
      }
    }));
  }

  private void remove(final Player player, final String[] args) {
    try {
      regionValidToRemove(player, args[1]);
    } catch (final Exception ex) {
      player.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  private void regionValidToRemove(final Player player, final String arg) {
    UUIDFetcher.getUUID(arg, uuid -> UUIDFetcher.getName(uuid, name -> {
      final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegion(), uuid.toString(), false);
      if (region != null && region.getMembers().contains(uuid)) {
        region.getMembers().removePlayer(uuid);
        isAlreadyMember(player, name, Messages.PREFIX, " §7Du hast §e", " von deiner Zone entfernt.");
      } else {
        isAlreadyMember(player, name, Messages.PREFIX, " §e", " §7ist kein Mitglied deiner Zone.");
      }
    }));
  }

  private void isAlreadyMember(final Player player, final String name, final String prefix, final String message, final String suffix) {
    player.sendMessage(prefix + message + name + suffix);
  }

  private void info(final Player player, final String[] args) {
    final Group group = BungeeGroupManager.getGroupManager().getGroup(player);
    if (CommandUtils.isOperator(player, group)) {
      try {
        determinePlayerInfo(player, args[1]);

      } catch (final Exception ex) {
        player.sendMessage(Messages.PLAYER_NOT_FOUND);
      }
    }
  }

  private void determinePlayerInfo(final Player player, final String arg) {
    UUIDFetcher.getUUID(arg, uuid -> {
      final Long time = Bukkit.getOfflinePlayer(uuid).getLastPlayed();
      final Long first = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
      final String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
      final String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));

      sendPlayerInfo(player, lastonline, firstonline);
    });
  }

  private void sendPlayerInfo(final Player player, final String lastonline, final String firstonline) {
    player.sendMessage(" ");
    player.sendMessage(Messages.PREFIX + " §7Zuletzt online§8: §c" + lastonline);
    player.sendMessage(Messages.PREFIX + " §7Erstes mal§8: §c" + firstonline);
    player.sendMessage(" ");
  }

  private void setLength(final Player player, final String[] args) {
    final Group group = BungeeGroupManager.getGroupManager().getGroup(player);

    if (CommandUtils.isOperator(player, group)) {
      try {
        updateLength(player, args[1], Integer.valueOf(args[2]));
      } catch (final NumberFormatException ex) {
        player.sendMessage(Messages.USE_INTEGER);
      } catch (final Exception ex) {
        player.sendMessage(Messages.PLAYER_NOT_FOUND);
      }
    }
  }

  private void updateLength(final Player player, final String arg, final Integer max) {
    UUIDFetcher.getUUID(arg, uuid -> {
      final SurvivalPlayer survivalPlayer = SurvivalData.getInstance().getPlayers().get(uuid);

      survivalPlayer.setMaxzone(max);
      UUIDFetcher.getName(uuid, name -> player.sendMessage(Messages.PREFIX + " §e" + name + " §7kann nun eine Zone mit der Länge §c" +
          max + " §7erstellen."));
    });
  }

  private void sendHelp(final Player player) {
    player.sendMessage(Messages.ZONE_HELP);
    if (player.isOp()) {
      player.sendMessage(Messages.ZONE_HELP_ADMIN);
    }
  }

  private void createZone(final SurvivalPlayer survivalPlayer) {
    if (Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().getRegion(), survivalPlayer.getPlayer().getUniqueId().toString(), false) != null) {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_ALREADY_EXIST);
    } else {
      allowCreateZone(survivalPlayer);
    }
  }

  private void allowCreateZone(final SurvivalPlayer survivalPlayer) {
    if (!survivalPlayer.isZonenedit()) {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_EXPLAINATION);
    }
    survivalPlayer.setZonenedit(!survivalPlayer.isZonenedit());
  }

  private void searchZone(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.isZonensearch()) {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_SEARCH_DISABLE);
    } else {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_SEARCH_ENABLE);
    }
    survivalPlayer.setZonensearch(!survivalPlayer.isZonensearch());
  }

  private void deleteZone(final Player player, final ProtectedRegion region) {
    if (region != null) {
      SurvivalData.getInstance().getDynmap().getRegion().removeRegion(region.getId());
      player.sendMessage(Messages.ZONE_REMOVED);
    } else {
      player.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  private void infoZone(final ProtectedRegion region, final Player player) {
    if (region != null) {
      sendZoneInfo(region, player);
    } else {
      player.sendMessage(Messages.PREFIX + " §cDu stehst in keiner Zone.");
    }
  }

  private void sendZoneInfo(final ProtectedRegion region, final Player player) {
    UUIDFetcher.getName(UUID.fromString(region.getId()), name -> {
      isAlreadyMember(player, " §7Besitzer§8: ", "\n", Messages.PREFIX, name);
      player.sendMessage(Messages.PREFIX + " §7Mitglieder§8: " + getZoneInfo(region) + "\n");
    });
  }

  private StringBuilder getZoneInfo(final ProtectedRegion region) {
    final StringBuilder member = new StringBuilder(SurvivalData.getInstance().getAsyncMySQL().getName(region.getOwners().getUniqueIds()
        .iterator().next()));
    region.getOwners().getUniqueIds().forEach(uuid -> member.append(", ").append(SurvivalData.getInstance().getAsyncMySQL().getName(uuid)));
    return member;
  }

}
