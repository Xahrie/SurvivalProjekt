package net.mmm.survival.commands;

import java.util.List;

import net.mmm.survival.player.SurvivalLicence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Suders
 * Date: 30.09.2018
 * Time: 17:14:05
 * Location: SurvivalProjekt
 */
public class Licence implements CommandExecutor {

  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      if (args.length == 2) {
        if (args[0].equalsIgnoreCase("buy")) {
          final SurvivalPlayer executor = SurvivalPlayer.findSurvivalPlayer((Player) commandSender);
          if (args[1].equalsIgnoreCase("nether")) {
            buyLicence(executor, SurvivalLicence.NETHERLIZENZ, Messages.LICENCE_BUYING_NETHER);
          } else if (args[1].equalsIgnoreCase("end")) {
            buyLicence(executor, SurvivalLicence.ENDLIZENZ, Messages.LICENCE_BUYING_END);
          } else {
            commandSender.sendMessage(Messages.LICENCE_SYNTAX_ERROR);
          }
        } else if (args[0].equalsIgnoreCase("help")) {
          commandSender.sendMessage(Messages.LICENCE_SYNTAX);
        } else {
          commandSender.sendMessage(Messages.LICENCE_SYNTAX_ERROR);
        }
      } else {
        commandSender.sendMessage(Messages.LICENCE_SYNTAX_ERROR);
      }
    }
    return false;
  }

  private void buyLicence(final SurvivalPlayer executor, final SurvivalLicence licence, final String message) {
    if (checkLicenceNotBoughtBefore(executor, licence)) {
      final double cost = licence.getPrice();
      if (CommandUtils.checkMoney(cost, executor)) {
        executor.addOrTakeMoney(-cost);
        final List<SurvivalLicence> executorLicences = executor.getLicences();
        executorLicences.add(licence);
        final Player executorPlayer = executor.getPlayer();
        executorPlayer.sendMessage(message);
      }
    }
  }

  private boolean checkLicenceNotBoughtBefore(final SurvivalPlayer target, final SurvivalLicence netherlizenz) {
    if (target.hasLicence(netherlizenz)) {
      target.getPlayer().sendMessage(Messages.ALREADY_BOUGHT_LICENCE);
      return false;
    }

    return true;
  }
}
