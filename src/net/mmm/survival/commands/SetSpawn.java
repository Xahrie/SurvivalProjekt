package net.mmm.survival.commands;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /setspawn Command
 */
public class SetSpawn implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command cmd, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player player = (Player) sender;
      final Group group = BungeeGroupManager.getGroupManager().getGroup(player);

      if (CommandUtils.isOperator(player, group)) {
        final World world = player.getWorld();

        world.setSpawnLocation(player.getLocation());
        player.sendMessage(Messages.SPAWN_SET);
      }
    }

    return false;
  }

}
