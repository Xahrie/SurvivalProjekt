package net.mmm.survival.commands;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/* 
 * @author Suders
 * Date: 30.09.2018
 * Time: 17:14:05
 * Location: SurvivalProjekt 
*/

public class BuyLicence implements CommandExecutor {

  public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
    if (sender instanceof Player) {  //TODO (Abgie) 30.09.2018: Hierfuer haben wir CommandUtils.checkPlayer(CommandSender) : boolean
      final Player p = (Player) sender;
      if(args.length == 2) {
        if (args[0].equalsIgnoreCase("buy")) { //TODO (Abgie) 30.09.2018: Klasse muss den selben Namen wie der Command haben
          final SurvivalPlayer sp = SurvivalData.getInstance().getPlayers().get(p.getUniqueId());
          if(args[1].equalsIgnoreCase("nether")) {
            
          } else if(args[1].equalsIgnoreCase("end")) {
            
          } else {
            sendSyntax(sender, true);
          }
        } else if(args[0].equalsIgnoreCase("help")) {
          sendSyntax(sender, false);
        } else {
          sendSyntax(sender, true);
        }
      } else {
        sendSyntax(sender, true);
      }
    } else {
      sender.sendMessage(Messages.NOT_A_PLAYER);
    }
    return false;
  }
  
  /*
   * Sendet @param sender den Syntax
   * @param sender Sender des Befehls
   * @param error Ob der Syntax als Fehler ausgegeben werden soll
   */
  private void sendSyntax(final CommandSender sender, final boolean error) {
    sender.sendMessage(error ? Messages.LICENCE_SYNTAX_ERROR : Messages.LICENCE_SYNTAX);
  }
  
}
