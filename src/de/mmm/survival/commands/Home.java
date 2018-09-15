package de.mmm.survival.commands;

import de.mmm.survival.config.Config;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Home extends Teleport implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      if (checkWorld(p) && checkConfig(p) && checkTeleport(p)) {
        SurvivalPlayer.move.add(p);
        super.teleport(p, new Location(Bukkit.getWorld("world"), Config.getInstance().getData().getDouble("homes."
                + p.getUniqueId() + ".x"), Config.getInstance().getData().getDouble("homes." + p.getUniqueId() + ".y"), Config.getInstance().getData().getDouble("homes." + p.getUniqueId() + ".z")));
      }
    }
    return false;
  }

  private boolean checkWorld(final Player p) {
    if (p.getWorld().getName().equals("world")) {
      return true;
    }
    p.sendMessage(Messages.PREFIX + " §7Du kannst deinen Home-Punkt nur in der Hauptwelt setzen.");
    return false;
  }

  private boolean checkConfig(final Player p) {
    if (Config.getInstance().getData().contains("homes." + p.getUniqueId())) {
      return true;
    }
    p.sendMessage(Messages.PREFIX + " §7Du hast noch keinen Home-Punkt gesetzt.");
    return false;

  }

  private boolean checkTeleport(final Player p) {
    if (!SurvivalPlayer.move.contains(p)) {
      return true;
    }
    p.sendMessage(Messages.PREFIX + " §7Du wirst bereits teleportiert.");
    return false;
  }
}
