package de.mmm.survival.commands;

import de.mmm.survival.Survival;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Spawn extends Teleport implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;

      if (Survival.getInstance().spawns.containsKey(p.getWorld().getName())) {
        if (!SurvivalPlayer.move.contains(p)) {
          SurvivalPlayer.move.add(p);
          teleport(p, Survival.getInstance().spawns.get(p.getWorld().getName()));
        } else {
          p.sendMessage(Messages.PREFIX + " §7Du wirst bereits teleportiert.");
        }
      } else {
        p.sendMessage(Messages.PREFIX + " §cDer Spawn wurde nicht gesetzt.");
      }
    }
    return false;
  }
}
