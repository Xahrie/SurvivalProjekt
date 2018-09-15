package de.pas123.survival.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.material.Button;

import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.vexsoftware.votifier.model.Vote;

import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import de.pas123.survival.Survival;
import de.pas123.survival.commands.Befehle;
import de.pas123.survival.vote.VotifierPlugin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Events implements Listener {
	
	public static Map<Player, BlockVector> loc1 = new HashMap<Player, BlockVector>();
	public static Map<Player, BlockVector> loc2 = new HashMap<Player, BlockVector>();
	public static Map<Player, List<Block>> show = new HashMap<Player, List<Block>>();
	public static Map<UUID, Integer> maxzone = new HashMap<UUID, Integer>();
	public static List<Player> zonenedit = new ArrayList<Player>();
	public static List<Player> zonensearch = new ArrayList<Player>();
	public static List<Player> tamed = new ArrayList<Player>();
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(e.getItem() != null) {
				if(zonenedit.contains(p)) {
					if(e.getItem().getType().equals(Material.STICK)) {
						Location loc = e.getClickedBlock().getLocation();
						loc.setY(loc1.containsKey(p) ? 256 : 0);
						if(loc1.containsKey(p)) loc2.put(p, new BlockVector(loc.getX(), loc.getY(), loc.getZ())); else loc1.put(p, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
						p.sendMessage(Survival.getInstance().prefix + " §7Du hast Position §e" + (loc1.containsKey(p) && loc2.containsKey(p) ? "2." : "1.") + " §7gesetzt.");
						Bukkit.getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), new Runnable() {
							
							@Override
							public void run() {
								
								if(loc1.containsKey(p) && !loc2.containsKey(p)) {
									List<Block> blocks = new ArrayList<Block>();
									p.sendBlockChange(e.getClickedBlock().getLocation(), Material.LIME_STAINED_GLASS, (byte) 0);
									Location beacon = e.getClickedBlock().getLocation().subtract(0, 1, 0);
									Location ironblock = e.getClickedBlock().getLocation().subtract(0, 2, 0);
									
									p.sendBlockChange(beacon, Material.BEACON, (byte) 0);
									p.sendBlockChange(ironblock, Material.IRON_BLOCK, (byte) 0);
									
									blocks.add(beacon.getBlock());
									blocks.add(ironblock.getBlock());
									
									for(int x = -1; x <= 1; x++) {
										for(int z = -1; z <= 1; z++) {
											Location block = ironblock.clone().add(x, 0, z);
											p.sendBlockChange(block, Material.IRON_BLOCK, (byte) 0);
											blocks.add(block.getBlock());
										}
									}
									if(show.containsKey(p)) blocks.addAll(show.get(p));
									show.put(p, blocks);
								}
								
							}
							
						}, 10L);
					    
						if(loc1.containsKey(p) && loc2.containsKey(p)) {
							createRegion(p);
						}
					}
				} else if(zonensearch.contains(p)) {
					if(e.getItem().getType().equals(Material.STICK)) {
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								if(Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg, e.getClickedBlock().getLocation()) != null) {
									try {
										String name = Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg, e.getClickedBlock().getLocation()).getId();
										try {
											UUID uuid = UUID.fromString(name);
											try {
												UUIDFetcher.getName(uuid, new Consumer<String>() {
													
													@Override
													public void accept(String name) {
														p.sendMessage(Survival.getInstance().prefix + " §7Es wurde die Zone von §e" + name + " §7gefunden.");
													}
													
												});
											} catch (Exception ex) {}
										} catch (Exception ex) {
											p.sendMessage(Survival.getInstance().prefix + " §7Es wurde die Zone §e" + name + " §7gefunden.");
										}
										if(name.toLowerCase().contains("spawnzone") || name.toLowerCase().equals("spawnzone")) {
											p.sendMessage(Survival.getInstance().prefix + " §7Es wurde die Zone §eSpawnzone §7gefunden.");
										}
									} catch (Exception ex) {}
								} else {
									p.sendMessage(Survival.getInstance().prefix + " §7Es wurde keine Zone bei §8(§e" + e.getClickedBlock().getLocation().getBlockX() + "§7/" + "§e" + e.getClickedBlock().getLocation().getBlockZ() + "§8) §7gefunden.");
								}
								Thread.currentThread().interrupt();
							}
						}).start();
					}
				}
			}
		}
	}
	public static void sendBar(Player p, String msg) {
		Hotbar.send(p, msg);
	}
	public static void createRegion(Player p) {
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				RegionManager manager = Survival.getInstance().dynmap.rg;
				 
				ProtectedCuboidRegion pr = new ProtectedCuboidRegion(p.getUniqueId().toString(), (BlockVector)loc1.get(p), (BlockVector)loc2.get(p));
				 
				int x1 = pr.getMinimumPoint().getBlockX();
				int y1 = pr.getMinimumPoint().getBlockY();
				int z1 = pr.getMinimumPoint().getBlockZ();
				
				int x2 = pr.getMaximumPoint().getBlockX();
				int y2 = pr.getMaximumPoint().getBlockY();
				int z2 = pr.getMaximumPoint().getBlockZ();
				
				int a = x1-x2;
				a = a < 0 ? a * -1 : a * 1;
				
				int b = z1-z2;
				b = b < 0 ? b * -1 : b * 1;
				
				int max = maxzone.containsKey(p.getUniqueId()) ? maxzone.get(p.getUniqueId()) : 100;
				
				if(a >= 20 && a <= max && b >= 20 && b <= max) {
					
					 CuboidIterator blocks = new CuboidIterator(Bukkit.getWorld("world"), x1, y1, z1, x2, y2, z2);
					 boolean found = false;
					 for(Iterator<?> iterator = blocks; iterator.hasNext();) {
						 Block block = (Block) iterator.next();
						 if(Regions.checkRegionLocationIn(manager, block.getLocation()) != null) {
							 found = true;
							 System.out.println(Regions.checkRegionLocationIn(manager, block.getLocation()).getId());
							 
							 p.sendMessage(Survival.getInstance().prefix + " §cDu kannst keine Zone in einer bereits bestehenden Zone erstellen.");
							 break;
						 }
					 }
					 
					 loc1.remove(p);
					 loc2.remove(p);
					 zonenedit.remove(p);
					 for(Block block : show.get(p)) {
						 p.sendBlockChange(block.getLocation(), block.getBlockData());
					 }
					 if(!found) {
						 maxzone.remove(p.getUniqueId());
						 DefaultDomain dd = new DefaultDomain();
						 dd.addPlayer(p.getUniqueId());
						 pr.setOwners(dd);
						 pr.setPriority(100);
						    
						 Map<Flag<?>, Object> flags = new HashMap<Flag<?>, Object>();
						    
						 flags.put(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
						 flags.put(Flags.POTION_SPLASH, StateFlag.State.DENY);
						 flags.put(Flags.FIRE_SPREAD, StateFlag.State.DENY);
						 flags.put(Flags.LAVA_FIRE, StateFlag.State.DENY);
						 
						 pr.setFlags(flags);
						 manager.addRegion(pr);
						 try {
							 manager.save();
						 } catch (Exception localException) {}
						 sendBar(p, "§7Du hast erfolgreich deine Zone erstellt.");
					 }
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cDeine Zone darf minimal 20x20 und maximal 100x100 Blöcke groß sein. Deine Zone ist " + a + "x" + b + " Blöcke groß.");
				}
				Thread.currentThread().interrupt();
			}
		}).start();
	}
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onInteractButton(PlayerInteractEvent e) {
		if(e.getClickedBlock().getState() instanceof Button) {
			Button button = (Button) e.getClickedBlock().getState();
			Block b = e.getClickedBlock().getRelative(button.getAttachedFace(), 2);
			if(b.getState() instanceof CommandBlock) {
				CommandBlock commandblock = (CommandBlock) b.getState();
				e.setCancelled(true);
				String[] args = commandblock.getCommand().split(" ");
				switch (args[0]) {
					case "FARMWELT":
						e.getPlayer().teleport(Survival.getInstance().farmweltspawn);
						e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3F, 0.3F);
						e.getPlayer().sendMessage(Survival.getInstance().prefix + " §7Du wurdest zum §eFarmwelt-Spawn §7teleportiert.");
						break;
					case "FACEBOOK": 
						TextComponent facebook = new TextComponent(Survival.getInstance().prefix + " §7Klicke §ehier §7zu unserem Facebook Profil.");
						facebook.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
						e.getPlayer().spigot().sendMessage(facebook);
						break;
					case "WEBSITE":
						TextComponent webseite = new TextComponent(Survival.getInstance().prefix + " §7Klicke §ehier §7zu unserer Webseite.");
						webseite.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.MineMagicMania.de/"));
						e.getPlayer().spigot().sendMessage(webseite);
						break;
					case "YOUTUBE": 
						TextComponent youtube = new TextComponent(Survival.getInstance().prefix + " §7Klicke §ehier §7zu unserem Youtube Kanal.");
						youtube.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
						e.getPlayer().spigot().sendMessage(youtube);
						break;
					case "TWITTER": 
						TextComponent twitter = new TextComponent(Survival.getInstance().prefix + " §7Klicke §ehier §7zu unserem Twitter Profil.");
						twitter.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
						e.getPlayer().spigot().sendMessage(twitter);
						break;
					case "PROJEKTTELEPORTER":
						
						break;
					default:
						break;
				}
			}
		}
	}
	@EventHandler
	public void onInteractEntity(PlayerInteractEntityEvent e) {
		if(tamed.contains(e.getPlayer())) {
			try {
				Tameable en = (Tameable) e.getRightClicked();
				if(en.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
					en.setTamed(false);
					e.getPlayer().sendMessage(Survival.getInstance().prefix + " §7Du hast das Tier freigelassen.");
					tamed.remove(e.getPlayer());
				} else {
					e.getPlayer().sendMessage(Survival.getInstance().prefix + " §7Du hast dieses Tier nicht gezähmt.");
				}
			} catch (Exception ex) {}
		}
	}
	@EventHandler
	public void onTravel(PlayerPortalEvent e) {
		if(e.getCause().equals(TeleportCause.NETHER_PORTAL)) {
			e.setCancelled(true);
			e.useTravelAgent(false);
			if(e.getFrom().getWorld().getName().equals("world")) {
				e.getPlayer().teleport(Survival.getInstance().netherspawn != null ? Survival.getInstance().netherspawn : Bukkit.getWorld("world_nether").getSpawnLocation(), TeleportCause.NETHER_PORTAL);
			} else {
				e.getPlayer().teleport(Survival.getInstance().spawn, TeleportCause.NETHER_PORTAL);
			}
		}
	}
	/*@EventHandler
	public void onPortal(PortalCreateEvent e) {
		if(Regions.checkRegionLocationIn(WorldGuardPlugin.inst().getRegionManager(e.getWorld()), e.getBlocks().get(0).getLocation()) != null) {
			ProtectedRegion region = Regions.checkRegionLocationIn(WorldGuardPlugin.inst().getRegionManager(e.getWorld()), e.getBlocks().get(0).getLocation());
			try {
				UUID.fromString(region.getId());
				e.setCancelled(true);
			} catch (Exception ex) {
				e.setCancelled(false);
			}
		} else {
			e.setCancelled(true);
		}
	}
	*/
	@EventHandler
	public void onTarget(EntityTargetEvent e) {
		if(e.getEntity().getWorld().getName().equals("world")) {
			if(e.getTarget() instanceof Player) {
				if(e.getEntity() instanceof IronGolem || e.getEntity() instanceof Wolf) {
					if(e.getTarget() instanceof Player) {
						e.setCancelled(true);
					}
				}
			}
		}
	}
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if(e.getEntity() instanceof Wither) {
			if(e.getSpawnReason().equals(SpawnReason.BUILD_WITHER)) {
				if(!e.getLocation().getWorld().getName().equals("world_nether")) {
					e.setCancelled(true);
				}
			}
		} else if(e.getEntity() instanceof Monster) {
			if(!e.getSpawnReason().equals(SpawnReason.SPAWNER)) {
				e.setCancelled(true);
			}
		} else {
			e.setCancelled(false);
		}
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent e) {
		e.setDeathMessage(null);
	}
	@EventHandler
	public void onRespawn(PlayerRespawnEvent e) {
		e.setRespawnLocation(Survival.getInstance().spawn != null ? (e.getPlayer().getBedSpawnLocation() != null ? e.getPlayer().getBedSpawnLocation() : Survival.getInstance().spawn) : e.getPlayer().getWorld().getSpawnLocation());
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		Scoreboards.setScoreboard(e.getPlayer());
		if(VotifierPlugin.votes.containsKey(e.getPlayer().getName().toLowerCase())) {
			for(Vote vote : VotifierPlugin.votes.get(e.getPlayer().getName().toLowerCase())) {
				e.getPlayer().sendMessage(Survival.getInstance().prefix + " §7Danke das du für uns gevotest hast. §8[§e" + vote.getServiceName() + "§8]");
				Survival.getInstance().votes.put(e.getPlayer().getUniqueId(), Survival.getInstance().votes.containsKey(e.getPlayer().getUniqueId()) ? Survival.getInstance().votes.get(e.getPlayer().getUniqueId())+1 : 1);
				VotifierPlugin.vote(e.getPlayer().getUniqueId(), vote.getServiceName());
				e.getPlayer().getInventory().addItem(ItemManager.build(Material.IRON_NUGGET, "§cMünze", Arrays.asList("§7§oDu kannst diese Münzen beim Markt eintauschen.")));
			}
			VotifierPlugin.votes.remove(e.getPlayer().getName().toLowerCase());
		}
		Survival.getInstance().coins.put(e.getPlayer().getUniqueId(), Survival.getInstance().async.getMySQL().getCoins(e.getPlayer().getUniqueId()));
	}
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		e.setQuitMessage(null);
		if(zonensearch.contains(e.getPlayer())) zonensearch.remove(e.getPlayer());
		if(Survival.getInstance().coins.containsKey(e.getPlayer().getUniqueId())) Survival.getInstance().coins.remove(e.getPlayer().getUniqueId());
	}
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(Befehle.move.contains(e.getPlayer())) {
			Befehle.move.remove(e.getPlayer());
//			e.getPlayer().sendMessage(Survival.getInstance().prefix + " §7Du hast dich bewegt, teleportertion abgebrochen.");
		}
	}
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		e.setCancelled(true);
		for(Player all : Bukkit.getOnlinePlayers()) {
			all.sendMessage(BungeeGroupManager.getGroupManager().getPrefix(p) + p.getName() + " §7» §7" + e.getMessage());
		}
	}
}
