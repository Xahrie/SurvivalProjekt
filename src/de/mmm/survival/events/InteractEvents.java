package de.mmm.survival.events;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import de.mmm.survival.Survival;
import de.mmm.survival.player.Hotbar;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Messages;
import de.mmm.survival.util.Regions;
import de.mmm.survival.util.UUIDFetcher;
import de.mmm.survival.worldedit.CuboidIterator;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Events, die eine Interaktion von Spielern beschreiben
 *
 * @see org.bukkit.event.player.PlayerInteractEntityEvent
 * @see org.bukkit.event.player.PlayerInteractEvent
 */
public class InteractEvents implements Listener {

  private static final Map<Player, BlockVector> loc1 = new HashMap<>();
  private static final Map<Player, BlockVector> loc2 = new HashMap<>();
  private static final Map<Player, List<Block>> show = new HashMap<>();

  /**
   * Erstellen einer Region
   *
   * @param p Spieler
   */
  private static void createRegion(final Player p) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

    new Thread(() -> {
      RegionManager manager = Survival.getInstance().dynmap.rg;
      ProtectedCuboidRegion pr = new ProtectedCuboidRegion(p.getUniqueId().toString(), loc1.get(p), loc2.get(p));

      int x1 = pr.getMinimumPoint().getBlockX();
      int y1 = pr.getMinimumPoint().getBlockY();
      int z1 = pr.getMinimumPoint().getBlockZ();
      int x2 = pr.getMaximumPoint().getBlockX();
      int y2 = pr.getMaximumPoint().getBlockY();
      int z2 = pr.getMaximumPoint().getBlockZ();
      int a = x1 - x2;

      a = a < 0 ? a * -1 : a;
      int b = z1 - z2;

      b = b < 0 ? b * -1 : b;
      int max = survivalPlayer.getMaxzone();

      if (a >= 20 && a <= max && b >= 20 && b <= max) {
        CuboidIterator blocks = new CuboidIterator(Bukkit.getWorld("world"), x1, y1, z1, x2, y2, z2);
        boolean found = false;
        while (blocks.hasNext()) {
          Block block = (Block) ((Iterator<?>) blocks).next();
          if (Regions.checkRegionLocationIn(manager, block.getLocation()) != null) {
            found = true;
            System.out.println(Objects.requireNonNull(Regions.checkRegionLocationIn(manager, block.getLocation())).getId());

            p.sendMessage(Messages.PREFIX + " §cDu kannst keine Zone in einer bereits bestehenden Zone erstellen.");
            break;
          }
        }

        loc1.remove(p);
        loc2.remove(p);
        survivalPlayer.setZonenedit(false);
        show.get(p).forEach(block -> p.sendBlockChange(block.getLocation(), block.getBlockData()));
        if (!found) {
          survivalPlayer.setMaxzone(100);
          DefaultDomain dd = new DefaultDomain();
          dd.addPlayer(p.getUniqueId());
          pr.setOwners(dd);
          pr.setPriority(100);
          Map<Flag<?>, Object> flags = new HashMap<>();

          flags.put(Flags.CREEPER_EXPLOSION, StateFlag.State.DENY);
          flags.put(Flags.POTION_SPLASH, StateFlag.State.DENY);
          flags.put(Flags.FIRE_SPREAD, StateFlag.State.DENY);
          flags.put(Flags.LAVA_FIRE, StateFlag.State.DENY);
          pr.setFlags(flags);
          manager.addRegion(pr);
          try {
            manager.save();
          } catch (StorageException ignored) {
          }
          Hotbar.send(p);
        }
      } else {
        p.sendMessage(Messages.PREFIX + " §cDeine Zone darf minimal 20x20 und maximal 100x100 Blöcke groß sein. Deine" +
                " Zone ist " + a + "x" + b + " Blöcke groß.");
      }
      Thread.currentThread().interrupt();
    }).start();

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
    final Player p = e.getPlayer();
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

    if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem() != null && e.getItem().getType().equals(Material.STICK)) {
      //Keine Zone vorhanden
      if (survivalPlayer != null && survivalPlayer.isZonenedit()) {
        createZone(p, e);
      } else if (survivalPlayer != null && survivalPlayer.isZonensearch()) {
        zoneGefunden(p, e);
      }
    }

  }

  /**
   * Wenn ein Spieler mit einem Button interagiert
   *
   * @param e PlayerInteractEvent
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onInteractButton(final PlayerInteractEvent e) {
    if (e.getClickedBlock().getState() instanceof Button) {
      final Button button = (Button) e.getClickedBlock().getState();
      final Block b = e.getClickedBlock().getRelative(button.getAttachedFace(), 2);
      if (b.getState() instanceof CommandBlock) {
        final CommandBlock commandblock = (CommandBlock) b.getState();
        e.setCancelled(true);
        final String[] args = commandblock.getCommand().split(" ");
        switch (args[0]) {
          case "FARMWELT":
            e.getPlayer().teleport(Survival.getInstance().spawns.get("farmwelt"));
            e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 0.3F, 0.3F);
            e.getPlayer().sendMessage(Messages.PREFIX + " §7Du wurdest zum §eFarmwelt-Spawn §7teleportiert.");
            break;
          case "FACEBOOK":
            final TextComponent facebook = new TextComponent(Messages.PREFIX + " §7Klicke §ehier §7zu unserem Facebook Profil.");
            facebook.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
            e.getPlayer().spigot().sendMessage(facebook);
            break;
          case "WEBSITE":
            final TextComponent webseite = new TextComponent(Messages.PREFIX + " §7Klicke §ehier §7zu unserer Webseite.");
            webseite.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "http://www.MineMagicMania.de/"));
            e.getPlayer().spigot().sendMessage(webseite);
            break;
          case "YOUTUBE":
            final TextComponent youtube = new TextComponent(Messages.PREFIX + " §7Klicke §ehier §7zu unserem Youtube Kanal.");
            youtube.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, ""));
            e.getPlayer().spigot().sendMessage(youtube);
            break;
          case "TWITTER":
            final TextComponent twitter = new TextComponent(Messages.PREFIX + " §7Klicke §ehier §7zu unserem Twitter Profil.");
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

  /**
   * Wenn ein Spieler mit einem anderen Entity interagiert
   *
   * @param e PlayerInteractEntityEvent
   * @see org.bukkit.event.player.PlayerInteractEntityEvent
   */
  @EventHandler
  public void onInteractEntity(final PlayerInteractEntityEvent e) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(e.getPlayer());
    if (survivalPlayer.isTamed()) {
      final Tameable en = (Tameable) e.getRightClicked();
      if (en.getOwner().getUniqueId().equals(e.getPlayer().getUniqueId())) {
        en.setTamed(false);
        e.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast das Tier freigelassen.");
        survivalPlayer.setTamed(false);
      } else {
        e.getPlayer().sendMessage(Messages.PREFIX + " §7Du hast dieses Tier nicht gezähmt.");
      }
    }

  }

  /**
   * Erstelle eine Zone
   *
   * @param p Spieler
   * @param e PlayerInteractEvent
   * @see org.bukkit.event.player.PlayerInteractEvent
   */
  @SuppressWarnings("deprecation")
  private void createZone(final Player p, final PlayerInteractEvent e) {
    final Location loc = e.getClickedBlock().getLocation();

    loc.setY(loc1.containsKey(p) ? 256 : 0);
    if (loc1.containsKey(p)) {
      loc2.put(p, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    } else {
      loc1.put(p, new BlockVector(loc.getX(), loc.getY(), loc.getZ()));
    }
    p.sendMessage(Messages.PREFIX + " §7Du hast Position §e" + (loc1.containsKey(p) && loc2.containsKey(p) ?
            "2." : "1.") + " §7gesetzt.");
    Bukkit.getScheduler().scheduleAsyncDelayedTask(Survival.getInstance(), () -> {

      if (loc1.containsKey(p) && !loc2.containsKey(p)) {
        final List<Block> blocks = new ArrayList<>();

        p.sendBlockChange(e.getClickedBlock().getLocation(), Material.LIME_STAINED_GLASS, (byte) 0);
        final Location beacon = e.getClickedBlock().getLocation().subtract(0, 1, 0);
        final Location ironblock = e.getClickedBlock().getLocation().subtract(0, 2, 0);

        p.sendBlockChange(beacon, Material.BEACON, (byte) 0);
        p.sendBlockChange(ironblock, Material.IRON_BLOCK, (byte) 0);
        blocks.add(beacon.getBlock());
        blocks.add(ironblock.getBlock());

        for (int x = -1; x <= 1; x++) {
          for (int z = -1; z <= 1; z++) {
            final Location block = ironblock.clone().add(x, 0, z);
            p.sendBlockChange(block, Material.IRON_BLOCK, (byte) 0);
            blocks.add(block.getBlock());
          }
        }
        if (show.containsKey(p)) {
          blocks.addAll(show.get(p));
        }
        show.put(p, blocks);
      }

    }, 10L);

    if (loc1.containsKey(p) && loc2.containsKey(p)) {
      createRegion(p);
    }
  }

  private void zoneGefunden(final Player p, final PlayerInteractEvent e) {
    new Thread(() -> {
      if (Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg, e.getClickedBlock().getLocation()) != null) {
        String name = Objects.requireNonNull(Regions.checkRegionLocationIn(Survival.getInstance().dynmap.rg,
                e.getClickedBlock().getLocation())).getId();
        UUID uuid = UUID.fromString(name);
        UUIDFetcher.getName(uuid, name1 -> p.sendMessage(Messages.PREFIX + " §7Es wurde die Zone von §e" +
                name1 + " §7gefunden."));

        if (name.toLowerCase().contains("spawnzone") || name.toLowerCase().equals("spawnzone")) {
          p.sendMessage(Messages.PREFIX + " §7Es wurde die Zone §eSpawnzone §7gefunden.");
        }
      } else {
        p.sendMessage(Messages.PREFIX + " §7Es wurde keine Zone bei §8(§e" + e.getClickedBlock().getLocation
                ().getBlockX() + "§7/" + "§e" + e.getClickedBlock().getLocation().getBlockZ() + "§8) §7gefunden.");
      }
      Thread.currentThread().interrupt();
    }).start();
  }

}
