package de.mmm.survival.commands;

import de.mmm.survival.config.Config;
import de.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      final String sWorldName = p.getWorld().getName();
      if (p.isOp()) {
        Config.getInstance().getData().set("Spawn." + sWorldName + ".x", p.getLocation().getX());
        Config.getInstance().getData().set("Spawn." + sWorldName + ".y", p.getLocation().getY());
        Config.getInstance().getData().set("Spawn." + sWorldName + ".z", p.getLocation().getZ());
        Config.getInstance().getData().set("Spawn." + sWorldName + ".yaw", p.getLocation().getYaw());
        Config.getInstance().getData().set("Spawn." + sWorldName + ".pitch", p.getLocation().getPitch());
        Config.getInstance().save();
        p.sendMessage(Messages.PREFIX + " §7Du hast den §eSpawn §7gesetzt.");
      } else {
        p.sendMessage(Messages.PREFIX + " §cDu hast nicht die benötigten Rechte dafür.");
      }
    }
    return false;
  }
}
