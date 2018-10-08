package net.mmm.survival.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.mmm.survival.util.CommandUtils;
import net.mmm.survival.util.Konst;
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
  public boolean onCommand(final CommandSender commandSender, final Command command, final String s, final String[] args) {
    if (CommandUtils.checkPlayer(commandSender)) {
      sendMessagge((Player) commandSender);
    }
    return false;
  }

  private void sendMessagge(final Player executor) {
    executor.sendMessage(Messages.VOTE_PAGES);
    final Player.Spigot spigot = executor.spigot();
    spigot.sendMessage(getTextComponent("§7» §eMinecraft-Server.eu", Konst.MINECRAFT_SERVER_EU));
    spigot.sendMessage(getTextComponent("§7» §eMinecraft-Serverliste.net", Konst.MINECRAFT_SERVERLIST_NET));
  }

  private TextComponent getTextComponent(final String message, final String url) {
    final TextComponent componentMessage = new TextComponent(message);
    final ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.OPEN_URL, url);
    componentMessage.setClickEvent(clickEvent);

    return componentMessage;
  }
}
