package net.mmm.survival.commands;

import java.util.Objects;

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
      final Player p = (Player) sender;

      if (checkArguments(p, args)) {
        findRegion(args[0], p);
      }
    }

    return false;
  }

  /**
   * Erlaubtes Format verwendet
   *
   * @param args Argumente des Commands
   * @param p Spieler
   * @return boolean
   */
  private boolean checkArguments(final Player p, final String[] args) {
    if (args.length != 1) {
      p.sendMessage(Messages.USAGE_NAVI_COMMAND);
      return false;
    }

    return true;
  }

  /**
   * Finde die bestimmte Region eines Spielers
   *
   * @param args Argumente des Commands
   * @param p Spieler
   */
  private void findRegion(final String args, final Player p) {
    try {
      UUIDFetcher.getUUID(args, uuid -> UUIDFetcher.getName(uuid, name -> {
        if (Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, uuid.toString(), false) != null) {
          final ProtectedRegion region = Regions.checkExistingRegion(SurvivalData.getInstance().getDynmap().rg, uuid.toString(), false);

          p.sendMessage(Messages.PREFIX + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
          p.setCompassTarget(new Location(Bukkit.getWorld("world"), Objects.requireNonNull(region).getMinimumPoint().getBlockX(),
              region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()));
        }
      }));
    } catch (final Exception ex) {
      p.sendMessage(Messages.PLAYER_NOT_FOUND);
    }
  }

}
