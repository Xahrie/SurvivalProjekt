package de.mmm.survival.commands;

import de.mmm.survival.config.Config;
import de.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetHome implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      if (p.getWorld().getName().equals("world")) {
        //TODO: evtl. per Mysql
        Config.getInstance().getData().set("homes." + p.getUniqueId() + ".x", p.getLocation().getX());
        Config.getInstance().getData().set("homes." + p.getUniqueId() + ".y", p.getLocation().getY());
        Config.getInstance().getData().set("homes." + p.getUniqueId() + ".z", p.getLocation().getZ());
        Config.getInstance().save();
        p.sendMessage(Messages.PREFIX + " §7Du hast deinen Home-Punkt gesetzt.");
      } else {
        p.sendMessage(Messages.PREFIX + " §7Du kannst deinen Home-Punkt nur in der Hauptwelt setzen.");
      }
    }
    return false;
  }
}
