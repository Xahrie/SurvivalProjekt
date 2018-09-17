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
      final Player p = (Player) sender;

      if (CommandUtils.checkWorld(p)) {
        if (args.length == 1) {
          evaluateOneArgument(p, args);
        } else if (args.length == 2) {
          evaluateTwoArguments(p, args);
        } else if (args.length == 3) {
          evaluateThreeArguments(p, args);
        } else {
          sendHelp(p);
        }
      }

    }
    return false;
  }

  /**
   * Erstellt, Sucht und Löscht die eigene Zone
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void evaluateOneArgument(final Player p, final String[] args) {
    if (args[0].equalsIgnoreCase("create")) {
      createZone(SurvivalPlayer.findSurvivalPlayer(p));
    } else if (args[0].equalsIgnoreCase("search")) {
      searchZone(SurvivalPlayer.findSurvivalPlayer(p));
    } else if (args[0].equalsIgnoreCase("delete")) {
      delete(p);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(p);
    } else {
      sendHelp(p);
    }
  }

  /**
   * Mitglieder hinzufuegen, entfernen oder abfragen
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void evaluateTwoArguments(final Player p, final String[] args) {
    if (args[0].equalsIgnoreCase("add")) {
      add(p, args);
    } else if (args[0].equalsIgnoreCase("remove")) {
      remove(p, args);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(p, args);
    } else {
      sendHelp(p);
    }
  }

  /**
   * Laenge der Zone festlegen
   *
   * @param p Spieler
   * @param args Argumente des Command
   */
  private void evaluateThreeArguments(final Player p, final String[] args) {
    if (args[0].equalsIgnoreCase("setlength")) {
      setLength(p, args);
    } else {
      sendHelp(p);
    }
  }

  /**
   * loesche bestimmte Zone
   *
   * @param p Spieler
   */
  private void delete(final Player p) {
    final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, p.getUniqueId().toString(), false);

    deleteZone(p, region);
  }

  /**
   * Information ueber bestimmte Zone
   *
   * @param p Spieler
   */
  private void info(final Player p) {
    final ProtectedRegion region = Regions.checkRegionLocationIn(SurvivalData.getInstance().getDynmap().rg, p.getLocation());

    infoZone(region, p);
  }

  /**
   * Mitspieler zur Zone hinzufuegen
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void add(final Player p, final String[] args) {
    try {
      UUIDFetcher.getUUID(args[1], uuid -> UUIDFetcher.getName(uuid, name -> {
        final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, uuid.toString(), false);
        if (region != null && !region.getMembers().contains(uuid)) {
          region.getMembers().addPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
          p.sendMessage(Messages.PREFIX + " §7Du hast §e" + name + " §7zu deiner Zone hinzugefügt.");
        } else {
          p.sendMessage(Messages.PREFIX + " §e" + name + " §7ist bereits Mitglied deiner Zone.");
        }
      }));
    } catch (final Exception ex) {
      p.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  /**
   * Mitspieler entfernen
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void remove(final Player p, final String[] args) {
    try {
      UUIDFetcher.getUUID(args[1], uuid -> UUIDFetcher.getName(uuid,
          name -> {
            final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, uuid
                .toString(), false);

            if (region != null && region.getMembers().contains(uuid)) {
              region.getMembers().removePlayer(uuid);
              p.sendMessage(Messages.PREFIX + " §7Du hast §e" + name + " von deiner Zone entfernt.");
            } else {
              p.sendMessage(Messages.PREFIX + " §e" + name + " §7ist kein Mitglied deiner Zone.");
            }
          }));

    } catch (final Exception ex) {
      p.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  /**
   * Infoi ueber einen Spieler
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void info(final Player p, final String[] args) {
    final Group group = BungeeGroupManager.getGroupManager().getGroup(p);
    if (CommandUtils.isOperator(p, group)) {
      try {
        UUIDFetcher.getUUID(args[1], uuid -> {
          final Long time = Bukkit.getOfflinePlayer(uuid).getLastPlayed();
          final Long first = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
          final String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
          final String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));

          p.sendMessage(" ");
          p.sendMessage(Messages.PREFIX + " §7Zuletzt online§8: §c" + lastonline);
          p.sendMessage(Messages.PREFIX + " §7Erstes mal§8: §c" + firstonline);
          p.sendMessage(" ");
        });

      } catch (final Exception ex) {
        p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
      }
    }
  }

  /**
   * Laenge der Zone setzen
   *
   * @param p Spieler
   * @param args Argumente des Commands
   */
  private void setLength(final Player p, final String[] args) {
    final Group group = BungeeGroupManager.getGroupManager().getGroup(p);

    if (CommandUtils.isOperator(p, group)) {
      try {
        final Integer max = Integer.valueOf(args[2]);

        try {
          UUIDFetcher.getUUID(args[1], uuid -> {
            final SurvivalPlayer survivalPlayer = SurvivalData.getInstance().getPlayers().get(uuid);

            survivalPlayer.setMaxzone(max);
            UUIDFetcher.getName(uuid, name -> p.sendMessage(Messages.PREFIX + " §e" + name + " §7kann nun eine Zone mit der Länge §c" +
                max + " §7erstellen."));
          });

        } catch (final Exception ex) {
          p.sendMessage(Messages.PLAYER_NOT_FOUND);
        }
      } catch (final NumberFormatException ex) {
        p.sendMessage(Messages.USE_INTEGER);
      }
    }
  }

  /**
   * Zonenhilfe
   *
   * @param p Spieler
   */
  private void sendHelp(final Player p) {
    p.sendMessage(Messages.ZONE_HELP);
    if (p.isOp()) {
      p.sendMessage(Messages.ZONE_HELP_ADMIN);
    }
  }

  /**
   * Region erstellen
   *
   * @param survivalPlayer Spieler als SurvivalPlayer
   */
  private void createZone(final SurvivalPlayer survivalPlayer) {
    if (Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, survivalPlayer.getPlayer().getUniqueId().toString(), false) != null) {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_ALREADY_EXIST);
    } else {
      if (!survivalPlayer.isZonenedit()) {
        survivalPlayer.getPlayer().sendMessage(Messages.ZONE_EXPLAINATION);
      }
      survivalPlayer.setZonenedit(!survivalPlayer.isZonenedit());
    }
  }


  /**
   * Zone suchen
   *
   * @param survivalPlayer Spieler als SurvivalPlayer
   */
  private void searchZone(final SurvivalPlayer survivalPlayer) {
    if (survivalPlayer.isZonensearch()) {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_SEARCH_DISABLE);
    } else {
      survivalPlayer.getPlayer().sendMessage(Messages.ZONE_SEARCH_ENABLE);
    }
    survivalPlayer.setZonensearch(!survivalPlayer.isZonensearch());
  }

  /**
   * Zone loeschen
   *
   * @param p Spieler
   * @param region Zone
   */
  private void deleteZone(final Player p, final ProtectedRegion region) {
    if (region != null) {
      SurvivalData.getInstance().getDynmap().rg.removeRegion(region.getId());
      p.sendMessage(Messages.ZONE_REMOVED);
    } else {
      p.sendMessage(Messages.NO_ZONE_SET);
    }
  }

  /**
   * Zoneninfo
   *
   * @param region Zone
   * @param player Spieler
   */
  private void infoZone(final ProtectedRegion region, final Player player) {
    if (region != null) {
      UUIDFetcher.getName(UUID.fromString(region.getId()), name -> {
        player.sendMessage("\n" + Messages.PREFIX + " §7Besitzer§8: " + name);

        StringBuilder member = null;
        for (final UUID uuid : region.getOwners().getUniqueIds()) {
          member = (member == null) ? new StringBuilder(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid)) : member.append(", ")
              .append(SurvivalData.getInstance().getAsyncMySQL().getMySQL().getName(uuid));
        }
        player.sendMessage(Messages.PREFIX + " §7Mitglieder§8: " + member + "\n");
      });

    } else {
      player.sendMessage(Messages.PREFIX + " §cDu stehst in keiner Zone.");
    }
  }

}
