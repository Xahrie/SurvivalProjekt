package de.mmm.survival.commands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.mmm.survival.Survival;
import de.mmm.survival.util.Messages;
import de.mmm.survival.util.Regions;
import de.mmm.survival.util.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class Navi implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;

      if (checkArguments(p, args)) {
        if (Bukkit.getPlayer(args[0]) != null) {
          final UUID uuid = Bukkit.getPlayer(args[0]).getUniqueId();
          final String name = Bukkit.getPlayer(args[0]).getName();
          if (Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false) != null) {
            final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(),
                    false);
            p.sendMessage(Messages.PREFIX + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
            p.setCompassTarget(new Location(Bukkit.getWorld("world"), Objects.requireNonNull(region)
                    .getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint()
                    .getBlockZ()));
          }
        } else {
          //TODO (BlueIronGirl): Warum nicht in beiden Faellen per UUIDFetcher ?
          try {
            UUIDFetcher.getUUID(args[0], uuid -> UUIDFetcher.getName(uuid, name -> {
              if (Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false) != null) {
                final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
                p.sendMessage(Messages.PREFIX + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
                p.setCompassTarget(new Location(Bukkit.getWorld("world"), Objects.requireNonNull(region)
                        .getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()));
              }
            }));
          } catch (final Exception ex) {
            p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
          }
        }
      }
    }

    return false;
  }

  private boolean checkArguments(final Player p, final String[] args) {
    if (args.length == 1) {
      return true;
    }
    p.sendMessage(Messages.PREFIX + " §cBenutze /navi <Spieler>");
    return false;
  }
}
