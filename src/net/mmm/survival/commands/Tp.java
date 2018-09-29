package net.mmm.survival.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Description
 *
 * @author Abgie on 30.09.2018 00:03
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class Tp implements CommandExecutor {

  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] strings) {
    final String world = strings[0];
    ((Player) commandSender).teleport(Bukkit.getWorld(world).getSpawnLocation());
    return false;
  }
}
