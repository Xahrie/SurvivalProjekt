package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.Regions;
import net.mmm.survival.util.UUIDFetcher;
import net.mmm.survival.worldedit.CuboidIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Button;

/**
 * Events, die eine Interaktion von Spielern beschreiben
 *
 * @see org.bukkit.event.player.PlayerInteractEntityEvent
 * @see org.bukkit.event.player.PlayerInteractEvent
 */
public class InteractEvents implements Listener {
  private static final Map<Player, BlockVector> loc1 = new HashMap<>(), loc2 = new HashMap<>();
  private static final Map<Player, List<Block>> show = new HashMap<>();

  private static void createRegion(final Player player) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player, player.getName());

    new Thread(() -> {
      RegionManager manager = SurvivalData.getInstance().getDynmap().getRegionManager();
      ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(player.getUniqueId().toString(), loc1.get(player), loc2.get(player));
      int x1 = cuboidRegion.getMinimumPoint().getBlockX();
      int y1 = cuboidRegion.getMinimumPoint().getBlockY();
      int z1 = cuboidRegion.getMinimumPoint().getBlockZ();
      int x2 = cuboidRegion.getMaximumPoint().getBlockX();
      int y2 = cuboidRegion.getMaximumPoint().getBlockY();
      int z2 = cuboidRegion.getMaximumPoint().getBlockZ();
      checkValid(player, survivalPlayer, manager, cuboidRegion, x1, y1, z1, x2, y2, z2);
      Thread.currentThread().interrupt();
    }).start();
  }

  private static void checkValid(final Player player, final SurvivalPlayer survivalPlayer, final RegionManager manager,
                                 final ProtectedCuboidRegion cuboidRegion, final int xMin, final int yMin, final int zMin, final int xMax,
                                 final int yMax, final int zMax) {
    if (validZone(Math.abs(xMin - xMax), Math.abs(zMin - zMax), survivalPlayer.getMaxzone(), survivalPlayer)) {
      final CuboidIterator blocks = new CuboidIterator(Bukkit.getWorld("world"), xMin, yMin, zMin, xMax, yMax, zMax);
      final boolean found = checkBlocks(player, manager, blocks);
      loc1.remove(player);
      loc2.remove(player);
      settingUpZone(survivalPlayer, manager, cuboidRegion, found);
    }
  }

  private static boolean checkBlocks(final Player player, final RegionManager manager, final CuboidIterator blocks) {
    boolean found = false;
    while (blocks.hasNext()) {
      final Block block = (Block) ((Iterator<?>) blocks).next();
      if (Regions.checkRegionLocationIn(manager, block.getLocation()) != null) {
        found = true;
        player.sendMessage(Messages.NO_DUPLICATE_ZONE);
        break;
      }
    }
    return found;
  }

  private static void settingUpZone(final SurvivalPlayer survivalPlayer, final RegionManager manager,
                                    final ProtectedCuboidRegion cuboidRegion, final boolean found) {
    survivalPlayer.setZonenedit(false);
    show.get(survivalPlayer.getPlayer()).forEach(block -> survivalPlayer.getPlayer().sendBlockChange(block.getLocation(), block.getBlockData()));
    checkFound(survivalPlayer, manager, cuboidRegion, found);
  }

  private static void checkFound(final SurvivalPlayer survivalPlayer, final RegionManager manager, final ProtectedCuboidRegion cuboidRegion,
                                 final boolean found) {
    if (!found) {
      final DefaultDomain defaultDomain = new DefaultDomain();
      defaultDomain.addPlayer(survivalPlayer.getPlayer().getUniqueId());
      cuboidRegion.setOwners(defaultDomain);
      cuboidRegion.setPriority(100);
      setFlags(cuboidRegion);
      manager.addRegion(cuboidRegion);

      try {
        manager.save();
      } catch (final StorageException ex) {
        ex.printStackTrace();
      }
      survivalPlayer.sendHotbarMessage("§7Du hast erfolgreich deine Zone erstellt.");
    }
  }

  private static void setFlags(final ProtectedCuboidRegion cuboidRegion) {
    final Map<Flag<?>, Object> flags = new HashMap<>();
    flags.put(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
    flags.put(Flags.POTION_SPLASH, StateFlag.State.DENY);
    flags.put(Flags.FIRE_SPREAD, StateFlag.State.DENY);
    flags.put(Flags.LAVA_FIRE, StateFlag.State.DENY);
    cuboidRegion.setFlags(flags);
  }

  private static boolean validZone(final int a, final int b, final int max, final SurvivalPlayer survivalPlayer) {
    if (a >= survivalPlayer.getMaxzone() && a <= max && b >= survivalPlayer.getMaxzone() && b <= max) {
      return true;
    } else {
      survivalPlayer.getPlayer().sendMessage(Messages.PREFIX + " §cDeine Zone darf minimal " + survivalPlayer.getMaxzone() + "x" +
          survivalPlayer.getMaxzone() + " Blöcke groß sein. Deine Zone ist " + a + "x" + b + " Blöcke groß.");
    }

    return false;
  }

  /**
   * Wenn ein Spieler interagiert
   *
   * @param e PlayerInteractEvent
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @SuppressWarnings("deprecation")
  @EventHandler
  public void onInteract(final PlayerInteractEvent e) {
    final Player player = e.getPlayer();
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player, player.getName());

    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && e.getItem().getType().equals(Material.STICK)) {
      //Keine Zone vorhanden
      if (survivalPlayer != null && survivalPlayer.isZonenedit()) {
        editZone(player, e);
      } else if (survivalPlayer != null && survivalPlayer.isZonensearch()) {
        searchZone(player, e);
      }
    }

  }

  @SuppressWarnings("deprecation")
  private void editZone(final Player player, final PlayerInteractEvent e) {
    final Location loc = e.getClickedBlock().getLocation();

    loc.setY(loc1.containsKey(player) ? 256 : 0);
    isLocationSet(player, loc);
    player.sendMessage(Messages.PREFIX + " §7Du hast Position §e" + (loc1.containsKey(player) && loc2.containsKey(player) ? "2." : "1.") + " §7gesetzt.");
    zoneScheduler(player, e);
    if (loc1.containsKey(player) && loc2.containsKey(player)) {
      createRegion(player);
    }
  }

  private void isLocationSet(final Player player, final Location loc) {
    if (loc1.containsKey(player)) {
      loc2.put(player, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    } else {
      loc1.put(player, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    }
  }

  @SuppressWarnings("deprecation")
  private void zoneScheduler(final Player player, final PlayerInteractEvent e) {
    Bukkit.getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), () -> {

      if (loc1.containsKey(player) && !loc2.containsKey(player)) {
        final List<Block> blocks = new ArrayList<>();

        player.sendBlockChange(e.getClickedBlock().getLocation(), Material.LIME_STAINED_GLASS, (byte) 0);
        final Location beacon = e.getClickedBlock().getLocation().subtract(0, 1, 0);
        final Location ironblock = e.getClickedBlock().getLocation().subtract(0, 2, 0);

        replaceBlocks(player, blocks, beacon, ironblock);

        if (show.containsKey(player)) {
          blocks.addAll(show.get(player));
        }

        show.put(player, blocks);
      }

    }, 10L);
  }

  @SuppressWarnings("deprecation")
  private void replaceBlocks(final Player player, final List<Block> blocks, final Location beacon, final Location ironblock) {
    player.sendBlockChange(beacon, Material.BEACON, (byte) 0);
    player.sendBlockChange(ironblock, Material.IRON_BLOCK, (byte) 0);
    blocks.add(beacon.getBlock());
    blocks.add(ironblock.getBlock());

    replace(player, blocks, ironblock);
  }

  @SuppressWarnings("deprecation")
  private void replace(final Player player, final List<Block> blocks, final Location ironblock) {
    IntStream.range(-1, 2).forEach(x ->
        IntStream.range(-1, 2).forEach(z -> {
          final Location block = ironblock.clone().add(x, 0, z);

          player.sendBlockChange(block, Material.IRON_BLOCK, (byte) 0);
          blocks.add(block.getBlock());
        }));
  }

  private void searchZone(final Player player, final PlayerInteractEvent e) {
    new Thread(() -> {
      checkNoZoneFound(player, e);
      Thread.currentThread().interrupt();
    }).start();
  }

  private void checkNoZoneFound(final Player player, final PlayerInteractEvent event) {
    if (noZoneFound(player, event)) {
      final String name = Objects.requireNonNull(Regions.checkRegionLocationIn(SurvivalData.getInstance().getDynmap().getRegionManager(),
          event.getClickedBlock().getLocation())).getId();
      final UUID uuid = UUID.fromString(name);
      UUIDFetcher.getName(uuid, name1 -> player.sendMessage(Messages.PREFIX + " §7Es wurde die Zone von §e" + name1 + " §7gefunden."));

      if (name.toLowerCase().contains("spawnzone")) {
        player.sendMessage(Messages.SPAWNZONE_FOUND);
      }
    }
  }

  private boolean noZoneFound(final Player player, final PlayerInteractEvent event) {
    if (Regions.checkRegionLocationIn(SurvivalData.getInstance().getDynmap().getRegionManager()/*.getRegion()*/, event.getClickedBlock()
        .getLocation()) == null) {
      player.sendMessage(Messages.PREFIX + " §7Es wurde keine Zone bei §8(§e" + event.getClickedBlock().getLocation().getBlockX() + "§7/" +
          "§e" + event.getClickedBlock().getLocation().getBlockZ() + "§8) §7gefunden.");
      return false;
    }
    return true;
  }

  /**
   * Wenn ein Spieler mit einem Button interagiert
   *
   * @param event PlayerInteractEvent
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInteractButton(final PlayerInteractEvent event) {
    if (event.getClickedBlock().getState() instanceof Button) {
      final Block b = event.getClickedBlock().getRelative(((Button) event.getClickedBlock().getState()).getAttachedFace(), 2);
      if (b.getState() instanceof CommandBlock) {
        final CommandBlock commandblock = (CommandBlock) b.getState();
        event.setCancelled(true);
        checkArguments(event, commandblock);
      }
    }
  }

  private void checkArguments(final PlayerInteractEvent event, final CommandBlock commandblock) {
    final String[] args = commandblock.getCommand().split(" ");

    if (args[0].equals("FARMWELT")) {
      performFarmwelt(event);
    } else if (args[0].equals("FACEBOOK")) {
      performSocial(event, " §7Klicke §ehier §7zu unserem Facebook Profil.", "");
    } else if (args[0].equals("WEBSEITE")) {
      performSocial(event, " §7Klicke §ehier §7zu unserer Webseite.", "http://www.MineMagicMania.de/");
    } else if (args[0].equals("YOUTUBE")) {
      performSocial(event, " §7Klicke §ehier §7zu unserem Youtube Kanal.", "");
    } else if (args[0].equals("YOUTUBE")) {
      performSocial(event, " §7Klicke §ehier §7zu unserem Twitter Profil.", "");
    }
  }

  private void performSocial(final PlayerInteractEvent e, final String s, final String s2) {
    final TextComponent facebook = new TextComponent(Messages.PREFIX + s);
    facebook.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, s2));
    e.getPlayer().spigot().sendMessage(facebook);
  }

  private void performFarmwelt(final PlayerInteractEvent e) {
    e.getPlayer().teleport(Bukkit.getWorld("farmwelt").getSpawnLocation());
    e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3F, 0.3F);
    e.getPlayer().sendMessage(Messages.PREFIX + " §7Du wurdest zum §eFarmwelt-Spawn §7teleportiert.");
  }

  /**
   * Wenn ein Spieler mit einem anderen Entity interagiert
   *
   * @param e PlayerInteractEntityEvent
   * @see org.bukkit.event.player.PlayerInteractEntityEvent
   */
  @EventHandler
  public void onInteractEntity(final PlayerInteractEntityEvent e) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(e.getPlayer(), e.getPlayer().getName());
    if (survivalPlayer.isTamed()) {
      isTamed((Tameable) e.getRightClicked(), e, survivalPlayer);
    }
  }

  private void isTamed(final Tameable tameable, final PlayerInteractEntityEvent event, final SurvivalPlayer survivalPlayer) {
    if (tameable.getOwner().getUniqueId().equals(event.getPlayer().getUniqueId())) {
      tameable.setTamed(false);
      event.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast das Tier freigelassen.");
      survivalPlayer.setTamed(false);

    } else {
      event.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast dieses Tier nicht gezähmt.");
    }
  }

}
