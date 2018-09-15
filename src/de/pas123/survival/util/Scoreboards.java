package de.pas123.survival.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;

public class Scoreboards {
	
	private static Scoreboard sb;
	
	public static void setScoreboard(Player p) {
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		
		BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
			
		for(Group group : manager.tablist.keySet()) {
			sb.registerNewTeam("" + manager.tablist.get(group).toString()).setPrefix(BungeeGroupManager.getGroupManager().getKurzel(group));
		}
		
		p.setScoreboard(sb);
		setPrefix(p);
	}
	@SuppressWarnings("deprecation")
	public static void setPrefix(Player p) {
		BungeeGroupManager manager = BungeeGroupManager.getGroupManager();
		final Boolean nicked = false;
		for(Player all : Bukkit.getOnlinePlayers()) {
			try {all.getScoreboard().getTeam("" + manager.tablist.get((nicked ? Group.PREMIUM : manager.getGroup(p.getUniqueId())))).addPlayer(p);} catch (Exception ex) {}
			try {p.getScoreboard().getTeam("" + manager.tablist.get((nicked ? Group.PREMIUM : manager.getGroup(all.getUniqueId())))).addPlayer(all);} catch (Exception ex) {}
		}
		p.setDisplayName((nicked ? manager.getPrefix(Group.PREMIUM) : manager.getPrefix(p)) + p.getName());
		p.setPlayerListName((nicked ? manager.getPrefix(Group.PREMIUM) : manager.getPrefix(p)) + p.getName());
	}
	
}
