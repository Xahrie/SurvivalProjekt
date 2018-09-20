package net.mmm.survival.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * /vote Command
 */
public class Vote implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(sender)) {
      final Player player = (Player) sender;

      player.sendMessage(Messages.VOTE_PAGES);
      player.spigot().sendMessage(getTextComponent("§7» §eMinecraft-Server.eu", "https://minecraft-server.eu/"));
      player.spigot().sendMessage(getTextComponent("§7» §eMinecraft-Serverliste.net", "https://www.minecraft-serverlist.net/serverlist"));
    }

    return false;
  }

  /**
   * Messagestil
   *
   * @param message Mitteilung
   * @param url URL
   * @return Textkomponente
   */
  private TextComponent getTextComponent(final String message, final String url) {
    final TextComponent msg = new TextComponent(message);

    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));

    return msg;
  }
}
