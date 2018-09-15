package de.mmm.survival.commands;

import de.mmm.survival.util.Messages;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Vote implements CommandExecutor {
  @Override
  public boolean onCommand(final CommandSender sender, final Command command, final String s, final String[] args) {
    if (sender instanceof Player) {
      final Player p = (Player) sender;
      p.sendMessage(Messages.PREFIX + " §7Unsere Vote-Seiten.");
      p.spigot().sendMessage(getTextComponent("§7» §eMinecraft-Server.eu", "https://minecraft-server.eu/"));
      p.spigot().sendMessage(getTextComponent("§7» §eMinecraft-Serverliste.net", "https://www.minecraft-serverlist.net/serverlist"));
    }
    return false;
  }

  private TextComponent getTextComponent(final String message, final String url) {
    final TextComponent msg = new TextComponent(message);
    msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
    return msg;
  }
}
