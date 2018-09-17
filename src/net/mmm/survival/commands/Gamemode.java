package net.mmm.survival.commands;

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
      final Player p = (Player) sender;
      final Group group = BungeeGroupManager.getGroupManager().getGroup(p);

      if (CommandUtils.isOperator(p, group) && checkArguments(args, p)) {
        if (args.length == 1) {
          evaluateOneArgument(args, p);
        } else if (args.length == 2) {
          evaluateTwoArguments(args, p);
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
   * @param p Spieler
   */
  private void evaluateOneArgument(final String[] args, final Player p) {
    final String mode = args[0];
    if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
      p.sendMessage(Messages.GAMEMODE_SURVIVAL);
      p.setGameMode(GameMode.SURVIVAL);
    } else if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("creativ")) {
      p.sendMessage(Messages.GAMEMODE_CREATIVE);
      p.setGameMode(GameMode.CREATIVE);
    } else if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("adventure")) {
      p.sendMessage(Messages.GAMEMODE_ADVENTURE);
      p.setGameMode(GameMode.ADVENTURE);
    } else if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spec") || mode.equalsIgnoreCase("spectator")) {
      p.sendMessage(Messages.GAMEMODE_SPECTATOR);
      p.setGameMode(GameMode.SPECTATOR);
    } else {
      //TODO (Abgie) 17.09.2018: Fehlermeldung
    }

  }

  /**
   * setzt den GameMode anderer Spieler
   *
   * @param args Argumente des Commands
   * @param p Spieler
   */
  private void evaluateTwoArguments(final String[] args, final Player p) {
    if (Bukkit.getPlayer(args[1]) != null) {
      final Player player = Bukkit.getPlayer(args[1]);
      final String mode = args[0];
      if (mode.equalsIgnoreCase("0") || mode.equalsIgnoreCase("s") || mode.equalsIgnoreCase("survival")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
        player.sendMessage(Messages.GAMEMODE_SURVIVAL);
        player.setGameMode(GameMode.SURVIVAL);
      } else if (mode.equalsIgnoreCase("1") || mode.equalsIgnoreCase("c") || mode.equalsIgnoreCase("creativ")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
        player.sendMessage(Messages.GAMEMODE_CREATIVE);
        player.setGameMode(GameMode.CREATIVE);
      } else if (mode.equalsIgnoreCase("2") || mode.equalsIgnoreCase("a") || mode.equalsIgnoreCase("adventure")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
        player.sendMessage(Messages.GAMEMODE_ADVENTURE);
        player.setGameMode(GameMode.ADVENTURE);
      } else if (mode.equalsIgnoreCase("3") || mode.equalsIgnoreCase("spec") || mode.equalsIgnoreCase("spectator")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
        player.sendMessage(Messages.GAMEMODE_SPECTATOR);
        player.setGameMode(GameMode.SPECTATOR);
      } else {
        //TODO (Abgie) 17.09.2018: Hier auch ;)
      }
    } else {
      p.sendMessage(Messages.PLAYER_NOT_FOUND);
    }

  }

}
