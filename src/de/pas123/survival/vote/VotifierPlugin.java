package de.pas123.survival.vote;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import de.pas123.survival.Survival;
import de.pas123.survival.util.ItemManager;


public class VotifierPlugin implements Listener {
	
	public static Map<String, List<Vote>> votes = new HashMap<String, List<Vote>>();
	
	@EventHandler
	public void onVote(VotifierEvent e) {
		System.out.println(e.getVote().getUsername());
		System.out.println(e.getVote().getServiceName());
		System.out.println(e.getVote().getTimeStamp());
		System.out.println(e.getVote().getAddress());
		if(Bukkit.getPlayer(e.getVote().getUsername()) != null) {
			vote(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId(), e.getVote().getServiceName());
			Bukkit.getPlayer(e.getVote().getUsername()).sendMessage(Survival.getInstance().prefix + " §7Danke das du für uns gevotet hast. §8[§e" + e.getVote().getServiceName() + "§8]");
			Survival.getInstance().votes.put(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId(), Survival.getInstance().votes.containsKey(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId()) ? Survival.getInstance().votes.get(Bukkit.getPlayer(e.getVote().getUsername()).getUniqueId())+1 : 1);
			Bukkit.getPlayer(e.getVote().getUsername()).getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze", Arrays.asList("§7§oDu kannst diese Münzen beim Markt eintauschen.")));
		} else {
			votes.put(e.getVote().getUsername().toLowerCase(), votes.containsKey(e.getVote().getUsername().toLowerCase()) ? votes.put(e.getVote().getUsername().toLowerCase(), add(votes.get(e.getVote().getUsername().toLowerCase()), e.getVote())) : add(Arrays.asList(), e.getVote()));
		}
	}
	public static void vote(UUID uuid, String website) {
		Survival.getInstance().async.addVote(uuid, website);
	}
	List<Vote> add(List<Vote> list, Vote add) {
		List<Vote> votes = list;
		votes.add(add);
		return votes;
	}
	
}
