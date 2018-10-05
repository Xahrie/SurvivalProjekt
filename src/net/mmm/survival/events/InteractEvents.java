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
import net.mmm.survival.regions.Regions;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
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
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
    new Thread(() -> {
      RegionManager manager = SurvivalData.getInstance().getDynmap().getRegionManager();
      ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(survivalPlayer.getUuid().toString(),
          loc1.get(player), loc2.get(player));
      int xMin = cuboidRegion.getMinimumPoint().getBlockX();
      int yMin = cuboidRegion.getMinimumPoint().getBlockY();
      int zMin = cuboidRegion.getMinimumPoint().getBlockZ();
      int xMax = cuboidRegion.getMaximumPoint().getBlockX();
      int yMax = cuboidRegion.getMaximumPoint().getBlockY();
      int zMax = cuboidRegion.getMaximumPoint().getBlockZ();
      checkValid(survivalPlayer, manager, cuboidRegion, xMin, yMin, zMin, xMax, yMax, zMax);
      Thread.currentThread().interrupt();
    }).start();
  }

  private static void checkValid(final SurvivalPlayer target, final RegionManager manager, final ProtectedCuboidRegion cuboidRegion,
                                 final int xMin, final int yMin, final int zMin, final int xMax, final int yMax, final int zMax) {
    if (validZone(Math.abs(xMin - xMax), Math.abs(zMin - zMax), target.getMaxzone(), target)) {
      final CuboidIterator blocks = new CuboidIterator(SurvivalWorld.BAUWELT.get(), xMin, yMin, zMin,
          xMax, yMax, zMax);
      final boolean found = checkBlocks(target.getPlayer(), manager, blocks);
      loc1.remove(target.getPlayer());
      loc2.remove(target.getPlayer());
      settingUpZone(target, manager, cuboidRegion, found);
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

  private static void settingUpZone(final SurvivalPlayer owner, final RegionManager manager, final ProtectedCuboidRegion cuboidRegion,
                                    final boolean found) {
    owner.setZonenedit(false);
    show.get(owner.getPlayer()).forEach(block ->
        owner.getPlayer().sendBlockChange(block.getLocation(), block.getBlockData()));
    checkFound(owner, manager, cuboidRegion, found);
  }

  private static void checkFound(final SurvivalPlayer owner, final RegionManager manager, final ProtectedCuboidRegion cuboidRegion,
                                 final boolean found) {
    if (!found) {
      final DefaultDomain defaultDomain = new DefaultDomain();
      defaultDomain.addPlayer(owner.getUuid());
      cuboidRegion.setOwners(defaultDomain);
      cuboidRegion.setPriority(100);
      setFlags(cuboidRegion);
      manager.addRegion(cuboidRegion);

      try {
        manager.save();
      } catch (final StorageException ex) {
        ex.printStackTrace();
      }
      owner.sendHotbarMessage(Messages.ZONE_CREATED);
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

  private static boolean validZone(final int a, final int b, final int max, final SurvivalPlayer owner) {
    if (a >= owner.getMaxzone() && a <= max && b >= owner.getMaxzone() && b <= max) {
      return true;
    } else {
      owner.getPlayer().sendMessage(Messages.PREFIX + " §cDeine Zone darf minimal " +
          owner.getMaxzone() + "x" + owner.getMaxzone() + " Blöcke groß sein. Deine Zone ist " +
          a + "x" + b + " Blöcke groß.");
    }
    return false;
  }

  /**
   * @param event PlayerInteractEvent -> Wenn ein Spieler interagiert
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
    if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null && event.getItem().getType().equals(Material.STICK)) {
      //Keine Zone vorhanden
      if (survivalPlayer != null && survivalPlayer.isZonenedit()) {
        editZone(player, event);
      } else if (survivalPlayer != null && survivalPlayer.isZonensearch()) {
        searchZone(player, event);
      }
    }
  }

  private void editZone(final Player editor, final PlayerInteractEvent event) {
    final Location firstLocation = event.getClickedBlock().getLocation();

    firstLocation.setY(loc1.containsKey(editor) ? 256 : 0);
    isLocationSet(editor, firstLocation);
    editor.sendMessage(Messages.PREFIX + " §7Du hast Position §e" + (loc1.containsKey(editor) &&
        loc2.containsKey(editor) ? "2." : "1.") + " §7gesetzt.");
    zoneScheduler(editor, event);
    if (loc1.containsKey(editor) && loc2.containsKey(editor)) {
      createRegion(editor);
    }
  }

  private void isLocationSet(final Player editor, final Location loc) {
    if (loc1.containsKey(editor)) {
      loc2.put(editor, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    } else {
      loc1.put(editor, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    }
  }

  @SuppressWarnings("deprecation")
  private void zoneScheduler(final Player editor, final PlayerInteractEvent event) {
    Bukkit.getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), () -> {
      if (loc1.containsKey(editor) && !loc2.containsKey(editor)) {
        editor.sendBlockChange(event.getClickedBlock().getLocation(), Material.LIME_STAINED_GLASS, (byte) 0);
        final List<Block> blocks = new ArrayList<>();
        final Location beacon = event.getClickedBlock().getLocation().subtract(0, 1, 0);
        final Location ironblock = event.getClickedBlock().getLocation().subtract(0, 2, 0);

        replaceBlocks(editor, blocks, beacon, ironblock);
        if (show.containsKey(editor)) {
          blocks.addAll(show.get(editor));
        }
        show.put(editor, blocks);
      }

    }, 10L);
  }

  @SuppressWarnings("deprecation")
  private void replaceBlocks(final Player editor, final List<Block> blocks, final Location beacon, final Location ironblock) {
    editor.sendBlockChange(beacon, Material.BEACON, (byte) 0);
    editor.sendBlockChange(ironblock, Material.IRON_BLOCK, (byte) 0);
    blocks.add(beacon.getBlock());
    blocks.add(ironblock.getBlock());
    replace(editor, blocks, ironblock);
  }

  @SuppressWarnings("deprecation")
  private void replace(final Player editor, final List<Block> blocks, final Location ironblock) {
    IntStream.range(-1, 2).forEach(x ->
        IntStream.range(-1, 2).forEach(z -> {
          final Location block = ironblock.clone().add(x, 0, z);
          editor.sendBlockChange(block, Material.IRON_BLOCK, (byte) 0);
          blocks.add(block.getBlock());
        }));
  }

  private void searchZone(final Player finder, final PlayerInteractEvent event) {
    new Thread(() -> {
      checkNoZoneFound(finder, event);
      Thread.currentThread().interrupt();
    }).start();
  }

  private void checkNoZoneFound(final Player finder, final PlayerInteractEvent event) {
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();

    if (noZoneFound(finder, event)) {
      final String name = Objects.requireNonNull(Regions.checkRegionLocationIn(regionManager,
          event.getClickedBlock().getLocation())).getId();
      final UUID uuid = UUID.fromString(name);
      UUIDFetcher.getName(uuid, playerName -> finder.sendMessage(Messages.PREFIX +
          "§7Es wurde die Zone von §e" + playerName + " §7gefunden."));

      if (name.toLowerCase().contains("spawnzone")) {
        finder.sendMessage(Messages.SPAWNZONE_FOUND);
      }
    }
  }

  private boolean noZoneFound(final Player finder, final PlayerInteractEvent event) {
    final Location clickedLocation = event.getClickedBlock().getLocation();
    final RegionManager regionManager = SurvivalData.getInstance().getDynmap().getRegionManager();

    if (Regions.checkRegionLocationIn(regionManager, clickedLocation) == null) {
      finder.sendMessage(Messages.PREFIX + " §7Es wurde keine Zone bei §8(§e" +
          clickedLocation.getBlockX() + "§7/§e" + clickedLocation.getBlockZ() + "§8) §7gefunden.");
      return false;
    }
    return true;
  }

  /**
   * @param event PlayerInteractEvent -> Wenn ein Spieler mit einem Button interagiert
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInteractButton(final PlayerInteractEvent event) {
    final Block clicked = event.getClickedBlock();
    if (clicked.getState() instanceof Button) {
      final Block button = clicked.getRelative(((Button) clicked.getState()).getAttachedFace(), 2);
      if (button.getState() instanceof CommandBlock) {
        final CommandBlock commandblock = (CommandBlock) button.getState();
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
      performSocial(event, " §7Klicke §ehier §7zu unserem Facebook Profil.", Konst.FACEBOOK);
    } else if (args[0].equals("WEBSEITE")) {
      performSocial(event, " §7Klicke §ehier §7zu unserer Webseite.", Konst.WEBSITE);
    } else if (args[0].equals("YOUTUBE")) {
      performSocial(event, " §7Klicke §ehier §7zu unserem Youtube Kanal.", Konst.YOUTUBE);
    } else if (args[0].equals("TWITTER")) {
      performSocial(event, " §7Klicke §ehier §7zu unserem Twitter Profil.", Konst.TWITTER);
    }
  }

  private void performSocial(final PlayerInteractEvent event, final String messagePart1, final String messagePart2) {
    final TextComponent facebook = new TextComponent(Messages.PREFIX + messagePart1);
    facebook.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, messagePart2));
    event.getPlayer().spigot().sendMessage(facebook);
  }

  private void performFarmwelt(final PlayerInteractEvent event) {
    event.getPlayer().teleport(SurvivalWorld.FARMWELT.get().getSpawnLocation());
    event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3F, 0.3F);
    event.getPlayer().sendMessage(Messages.TELEPORT_FARMWELT);
  }

  /**
   * @param event PlayerInteractEntityEvent -> Wenn ein Spieler mit einem anderen Entity interagiert
   * @see org.bukkit.event.player.PlayerInteractEntityEvent
   */
  @EventHandler
  public void onInteractEntity(final PlayerInteractEntityEvent event) {
    final SurvivalPlayer owner = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    if (owner.isTamed()) {
      isTamed((Tameable) event.getRightClicked(), event, owner);
    }
  }

  private void isTamed(final Tameable tameable, final PlayerInteractEntityEvent event, final SurvivalPlayer owner) {
    if (tameable.getOwner().getUniqueId().equals(owner.getUuid())) {
      tameable.setTamed(false);
      event.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast das Tier freigelassen.");
      owner.setTamed(false);
    } else {
      event.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast dieses Tier nicht gezähmt.");
    }
  }

}
