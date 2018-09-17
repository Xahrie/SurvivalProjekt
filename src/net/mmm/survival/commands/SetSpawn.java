package net.mmm.survival.commands;

import net.mmm.survival.util.Messages;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawn implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;

      if (player.isOp()) {
        final World world = player.getWorld();

        world.setSpawnLocation(player.getLocation());
        player.sendMessage(Messages.PREFIX + " §7Du hast den §eSpawn §7gesetzt.");
      } else {
        player.sendMessage(Messages.PREFIX + " §cDu hast nicht die benötigten Rechte dafür.");
      }
    }
    return false;
  }
}
