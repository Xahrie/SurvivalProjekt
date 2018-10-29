package net.mmm.survival.commands;

import net.mmm.survival.commands.base.TeleportBase;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Description
 *
 * @author Abgie on 13.10.2018 14:32
 * project SurvivalProjekt
 * @version 1.0
 * @since JDK 8
 */
public class TeleportWorld extends TeleportBase implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
      if (args.length == 0) {
        commandSender.sendMessage(Messages.USAGE_TELEPORTWORLD_COMMAND);
      }
      if (args.length > 0) {
        evaluateArguments(commandSender, args[0], survivalPlayer);
      }
    }
    return false;
  }

  private void evaluateArguments(CommandSender commandSender, String arg, SurvivalPlayer survivalPlayer) {

    if (checkWorldExists(commandSender, arg) && CommandUtils.checkTeleport(survivalPlayer)) {
      final SurvivalWorld survivalWorld = SurvivalWorld.valueOf(arg.toUpperCase());
      final World world = survivalWorld.get();
      if (!((Player) commandSender).getWorld().equals(world)) {
        teleport((Player) commandSender, world.getSpawnLocation());
      } else {
        commandSender.sendMessage(Messages.ALREADY_ON_THIS_WORLD);
      }
    }
  }

  private boolean checkWorldExists(final CommandSender sender, final String sWorld) {
    if (sWorld.equalsIgnoreCase("bauwelt") || sWorld.equalsIgnoreCase("farmwelt") ||
        sWorld.equalsIgnoreCase("end") || sWorld.equalsIgnoreCase("nether")) {
      return true;
    } else {
      sender.sendMessage(Messages.USAGE_TELEPORTWORLD_COMMAND);
      return false;
    }
  }
}
