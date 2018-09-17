package net.mmm.survival.commands;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
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
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      final Group group = BungeeGroupManager.getGroupManager().getGroup(p);
      if (p.isOp() || group.equals(Group.OWNER) || group.equals(Group.MANAGER) || group.equals(Group.ADMIN)) {
        if (args.length == 1) {
          spielmodusSetzen(args, p);
        } else if (args.length == 2) {
          spielmodusAndererSpielerSetzen(args, p);
        } else {
          p.sendMessage(Messages.PREFIX + " §c/gm <0|1|2|3>");
          p.sendMessage(Messages.PREFIX + " §c/gm <0|1|2|3> <Spieler>");
        }
      } else {
        p.sendMessage(Messages.PREFIX + " §cDu hast nicht die benötigten Rechte dafür.");
      }
    }
    return false;
  }

  private void spielmodusAndererSpielerSetzen(final String[] args, final Player p) {
    if (Bukkit.getPlayer(args[1]) != null) {
      final Player player = Bukkit.getPlayer(args[1]);
      final String sSpielmodus = args[0];
      if (sSpielmodus.equalsIgnoreCase("0") || sSpielmodus.equalsIgnoreCase("s") || sSpielmodus.equalsIgnoreCase("survival")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
        player.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
        player.setGameMode(GameMode.SURVIVAL);
      } else if (sSpielmodus.equalsIgnoreCase("1") || sSpielmodus.equalsIgnoreCase("c") || sSpielmodus.equalsIgnoreCase("creativ")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
        player.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
        player.setGameMode(GameMode.CREATIVE);
      } else if (sSpielmodus.equalsIgnoreCase("2") || sSpielmodus.equalsIgnoreCase("a") || sSpielmodus.equalsIgnoreCase("adventure")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
        player.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
        player.setGameMode(GameMode.ADVENTURE);
      } else if (sSpielmodus.equalsIgnoreCase("3") || sSpielmodus.equalsIgnoreCase("spec") || sSpielmodus.equalsIgnoreCase("spectator")) {
        p.sendMessage(Messages.PREFIX + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
        player.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
        player.setGameMode(GameMode.SPECTATOR);
      }
    } else {
      p.sendMessage(Messages.PREFIX + " §cSpieler wurde nicht gefunden.");
    }
  }

  private void spielmodusSetzen(final String[] args, final Player p) {
    final String sSpielmodus = args[0];
    if (sSpielmodus.equalsIgnoreCase("0") || sSpielmodus.equalsIgnoreCase("s") || sSpielmodus.equalsIgnoreCase("survival")) {
      p.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
      p.setGameMode(GameMode.SURVIVAL);
    } else if (sSpielmodus.equalsIgnoreCase("1") || sSpielmodus.equalsIgnoreCase("c") || sSpielmodus.equalsIgnoreCase("creativ")) {
      p.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
      p.setGameMode(GameMode.CREATIVE);
    } else if (sSpielmodus.equalsIgnoreCase("2") || sSpielmodus.equalsIgnoreCase("a") || sSpielmodus.equalsIgnoreCase("adventure")) {
      p.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
      p.setGameMode(GameMode.ADVENTURE);
    } else if (sSpielmodus.equalsIgnoreCase("3") || sSpielmodus.equalsIgnoreCase("spec") || sSpielmodus.equalsIgnoreCase("spectator")) {
      p.sendMessage(Messages.PREFIX + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
      p.setGameMode(GameMode.SPECTATOR);
    }
  }
}
