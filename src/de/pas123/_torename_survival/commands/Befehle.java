package de.pas123.survival.commands;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import de.PAS123.Group.Group.Group;
import de.PAS123.Group.Main.Spigot.BungeeGroupManager;
import de.pas123.survival.Survival;
import de.pas123.survival.util.Events;
import de.pas123.survival.util.Regions;
import de.pas123.survival.util.UUIDFetcher;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Befehle implements CommandExecutor {
	
	public static List<Player> move = new ArrayList<Player>();
	
	
	public void teleport(Player p, Location loc) {
		new BukkitRunnable() {
			
			int i = 3;
			
			@Override
			public void run() {
				if(move.contains(p)) {
					if(i == 3) {
						p.sendMessage(Survival.getInstance().prefix + " §7Du wirst teleportiert.. §e§o» Bewege dich nicht..");
					}
					if(i == 2) {
						p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
					}
					if(i == 1) {
						p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
					}
					if(i == 0) {
						p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 4.0F, 5.0F);
						if(p != null) {
							p.teleport(loc);
							move.remove(p);
							cancel();
						}
					}
					i -= 1;
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cTeleportation wurde abgebrochen.. §7§o» Du hast dich bewegt.");
					move.remove(p);
					cancel();
				}
			}
		}.runTaskTimer(Survival.getInstance(), 0L, 20L);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("navi")) {
				if(args.length == 1) {
					if(Bukkit.getPlayer(args[0]) != null) {
						UUID uuid = Bukkit.getPlayer(args[0]).getUniqueId();
						String name = Bukkit.getPlayer(args[0]).getName();
						if(Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false) != null) {
							ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
							p.sendMessage(Survival.getInstance().prefix + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
							p.setCompassTarget(new Location(Bukkit.getWorld("world"), region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()));
						}
					} else {
						try {
							UUIDFetcher.getUUID(args[0], new Consumer<UUID>() {
								
								@Override
								public void accept(UUID uuid) {
									UUIDFetcher.getName(uuid, new Consumer<String>() {

										@Override
										public void accept(String name) {
											if(Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false) != null) {
												ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
												p.sendMessage(Survival.getInstance().prefix + " §7Dein Kompassziel wurde auf die Zone von §e" + name + " gesetzt.");
												p.setCompassTarget(new Location(Bukkit.getWorld("world"), region.getMinimumPoint().getBlockX(), region.getMinimumPoint().getBlockY(), region.getMinimumPoint().getBlockZ()));
											}
										}
										
									});
								}
								
							});
						} catch (Exception ex) {
							p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
						}
					}
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cBenutze /navi <Spieler>");
				}
			} else if(cmd.getName().equalsIgnoreCase("gm")) {
				Group group = BungeeGroupManager.getGroupManager().getGroup(p);
				if(p.isOp() || group.equals(Group.OWNER) || group.equals(Group.MANAGER) || group.equals(Group.ADMIN)) {
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
							p.setGameMode(GameMode.SURVIVAL);
						} else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creativ")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
							p.setGameMode(GameMode.CREATIVE);
						} else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
							p.setGameMode(GameMode.ADVENTURE);
						} else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
							p.setGameMode(GameMode.SPECTATOR);
						}
					} else if(args.length == 2) {
						if(Bukkit.getPlayer(args[1]) != null) {
							Player player = Bukkit.getPlayer(args[1]);
							if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
								player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
								player.setGameMode(GameMode.SURVIVAL);
							} else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creativ")) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
								player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
								player.setGameMode(GameMode.CREATIVE);
							} else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
								player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
								player.setGameMode(GameMode.ADVENTURE);
							} else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator")) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
								player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
								player.setGameMode(GameMode.SPECTATOR);
							}
						} else {
							p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
						}
					} else {
						p.sendMessage(Survival.getInstance().prefix + " §c/gm <0|1|2|3>");
						p.sendMessage(Survival.getInstance().prefix + " §c/gm <0|1|2|3> <Spieler>");
					}
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cDu hast nicht die benötigten Rechte dafür.");
				}
			} else if(cmd.getName().equalsIgnoreCase("gamemode")) {
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
						p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
						p.setGameMode(GameMode.SURVIVAL);
					} else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creativ")) {
						p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
						p.setGameMode(GameMode.CREATIVE);
					} else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
						p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
						p.setGameMode(GameMode.ADVENTURE);
					} else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator")) {
						p.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
						p.setGameMode(GameMode.SPECTATOR);
					}
				} else if(args.length == 2) {
					if(Bukkit.getPlayer(args[1]) != null) {
						Player player = Bukkit.getPlayer(args[1]);
						if(args[0].equalsIgnoreCase("0") || args[0].equalsIgnoreCase("s") || args[0].equalsIgnoreCase("survival")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
							player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eÜberlebensmodus §7§o(Survival) §7gesetzt.");
							player.setGameMode(GameMode.SURVIVAL);
						} else if(args[0].equalsIgnoreCase("1") || args[0].equalsIgnoreCase("c") || args[0].equalsIgnoreCase("creativ")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
							player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eKreativmodus §7§o(Creative) §7gesetzt.");
							player.setGameMode(GameMode.CREATIVE);
						} else if(args[0].equalsIgnoreCase("2") || args[0].equalsIgnoreCase("a") || args[0].equalsIgnoreCase("adventure")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
							player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eAbenteuermodus §7§o(Adventure) §7gesetzt.");
							player.setGameMode(GameMode.ADVENTURE);
						} else if(args[0].equalsIgnoreCase("3") || args[0].equalsIgnoreCase("spec") || args[0].equalsIgnoreCase("spectator")) {
							p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getDisplayName() + " §7in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
							player.sendMessage(Survival.getInstance().prefix + " §7Du wurdest in den Spielmodus§8: §eZuschauermodus §7§o(Spectatormode) §7gesetzt.");
							player.setGameMode(GameMode.SPECTATOR);
						}
					} else {
						p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
					}
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §c/gamemode <0|1|2|3>");
					p.sendMessage(Survival.getInstance().prefix + " §c/gamemode <0|1|2|3> <Spieler>");
				}
			} else if(cmd.getName().equalsIgnoreCase("vote")) {
				p.sendMessage(Survival.getInstance().prefix + " §7Unsere Vote-Seiten.");
				p.spigot().sendMessage(TextComponent("§7» §eMinecraft-Server.eu", "https://minecraft-server.eu/"));
				p.spigot().sendMessage(TextComponent("§7» §eMinecraft-Serverliste.net", "https://www.minecraft-serverlist.net/serverlist"));
			} else if(cmd.getName().equalsIgnoreCase("sethome")) {
				if(p.getWorld().getName().equals("world")) {
					Survival.getInstance().data.set("homes." + p.getUniqueId() + ".x", p.getLocation().getX());
					Survival.getInstance().data.set("homes." + p.getUniqueId() + ".y", p.getLocation().getY());
					Survival.getInstance().data.set("homes." + p.getUniqueId() + ".z", p.getLocation().getZ());
					Survival.getInstance().save();
					p.sendMessage(Survival.getInstance().prefix + " §7Du hast deinen Home-Punkt gesetzt.");
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §7Du kannst deinen Home-Punkt nur in der Hauptwelt setzen.");
				}
			} else if(cmd.getName().equalsIgnoreCase("home")) {
				if(p.getWorld().getName().equals("world")) {
					if(Survival.getInstance().data.contains("homes." + p.getUniqueId())) {
						if(!move.contains(p)) {
							move.add(p);
							teleport(p, new Location(Bukkit.getWorld("world"), Survival.getInstance().data.getDouble("homes." + p.getUniqueId() + ".x"), Survival.getInstance().data.getDouble("homes." + p.getUniqueId() + ".y"), Survival.getInstance().data.getDouble("homes." + p.getUniqueId() + ".z")));
						} else {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wirst bereits teleportiert.");
						}
					} else {
						p.sendMessage(Survival.getInstance().prefix + " §7Du hast noch keinen Home-Punkt gesetzt.");
					}
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §7Du kannst deinen Home-Punkt nur in der Hauptwelt setzen.");
				}
			} else if(cmd.getName().equalsIgnoreCase("setspawn")) {
				if(p.isOp()) {
					Survival.getInstance().data.set("Spawn.x", p.getLocation().getX());
					Survival.getInstance().data.set("Spawn.y", p.getLocation().getY());
					Survival.getInstance().data.set("Spawn.z", p.getLocation().getZ());
					Survival.getInstance().data.set("Spawn.yaw", p.getLocation().getYaw());
					Survival.getInstance().data.set("Spawn.pitch", p.getLocation().getPitch());
					Survival.getInstance().save();
					p.sendMessage(Survival.getInstance().prefix + " §7Du hast den §eSpawn §7gesetzt.");
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cDu hast nicht die benötigten Rechte dafür.");
				}
			} else if(cmd.getName().equalsIgnoreCase("setnetherspawn")) {
				if(p.isOp()) {
					Survival.getInstance().data.set("Spawn.nether.x", p.getLocation().getX());
					Survival.getInstance().data.set("Spawn.nether.y", p.getLocation().getY());
					Survival.getInstance().data.set("Spawn.nether.z", p.getLocation().getZ());
					Survival.getInstance().data.set("Spawn.nether.yaw", p.getLocation().getYaw());
					Survival.getInstance().data.set("Spawn.nether.pitch", p.getLocation().getPitch());
					Survival.getInstance().save();
					p.sendMessage(Survival.getInstance().prefix + " §7Du hast den §eSpawn §7gesetzt.");
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cDu hast nicht die benötigten Rechte dafür.");
				}
			} else if(cmd.getName().equalsIgnoreCase("setfarmweltspawn")) {
				if(p.isOp()) {
					Survival.getInstance().data.set("Spawn.farmwelt.x", p.getLocation().getX());
					Survival.getInstance().data.set("Spawn.farmwelt.y", p.getLocation().getY());
					Survival.getInstance().data.set("Spawn.farmwelt.z", p.getLocation().getZ());
					Survival.getInstance().data.set("Spawn.farmwelt.yaw", p.getLocation().getYaw());
					Survival.getInstance().data.set("Spawn.farmwelt.pitch", p.getLocation().getPitch());
					Survival.getInstance().save();
					p.sendMessage(Survival.getInstance().prefix + " §7Du hast den §eSpawn §7gesetzt.");
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cDu hast nicht die benötigten Rechte dafür.");
				}
			} else if(cmd.getName().equalsIgnoreCase("spawn")) {
				if(p.getWorld().getName().equals("world")) {
					if(Survival.getInstance().spawn != null) {
						if(!move.contains(p)) {
							move.add(p);
							teleport(p, Survival.getInstance().spawn);
						} else {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wirst bereits teleportiert.");
						}
					} else {
						p.sendMessage(Survival.getInstance().prefix + " §cDer Spawn wurde nicht gesetzt.");
					}
				} else if(p.getWorld().getName().equals("world_nether")) {
					if(Survival.getInstance().netherspawn != null) {
						if(!move.contains(p)) {
							move.add(p);
							teleport(p, Survival.getInstance().netherspawn);
						} else {
							p.sendMessage(Survival.getInstance().prefix + " §7Du wirst bereits teleportiert.");
						}
					} else {
						p.sendMessage(Survival.getInstance().prefix + " §cDer Nether-Spawn wurde nicht gesetzt.");
					}
				}
			} else if(cmd.getName().equalsIgnoreCase("zone")) {
				if(p.getWorld().getName().equals("world")) {
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("create")) {
							if(Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, p.getUniqueId().toString(), false) != null) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast bereits eine Zone.");
							} else {
								if(Events.zonenedit.contains(p)) {
									Events.zonenedit.remove(p);
								} else {
									Events.zonenedit.add(p);
									p.sendMessage(Survival.getInstance().prefix + " §7Klicke mit einem Stock auf die erste Ecke deiner Zone, danach klicke auf die gegenüber liegende Ecke.");
								}
							}
						} else if(args[0].equalsIgnoreCase("search")) {
							if(Events.zonensearch.contains(p)) {
								Events.zonensearch.remove(p);
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast den Zonen-Suchmodus §cverlassen§7.");
							} else {
								p.sendMessage(Survival.getInstance().prefix + " §7Zonen-Suchmodus §abetreten§7, klicke mit einem Stock auf den Boden um nach Zonen zu suchen.");
								Events.zonensearch.add(p);
							}
						} else if(args[0].equalsIgnoreCase("delete")) {
							ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, p.getUniqueId().toString(), false);
							if(region != null) {
								Survival.getInstance().dynmap.rg.removeRegion(region.getId());
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast deine Zone gelöscht.");
							} else {
								p.sendMessage(Survival.getInstance().prefix + " §cDu hast keine Zone.");
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							ProtectedRegion region = Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg, p.getLocation());
							if(region != null) {
								UUIDFetcher.getName(UUID.fromString(region.getId()), new Consumer<String>() {

									@Override
									public void accept(String name) {
										p.sendMessage(" ");
										p.sendMessage(Survival.getInstance().prefix + " §7Besitzer§8: " + name);
										
										String member = null;
							        	for(UUID uuid : region.getOwners().getUniqueIds()) {
							        		if(member == null) {
							        			member = Survival.getInstance().async.getMySQL().getName(uuid);
							        		} else {
							        			member += ", " + Survival.getInstance().async.getMySQL().getName(uuid);
							        		}
							        	}
							        	p.sendMessage(Survival.getInstance().prefix + " §7Mitglieder§8: " + member);
										
							        	
										p.sendMessage(" ");
									}
									
								});
								
							} else {
								p.sendMessage(Survival.getInstance().prefix + " §cDu stehst in keiner Zone.");
							}
						} else {
							sendHelp(p);
						}
					} else if(args.length == 2) {
						if(args[0].equalsIgnoreCase("add")) {
							try {
								if(Bukkit.getPlayer(args[1]) != null) {
									final Player player = Bukkit.getPlayer(args[1]);
									final UUID uuid = player.getUniqueId();
									ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
									if(!region.getMembers().contains(uuid)) {
										region.getMembers().addPlayer(uuid);
										p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getName() + " §7zu deiner Zone hinzugefügt.");
									} else {
										p.sendMessage(Survival.getInstance().prefix + " §e" + player.getName() + " §7ist bereits Mitglied deiner Zone.");
									}
								} else {
									try {
										UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

											@Override
											public void accept(UUID uuid) {
												UUIDFetcher.getName(uuid, new Consumer<String>() {

													@Override
													public void accept(String name) {
														ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
														if(!region.getMembers().contains(uuid)) {
															region.getMembers().addPlayer(Bukkit.getPlayer(args[1]).getUniqueId());
															p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + name + " §7zu deiner Zone hinzugefügt.");
														} else {
															p.sendMessage(Survival.getInstance().prefix + " §e" + name + " §7ist bereits Mitglied deiner Zone.");
														}
													}
												
												});
											}
										});
									} catch (Exception ex) {
										p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
									}
								}
							} catch (Exception ex) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast keine Zone.");
							}
						} else if(args[0].equalsIgnoreCase("remove")) {
							try {
								if(Bukkit.getPlayer(args[1]) != null) {
									final Player player = Bukkit.getPlayer(args[1]);
									final UUID uuid = player.getUniqueId();
									final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
									if(region.getMembers().contains(uuid)) {
										region.getMembers().removePlayer(uuid);
										p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + player.getName() + " von deiner Zone entfernt.");
									} else {
										p.sendMessage(Survival.getInstance().prefix + " §e" + player.getName() + " §7ist kein Mitglied deiner Zone.");
									}
								} else {
									try {
										UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {
											
											@Override
											public void accept(UUID uuid) {
												UUIDFetcher.getName(uuid, new Consumer<String>() {

													@Override
													public void accept(String name) {
														final ProtectedRegion region = Regions.checkExistingRegion(Survival.getInstance().dynmap.rg, uuid.toString(), false);
														if(region.getMembers().contains(uuid)) {
															region.getMembers().removePlayer(uuid);
															p.sendMessage(Survival.getInstance().prefix + " §7Du hast §e" + name + " von deiner Zone entfernt.");
														} else {
															p.sendMessage(Survival.getInstance().prefix + " §e" + name + " §7ist kein Mitglied deiner Zone.");
														}
													}
												
												});
											}
										
										});
									} catch (Exception ex) {
										p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
									}
								}
							} catch (Exception ex) {
								p.sendMessage(Survival.getInstance().prefix + " §7Du hast keine Zone.");
							}
						} else if(args[0].equalsIgnoreCase("info")) {
							if(p.isOp()) {
								if(Bukkit.getPlayer(args[1]) != null) {
									Long time = Bukkit.getPlayer(args[1]).getLastPlayed();
									Long first = Bukkit.getPlayer(args[1]).getFirstPlayed();
									String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
									String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));
									
									p.sendMessage(" ");
									p.sendMessage(Survival.getInstance().prefix + " §7Zuletzt online§8: §c" + lastonline);
									p.sendMessage(Survival.getInstance().prefix + " §7Erstes mal§8: §c" + firstonline);
									p.sendMessage(" ");
								} else {
									try {
										UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

											@Override
											public void accept(UUID uuid) {
												if(Bukkit.getOfflinePlayer(uuid) != null) {
													Long time = Bukkit.getOfflinePlayer(uuid).getLastPlayed();
													Long first = Bukkit.getOfflinePlayer(uuid).getFirstPlayed();
													String lastonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(time));
													String firstonline = new SimpleDateFormat("dd.MM.yyyy").format(new Date(first));
													
													p.sendMessage(" ");
													p.sendMessage(Survival.getInstance().prefix + " §7Zuletzt online§8: §c" + lastonline);
													p.sendMessage(Survival.getInstance().prefix + " §7Erstes mal§8: §c" + firstonline);
													p.sendMessage(" ");
												}
											}
											
										});
									} catch (Exception ex) {
										p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
									}
								}
							}
						} else {
							sendHelp(p);
						}
					} else if(args.length == 3) {
						if(args[0].equalsIgnoreCase("setlength")) {
							if(p.isOp()) {
								try {
									Integer max = Integer.valueOf(args[2]);
									if(Bukkit.getPlayer(args[1]) != null) {
										Events.maxzone.put(Bukkit.getPlayer(args[1]).getUniqueId(), max);
									} else {
										try {
											UUIDFetcher.getUUID(args[1], new Consumer<UUID>() {

												@Override
												public void accept(UUID uuid) {
													Events.maxzone.put(uuid, max);
													UUIDFetcher.getName(uuid, new Consumer<String>() {
															
														@Override
														public void accept(String name) {
															p.sendMessage(Survival.getInstance().prefix + " §e" + name + " §7kann nun eine Zone mit der Länge §c" + max + " §7erstellen.");
														}
														
													});
												}
												
											});
										} catch (Exception ex) {
											p.sendMessage(Survival.getInstance().prefix + " §cSpieler wurde nicht gefunden.");
										}
									}
								} catch (Exception ex) {
									p.sendMessage(Survival.getInstance().prefix + " §cDu musst eine Zahl angeben.");
								}
							} else {
								p.sendMessage(Survival.getInstance().prefix + " §cDu hast nicht die benötigten Rechte dafür.");
							}
						} else {
							sendHelp(p);
						}
					} else {
						sendHelp(p);
					}
				} else {
					p.sendMessage(Survival.getInstance().prefix + " §cZonen Befehle kannst du nur in der Hauptwelt nutzen.");
				}
			} else if(cmd.getName().equalsIgnoreCase("tame")) {
				if(Events.tamed.contains(p)) {
					Events.tamed.remove(p);
					p.sendMessage(Survival.getInstance().prefix + " §7Du kannst nun wieder normal mit den Tieren interagieren.");
				} else {
					Events.tamed.add(p);
					p.sendMessage(Survival.getInstance().prefix + " §7Klicke auf das Tier, dass du freilassen möchtest.");
					p.sendMessage(Survival.getInstance().prefix + " §7Zum Abbrechen gebe erneut §e/tame §7ein.");
				}
			}
		}
		return false;
	}
	public void sendHelp(Player p) {
		p.sendMessage(Survival.getInstance().prefix + " §7Zonenhilfe§8:");
		p.sendMessage("§e/zone create §8┃ §7Erstellt eine Zone");
		p.sendMessage("§e/zone search §8┃ §7Sucht nach Zonen");
		p.sendMessage("§e/zone add <Spieler> §8┃ §7Fügt einen Spieler auf deine Zone hinzu");
		p.sendMessage("§e/zone remove <Spieler> §8┃ §7Entfernt einen Spieler von deiner Zone");
		p.sendMessage("§e/zone delete §8┃ §7Löscht deine Zone");
		if(p.isOp()) p.sendMessage("§c/zone setlength <Spieler> <Anzahl> §8┃ §7Setzt die Max-Länge der Zone des Spielers");
		if(p.isOp()) p.sendMessage("§c/zone info <Spieler> §8┃ §7Zeigt Informationen über den Spieler an");
		if(p.isOp()) p.sendMessage("§c/zone info §8┃ §7Zeigt Informationen über die Zone in der du stehst an");
	}
	public TextComponent TextComponent(String message, String url) {
		TextComponent msg = new TextComponent(message);
		msg.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url));
		return msg;
	}
	
	
}
