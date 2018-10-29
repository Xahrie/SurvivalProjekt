package net.mmm.survival.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.regions.DynmapWorldGuardPlugin;
import net.mmm.survival.regions.Regions;
import net.mmm.survival.regions.SurvivalWorld;
import net.mmm.survival.util.Konst;
import net.mmm.survival.util.Messages;
import net.mmm.survival.util.UUIDUtils;
import net.mmm.survival.worldedit.CuboidIterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.CommandBlock;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
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

  /**
   * @param event PlayerInteractEvent => Wenn ein Spieler interagiert
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler
  public void onInteract(final PlayerInteractEvent event) {
    final Player player = event.getPlayer();
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
    final Action playerAction = event.getAction();
    if (playerAction.equals(Action.RIGHT_CLICK_BLOCK) && event.getItem() != null) {
      final ItemStack usedItem = event.getItem();
      final Material materialType = usedItem.getType();

      if (materialType.equals(Material.STICK)) {
        //Keine Zone vorhanden
        if (survivalPlayer != null && survivalPlayer.isZonenedit()) {
          evaluateEditZone(player, event);
        } else if (survivalPlayer != null && survivalPlayer.isZonensearch()) {
          evaluateFindZone(player, event);
        }
      }
    }

  }

  private void evaluateEditZone(final Player editor, final PlayerInteractEvent event) {
    final Block clickedBlock = event.getClickedBlock();
    final Location firstLocation = clickedBlock.getLocation();
    firstLocation.setY(loc1.containsKey(editor) ? 256 : 0);
    updateZoneCorner(editor, firstLocation);

    editor.sendMessage(Messages.PREFIX + " §7Du hast Position §e" + (loc1.containsKey(editor) &&
        loc2.containsKey(editor) ? "2." : "1.") + " §7gesetzt.");
    performScheduler(editor, event);
    if (loc1.containsKey(editor) && loc2.containsKey(editor)) {
      performCreateRegion(editor);
    }
  }

  private void evaluateFindZone(final Player finder, final PlayerInteractEvent event) {
    new Thread(() -> {
      evaluateNoZoneFound(finder, event);
      Thread.currentThread().interrupt();
    }).start();
  }

  private void updateZoneCorner(final Player editor, final Location location) {
    if (loc1.containsKey(editor)) {
      loc2.put(editor, new BlockVector(location.getX(), location.getY(), location.getZ()));
    } else {
      loc1.put(editor, new BlockVector(location.getX(), location.getY(), location.getZ()));
    }
  }

  @SuppressWarnings("deprecation")
  private void performScheduler(final Player editor, final PlayerInteractEvent event) {
    Bukkit.getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), () -> {
      if (loc1.containsKey(editor) && !loc2.containsKey(editor)) {
        final Block clickedBlock = event.getClickedBlock();
        final Location clickedBlockLocation = clickedBlock.getLocation();
        editor.sendBlockChange(clickedBlockLocation, Material.LIME_STAINED_GLASS, (byte) 0);

        final Location beacon = clickedBlockLocation.subtract(0, 1, 0);
        final Location ironblock = clickedBlockLocation.subtract(0, 2, 0);
        final List<Block> blocks = performReplaceBlocks(editor, beacon, ironblock);

        if (show.containsKey(editor)) {
          blocks.addAll(show.get(editor));
        }
        show.put(editor, blocks);
      }
    }, 10L);
  }

  private void performCreateRegion(final Player player) {
    new Thread(() -> {
      final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);
      final ProtectedCuboidRegion cuboidRegion = new ProtectedCuboidRegion(
          survivalPlayer.getUuid().toString(), loc1.get(player), loc2.get(player));
      final DynmapWorldGuardPlugin dynmapPlugin = SurvivalData.getInstance().getDynmap();
      final RegionManager manager = dynmapPlugin.getRegionManager();
      final BlockVector minimumPoint = cuboidRegion.getMinimumPoint();
      final BlockVector maximumPoint = cuboidRegion.getMaximumPoint();

      evaluateCreateRegion(survivalPlayer, manager, cuboidRegion, minimumPoint, maximumPoint);

      Thread.currentThread().interrupt();
    }).start();
  }

  private void evaluateNoZoneFound(final Player finder, final PlayerInteractEvent event) {
    final DynmapWorldGuardPlugin dynmapPlugin = SurvivalData.getInstance().getDynmap();
    final RegionManager regionManager = dynmapPlugin.getRegionManager();

    if (checkNoZoneFound(finder, event)) {
      final Block clickedBlock = event.getClickedBlock();
      final ProtectedRegion selectedRegion = Regions.evaluateRegionOnCurrentLocation(regionManager, clickedBlock.getLocation());
      if (checkZoneFound(finder, selectedRegion) && selectedRegion != null) {
        zoneFound(finder, selectedRegion);
      }
    }
  }

  @SuppressWarnings("deprecation")
  private List<Block> performReplaceBlocks(final Player editor, final Location beacon, final Location ironblock) {
    editor.sendBlockChange(beacon, Material.BEACON, (byte) 0);
    editor.sendBlockChange(ironblock, Material.IRON_BLOCK, (byte) 0);

    final List<Block> blocks = new ArrayList<>();
    blocks.add(beacon.getBlock());
    blocks.add(ironblock.getBlock());
    return buildBlocks(editor, blocks, ironblock);
  }

  private void evaluateCreateRegion(final SurvivalPlayer target, final RegionManager manager, final ProtectedCuboidRegion cuboidRegion,
                                    final BlockVector minimumPoint, final BlockVector maximumPoint) {
    final int xMin = minimumPoint.getBlockX();
    final int zMin = minimumPoint.getBlockZ();
    final int xMax = maximumPoint.getBlockX();
    final int zMax = maximumPoint.getBlockZ();

    if (checkValidZone(Math.abs(xMin - xMax), Math.abs(zMin - zMax), target.getMaxzone(), target)) {
      final CuboidIterator blocks = new CuboidIterator(SurvivalWorld.BAUWELT.get(), minimumPoint, maximumPoint);
      final boolean found = checkBlocks(target.getPlayer(), manager, blocks);
      loc1.remove(target.getPlayer());
      loc2.remove(target.getPlayer());
      evaluateSettingUpZone(target, manager, cuboidRegion, found);
    }
  }

  private boolean checkNoZoneFound(final Player finder, final PlayerInteractEvent event) {
    final DynmapWorldGuardPlugin dynmapPlugin = SurvivalData.getInstance().getDynmap();
    final RegionManager regionManager = dynmapPlugin.getRegionManager();
    final Block clickedBlock = event.getClickedBlock();
    final Location clickedLocation = clickedBlock.getLocation();
    if (Regions.evaluateRegionOnCurrentLocation(regionManager, clickedLocation) != null) {
      return true;
    } else {
      finder.sendMessage(Messages.PREFIX + " §7Es wurde keine Zone bei §8(§e" +
          clickedLocation.getBlockX() + "§7/§e" + clickedLocation.getBlockZ() + "§8) §7gefunden.");
    }

    return false;
  }

  private boolean checkZoneFound(final Player finder, final ProtectedRegion selectedRegion) {
    if (selectedRegion != null) {
      return true;
    } else {
      finder.sendMessage(Messages.ZONE_NOT_FOUND);
    }

    return false;
  }

  private void zoneFound(final Player finder, final ProtectedRegion selectedRegion) {
    final String name = selectedRegion.getId();
    final UUID uuid = UUID.fromString(name);
    finder.sendMessage(Messages.PREFIX + "§7Es wurde die Zone von §e" + UUIDUtils.getName(uuid) + " §7gefunden.");

    if (name.toLowerCase().contains("spawnzone")) {
      finder.sendMessage(Messages.SPAWNZONE_FOUND);
    }
  }

  @SuppressWarnings("deprecation")
  private List<Block> buildBlocks(final Player editor, final List<Block> blocks, final Location ironblock) {
    for (int x = -1; x < 2; x++) {
      for (int z = -1; z < 2; z++) {
        final Location block = ironblock.clone().add(x, 0, z);
        editor.sendBlockChange(block, Material.IRON_BLOCK, (byte) 0);
        blocks.add(block.getBlock());
      }
    }
    return blocks;
  }

  private boolean checkValidZone(final int a, final int b, final int max, final SurvivalPlayer owner) {
    if (a >= owner.getMaxzone() && a <= max && b >= owner.getMaxzone() && b <= max) {
      return true;
    } else {
      final Player ownerPlayer = owner.getPlayer();
      ownerPlayer.sendMessage(Messages.PREFIX + " §cDeine Zone darf minimal " + owner.getMaxzone() +
          " x " + owner.getMaxzone() + " Blöcke groß sein. Deine Zone ist " + a + " x " + b + " Blöcke groß.");
    }
    return false;
  }

  private boolean checkBlocks(final Player player, final RegionManager manager, final CuboidIterator blocks) {
    while (blocks.hasNext()) {
      final Block block = (Block) ((Iterator<?>) blocks).next();
      if (Regions.evaluateRegionOnCurrentLocation(manager, block.getLocation()) != null) {
        player.sendMessage(Messages.NO_DUPLICATE_ZONE);
        return true;
      }
    }
    return false;
  }

  private void evaluateSettingUpZone(final SurvivalPlayer owner, final RegionManager manager,
                                     final ProtectedCuboidRegion cuboidRegion, final boolean found) {
    owner.setZonenedit(false);
    final Player ownerPlayer = owner.getPlayer();
    for (final Block block : show.get(ownerPlayer)) {
      ownerPlayer.sendBlockChange(block.getLocation(), block.getBlockData());
    }
    evaluateNoZoneThere(owner, manager, cuboidRegion, found);
  }

  private void evaluateNoZoneThere(final SurvivalPlayer owner, final RegionManager manager,
                                   final ProtectedCuboidRegion cuboidRegion, final boolean found) {
    if (!found) {
      final DefaultDomain defaultDomain = new DefaultDomain();
      defaultDomain.addPlayer(owner.getUuid());
      cuboidRegion.setOwners(defaultDomain);
      cuboidRegion.setPriority(100);
      cuboidRegion.setFlags(defaultFlagsFromNewRegion());
      manager.addRegion(cuboidRegion);

      try {
        manager.save();
      } catch (final StorageException ex) {
        ex.printStackTrace();
      }
    }
  }

  private Map<Flag<?>, Object> defaultFlagsFromNewRegion() {
    final Map<Flag<?>, Object> flags = new HashMap<>();
    flags.put(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
    flags.put(Flags.POTION_SPLASH, StateFlag.State.DENY);
    flags.put(Flags.FIRE_SPREAD, StateFlag.State.DENY);
    flags.put(Flags.LAVA_FIRE, StateFlag.State.DENY);

    return flags;
  }

  /**
   * @param event PlayerInteractEvent => Wenn ein Spieler mit einem Button interagiert
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInteractButton(final PlayerInteractEvent event) {
    final Block clicked = event.getClickedBlock();

    if (clicked.getState() instanceof Button) {
      final Button blockState = (Button) clicked.getState();
      final Block button = clicked.getRelative(blockState.getAttachedFace(), 2);
      if (button.getState() instanceof CommandBlock) {
        final CommandBlock commandblock = (CommandBlock) button.getState();
        event.setCancelled(true);
        evaluateArguments(event, commandblock);
      }
    }
  }

  private void evaluateArguments(final PlayerInteractEvent event, final CommandBlock commandblock) {
    final String[] args = commandblock.getCommand().split(" ");
    switch (args[0]) {
      case "FARMWELT":
        performFarmwelt(event);
        break;
      case "FACEBOOK":
        performSocial(event, " §7Klicke §ehier §7zu unserem Facebook Profil.", Konst.FACEBOOK);
        break;
      case "WEBSEITE":
        performSocial(event, " §7Klicke §ehier §7zu unserer Webseite.", Konst.WEBSITE);
        break;
      case "YOUTUBE":
        performSocial(event, " §7Klicke §ehier §7zu unserem Youtube Kanal.", Konst.YOUTUBE);
        break;
      case "TWITTER":
        performSocial(event, " §7Klicke §ehier §7zu unserem Twitter Profil.", Konst.TWITTER);
        break;
    }
  }

  private void performFarmwelt(final PlayerInteractEvent event) {
    final Player eventPlayer = event.getPlayer();
    final World farmweltWorld = SurvivalWorld.FARMWELT.get();
    eventPlayer.teleport(farmweltWorld.getSpawnLocation());
    eventPlayer.playSound(eventPlayer.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3F, 0.3F);
    eventPlayer.sendMessage(Messages.TELEPORT_FARMWELT);
  }

  private void performSocial(final PlayerInteractEvent event, final String messagePart1, final String messagePart2) {
    final TextComponent facebook = new TextComponent(Messages.PREFIX + messagePart1);
    facebook.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, messagePart2));
    final Player eventPlayer = event.getPlayer();
    final Player.Spigot spigot = eventPlayer.spigot();
    spigot.sendMessage(facebook);
  }

  /**
   * @param event PlayerInteractEntityEvent => Wenn ein Spieler mit einem anderen Entity interagiert
   * @see org.bukkit.event.player.PlayerInteractEntityEvent
   */
  @EventHandler
  public void onInteractEntity(final PlayerInteractEntityEvent event) {
    final SurvivalPlayer owner = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    if (owner.isTamed()) {
      evaluateTamed(event);
    }
  }

  private void evaluateTamed(final PlayerInteractEntityEvent event) {
    final Tameable tameable = (Tameable) event.getRightClicked();
    if (checkUUID(event)) {
      tameable.setTamed(false);
      final Player eventPlayer = event.getPlayer();
      eventPlayer.sendMessage(Messages.ENTITY_TAMED_NOTMORE);
      final SurvivalPlayer owner = SurvivalPlayer.findSurvivalPlayer(eventPlayer);
      owner.setTamed(false);
    }
  }

  private boolean checkUUID(final PlayerInteractEntityEvent event) {
    final Tameable tameable = (Tameable) event.getRightClicked();
    final AnimalTamer tameableOwner = tameable.getOwner();
    final UUID uuid = tameableOwner.getUniqueId();
    final SurvivalPlayer owner = SurvivalPlayer.findSurvivalPlayer(event.getPlayer());
    if (uuid.equals(owner.getUuid())) {
      return true;
    } else {
      final Player eventPlayer = event.getPlayer();
      eventPlayer.sendMessage(Messages.ENTITY_NOT_TAMED);
    }

    return false;
  }
}
