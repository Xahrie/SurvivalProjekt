package net.mmm.survival.commands;

// Package-Stil???
import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /gamemode Command
 */
public class Gamemode implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;
      final Group group = BungeeGroupManager.getGroupManager().getGroup(player);

      if (CommandUtils.isOperator(player, group) && checkArguments(args, player)) {
        if (args.length == 1) {
          evaluateOneArgument(args, player);
        } else if (args.length == 2) {
          evaluateTwoArguments(args, player);
        }
      }
    }

    return false;
  }

  /**
   * Erlaubtes Format verwendet: 1 oder 2 Argumente
   *
   * @param strings Argumente des Commands
   * @param player Spieler
   * @return boolean
   */
  private boolean checkArguments(final String[] strings, final Player player) {
    if (!(strings.length == 1 || strings.length == 2)) {
      player.sendMessage(Messages.USAGE_GAMEMODE_COMMAND);
      return false;
    }

    return true;
  }

  /**
   * setzt den GameMode
   *
   * @param args Argumente des Commands
   * @param player Spieler
   */
  private void evaluateOneArgument(final String[] args, final Player player) {
    final String mode = args[0];
    if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
      player.sendMessage(Messages.GAMEMODE_SURVIVAL);
      player.setGameMode(GameMode.SURVIVAL);
    } else if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("creativ")) {
      player.sendMessage(Messages.GAMEMODE_CREATIVE);
      player.setGameMode(GameMode.CREATIVE);
    } else if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("adventure")) {
      player.sendMessage(Messages.GAMEMODE_ADVENTURE);
      player.setGameMode(GameMode.ADVENTURE);
    } else if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spec") || mode.equalsIgnoreCase("spectator")) {
      player.sendMessage(Messages.GAMEMODE_SPECTATOR);
      player.setGameMode(GameMode.SPECTATOR);
    } else {
      player.sendMessage(Messages.GAMEMODE_UNGUELTIG);
    }

  }

  /**
   * setzt den GameMode anderer Spieler
   *
   * @param args Argumente des Commands
   * @param player Spieler
   */
  private void evaluateTwoArguments(final String[] args, final Player player) {
    if (Bukkit.getPlayer(args[1]) != null) {
      final Player target = Bukkit.getPlayer(args[1]);
      final String mode = args[0];
      if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
        player.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
        target.sendMessage(Messages.GAMEMODE_SURVIVAL);
        target.setGameMode(GameMode.SURVIVAL);
      } else if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("creativ")) {
        player.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
        target.sendMessage(Messages.GAMEMODE_CREATIVE);
        target.setGameMode(GameMode.CREATIVE);
      } else if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("adventure")) {
        player.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
        target.sendMessage(Messages.GAMEMODE_ADVENTURE);
        target.setGameMode(GameMode.ADVENTURE);
      } else if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spec") || mode.equalsIgnoreCase("spectator")) {
        player.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
        target.sendMessage(Messages.GAMEMODE_SPECTATOR);
        target.setGameMode(GameMode.SPECTATOR);
      } else {
        player.sendMessage(Messages.GAMEMODE_UNGUELTIG);
      }
    } else {
      player.sendMessage(Messages.PLAYER_NOT_FOUND);
    }

  }

}
