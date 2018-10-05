package net.mmm.survival.commands;

import java.util.List;

import net.mmm.survival.SurvivalData;
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
//TODO (BlueIronGirl) 05.10.2018: hier koennte man n bisschen was in Methoden auslagern das waere deutlich schoener :)
public class Licence implements CommandExecutor {

  public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) return true;
    final Player p = (Player) sender;
    if (args.length == 2) {
      if (args[0].equalsIgnoreCase("buy")) {
        final SurvivalPlayer sp = SurvivalData.getInstance().getPlayers().get(p.getUniqueId());
        if (args[1].equalsIgnoreCase("nether")) {
          if (sp.hasLicence(SurvivalLicence.NETHERLIZENZ)) {
            sender.sendMessage(Messages.ALREADY_BOUGHT_LICENCE);
            return true;
          }
          final Double cost = SurvivalLicence.NETHERLIZENZ.getPrice();
          Double currentMoney = sp.getMoney();
          if (currentMoney >= cost) {
            currentMoney -= cost;
          } else {
            sender.sendMessage(Messages.NOT_ENOUGH_MONEY);
            return true;
          }
          final List<SurvivalLicence> licences = sp.getLicences();
          licences.add(SurvivalLicence.NETHERLIZENZ);
          sp.setLicence(licences);
          sp.setMoney(currentMoney);
          sender.sendMessage(Messages.LICENCE_BUYING_NETHER);
        } else if (args[1].equalsIgnoreCase("end")) {
          if (sp.hasLicence(SurvivalLicence.ENDLIZENZ)) {
            sender.sendMessage(Messages.ALREADY_BOUGHT_LICENCE);
            return true;
          }
          final Double cost = SurvivalLicence.ENDLIZENZ.getPrice();
          Double currentMoney = sp.getMoney();
          if (currentMoney >= cost) {
            currentMoney -= cost;
          } else {
            sender.sendMessage(Messages.NOT_ENOUGH_MONEY);
            return true;
          }
          final List<SurvivalLicence> licences = sp.getLicences();
          licences.add(SurvivalLicence.ENDLIZENZ);
          sp.setLicence(licences);
          sp.setMoney(currentMoney);
          sender.sendMessage(Messages.LICENCE_BUYING_END);
        } else {
          sendSyntax(sender, true);
        }
      } else if (args[0].equalsIgnoreCase("help")) {
        sendSyntax(sender, false);
      } else {
        sendSyntax(sender, true);
      }
    } else {
      sendSyntax(sender, true);
    }
    return false;
  }

  /**
   * Sendet @param sender den Syntax
   *
   * @param sender Sender des Befehls
   * @param error Ob der Syntax als Fehler ausgegeben werden soll
   */
  private void sendSyntax(final CommandSender sender, final boolean error) {
    sender.sendMessage(error ? Messages.LICENCE_SYNTAX_ERROR : Messages.LICENCE_SYNTAX);
  }

}
