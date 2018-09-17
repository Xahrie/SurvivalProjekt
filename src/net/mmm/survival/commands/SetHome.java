package net.mmm.survival.commands;

import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /sethome Command
 */
public class SetHome implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

      if (p.getWorld().getName().equals("world")) {
        survivalPlayer.setHome(p.getLocation());
        p.sendMessage(Messages.PREFIX + " §7Du hast deinen Home-Punkt gesetzt.");

      } else {
        p.sendMessage(Messages.PREFIX + " §7Du kannst deinen Home-Punkt nur in der Hauptwelt setzen.");
      }
    }
    return false;
  }
}
