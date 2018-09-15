package de.mmm.survival.commands;

import de.mmm.survival.util.Events;
import de.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Tame implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;

      if (Events.tamed.contains(p)) {
        Events.tamed.remove(p);
        p.sendMessage(Messages.PREFIX + " §7Du kannst nun wieder normal mit den Tieren interagieren.");
      } else {
        Events.tamed.add(p);
        p.sendMessage(Messages.PREFIX + " §7Klicke auf das Tier, dass du freilassen möchtest.");
        p.sendMessage(Messages.PREFIX + " §7Zum Abbrechen gebe erneut §e/tame §7ein.");
      }
    }
    return false;
  }
}
