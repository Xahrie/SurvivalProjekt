package de.mmm.survival.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.mmm.survival.Survival;
import de.mmm.survival.util.Events;
import de.mmm.survival.util.Messages;
import de.mmm.survival.util.Regions;
import de.mmm.survival.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.util.function.Consumer;

public class Zone implements CommandExecutor {

  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;

      if (p.getWorld().getName().equals("world")) {
        if (args.length == 1) {
          evaluateOneArgument(p, args);
        } else if (args.length == 2) {
          evaluateTwoArguments(p, args);
        } else if (args.length == 3) {
          evaluateThreeArguments(p, args);
        } else {
          sendHelp(p);
        }
      } else {
        p.sendMessage(Messages.PREFIX + " §cZonen Befehle kannst du nur in der Hauptwelt nutzen.");
      }

    }
    return false;
  }

  private void evaluateOneArgument(final Player p, final String[] args) {
    if (args[0].equalsIgnoreCase("create")) {
      create(p);
    } else if (args[0].equalsIgnoreCase("search")) {
      search(p);
    } else if (args[0].equalsIgnoreCase("delete")) {
      delete(p);
    } else if (args[0].equalsIgnoreCase("info")) {
      info(p);
    } else {
      sendHelp(p);
    }
  }

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

  private void evaluateThreeArguments(final Player p, final String[] args) {
    if (args[0].equalsIgnoreCase("setlength")) {
      setLength(p, args);
    } else {
      sendHelp(p);
    }
  }

  private void create(final Player p) {
    if (Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, p.getUniqueId().toString(), false) != null) {
      p.sendMessage(Messages.PREFIX + " §7Du hast bereits eine Zone.");
    } else {
      if (Events.zonenedit.contains(p)) {
        Events.zonenedit.remove(p);
      } else {
        Events.zonenedit.add(p);
        p.sendMessage(Messages.PREFIX + " §7Klicke mit einem Stock auf die erste Ecke deiner Zone, danach klicke auf die gegenüber liegende Ecke.");
      }
    }
  }

  private void search(final Player p) {
    if (Events.zonensearch.contains(p)) {
      Events.zonensearch.remove(p);
      p.sendMessage(Messages.PREFIX + " §7Du hast den Zonen-Suchmodus §cverlassen§7.");
    } else {
      p.sendMessage(Messages.PREFIX + " §7Zonen-Suchmodus §abetreten§7, klicke mit einem Stock auf den Boden um nach Zonen zu suchen.");
      Events.zonensearch.add(p);
    }
  }

  private void delete(final Player p) {
    final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, p.getUniqueId().toString(), false);
    if (region != null) {
      Survival.getInstance().dynmap.rg.removeRegion(region.getId());
      p.sendMessage(Messages.PREFIX + " §7Du hast deine Zone gelöscht.");
    } else {
      p.sendMessage(Messages.PREFIX + " §cDu hast keine Zone.");
    }
  }

  private void info(final Player p) {
    final ProtectedRegion region = Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg, p.getLocation());
    if (region != null) {
      UUIDFetcher.getName(UUID.fromString(region.getId()), new Consumer<String>() {

        @Override
        public void accept(final String name) {
          p.sendMessage(" ");
          p.sendMessage(Messages.PREFIX + " §7Besitzer§8: " + name);

          StringBuilder member = null;
          for (final UUID uuid : region.getOwners().getUniqueIds()) {
            if (member == null) {
              member = new StringBuilder(Survival.getInstance().async.getMySQL().getName(uuid));
            } else {
              member.append(", ").append(Survival.getInstance().async.getMySQL().getName(uuid));
            }
          }
          p.sendMessage(Messages.PREFIX + " §7Mitglieder§8: " + member);


          p.sendMessage(" ");
        }

      });

    } else {
      p.sendMessage(Messages.PREFIX + " §cDu stehst in keiner Zone.");
    }
  }

  private void add(final Player p, final String[] args) {
    try {
      if (Bukkit.getPlayer(args[1]) != null) {
        final Player player = Bukkit.getPlayer(args[1]);
        final UUID uuid = player.getUniqueId();
        final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
        if (region != null && !region.getMembers().contains(uuid)) {
          region.getMembers().addPlayer(uuid);
          p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getName() + " §7zu deiner Zone hinzugefügt.");
        } else {
          p.sendMessage(Messages.PREFIX + " §e" + player.getName() + " §7ist bereits Mitglied deiner Zone.");
        }
      } else {
        try {
          UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

            @Override
            public void accept(final UUID uuid) {
              UUIDFetcher.getName(uuid, new Consumer<String>() {

                @Override
                public void accept(final String name) {
                  final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
                  if (region != null && !region.getMembers().contains(uuid)) {
                    region.getMembers().addPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
                    p.sendMessage(Messages.PREFIX + " §7Du hast §e" + name + " §7zu deiner Zone hinzugefügt.");
                  } else {
                    p.sendMessage(Messages.PREFIX + " §e" + name + " §7ist bereits Mitglied deiner Zone.");
                  }
                }

              });
            }
          });
        } catch (final Exception ex) {
          p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
        }
      }
    } catch (final Exception ex) {
      p.sendMessage(Messages.PREFIX + " §7Du hast keine Zone.");
    }
  }

  private void remove(final Player p, final String[] args) {
    try {
      if (Bukkit.getPlayer(args[1]) != null) {
        final Player player = Bukkit.getPlayer(args[1]);
        final UUID uuid = player.getUniqueId();
        final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
        if (region != null && region.getMembers().contains(uuid)) {
          region.getMembers().removePlayer(uuid);
          p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getName() + " von deiner Zone entfernt.");
        } else {
          p.sendMessage(Messages.PREFIX + " §e" + player.getName() + " §7ist kein Mitglied deiner Zone.");
        }
      } else {
        try {
          UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

            @Override
            public void accept(final UUID uuid) {
              UUIDFetcher.getName(uuid, new Consumer<String>() {

                @Override
                public void accept(final String name) {
                  final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
                  if (region != null && region.getMembers().contains(uuid)) {
                    region.getMembers().removePlayer(uuid);
                    p.sendMessage(Messages.PREFIX + " §7Du hast §e" + name + " von deiner Zone entfernt.");
                  } else {
                    p.sendMessage(Messages.PREFIX + " §e" + name + " §7ist kein Mitglied deiner Zone.");
                  }
                }

              });
            }

          });
        } catch (final Exception ex) {
          p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
        }
      }
    } catch (final Exception ex) {
      p.sendMessage(Messages.PREFIX + " §7Du hast keine Zone.");
    }
  }

  private void info(final Player p, final String[] args) {
    if (p.isOp()) {
      if (Bukkit.getPlayer(args[1]) != null) {
        final Long time = Bukkit.getPlayer(args[1]).getLastPlayed();
        final Long first = Bukkit.getPlayer(args[1]).getFirstPlayed();
        final String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
        final String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));

        p.sendMessage(" ");
        p.sendMessage(Messages.PREFIX + " §7Zuletzt online§8: §c" + lastonline);
        p.sendMessage(Messages.PREFIX + " §7Erstes mal§8: §c" + firstonline);
        p.sendMessage(" ");
      } else {
        try {
          UUIDFetcher.getUUID(args[1], uuid -> {
            if (Bukkit.getOfflinePlayer(uuid) != null) {
              final Long time = Bukkit.getOfflinePlayer(uuid).getLastPlayed();
              final Long first = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
              final String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
              final String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));

              p.sendMessage(" ");
              p.sendMessage(Messages.PREFIX + " §7Zuletzt online§8: §c" + lastonline);
              p.sendMessage(Messages.PREFIX + " §7Erstes mal§8: §c" + firstonline);
              p.sendMessage(" ");
            }
          });
        } catch (final Exception ex) {
          p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
        }
      }
    }
  }


  private void setLength(final Player p, final String[] args) {
    if (p.isOp()) {
      try {
        final Integer max = Integer.valueOf(args[2]);
        if (Bukkit.getPlayer(args[1]) != null) {
          Events.maxzone.put(Bukkit.getPlayer(args[1]).getUniqueId(), max);
        } else {
          try {
            UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

              @Override
              public void accept(final UUID uuid) {
                Events.maxzone.put(uuid, max);
                UUIDFetcher.getName(uuid, new Consumer<String>() {

                  @Override
                  public void accept(final String name) {
                    p.sendMessage(Messages.PREFIX + " §e" + name + " §7kann nun eine Zone mit der Länge §c" + max + " §7erstellen.");
                  }

                });
              }

            });
          } catch (final Exception ex) {
            p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
          }
        }
      } catch (final Exception ex) {
        p.sendMessage(Messages.PREFIX + " §cDu musst eine Zahl angeben.");
      }
    } else {
      p.sendMessage(Messages.PREFIX + " §cDu hast nicht die benötigten Rechte dafür.");
    }
  }

  private void sendHelp(final Player p) {
    p.sendMessage(Messages.PREFIX + " §7Zonenhilfe§8:");
    p.sendMessage("§e/zone create §8┃ §7Erstellt eine Zone");
    p.sendMessage("§e/zone search §8┃ §7Sucht nach Zonen");
    p.sendMessage("§e/zone add <Spieler> §8┃ §7Fügt einen Spieler auf deine Zone hinzu");
    p.sendMessage("§e/zone remove <Spieler> §8┃ §7Entfernt einen Spieler von deiner Zone");
    p.sendMessage("§e/zone delete §8┃ §7Löscht deine Zone");
    if (p.isOp()) {
      p.sendMessage("§c/zone setlength <Spieler> <Anzahl> §8┃ §7Setzt die Max-Länge der Zone des Spielers");
    }
    if (p.isOp()) {
      p.sendMessage("§c/zone info <Spieler> §8┃ §7Zeigt Informationen über den Spieler an");
    }
    if (p.isOp()) {
      p.sendMessage("§c/zone info §8┃ §7Zeigt Informationen über die Zone in der du stehst an");
    }
  }

}
