package net.mmm.survival.commands;

import net.mmm.survival.Survival;
import net.mmm.survival.farming.Type;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Teleport fuehrt die Teleportation durch
 */
class Teleport {

  /**
   * Teleportation wir durchgefuehrt
   *
   * @param teleportedPlayer Spieler
   * @param location Target-Location
   */
  void teleport(final Player teleportedPlayer, final Location location) {
    final SurvivalPlayer teleported = SurvivalPlayer
        .findSurvivalPlayer(teleportedPlayer, teleportedPlayer.getName());
    teleported.setTeleport(true);
    countdown(location, teleported);
  }

  private void countdown(final Location location, final SurvivalPlayer teleported) {
    new BukkitRunnable() {
      int i = 3;

      @Override
      public void run() {
        if (teleported.isTeleport()) {
          if (i == 3) {
            teleported.getPlayer().sendMessage(Messages.TELEPORT_DONT_MOVE);
          }
          if (i == 2) {
            teleported.getPlayer().playSound(teleported.getPlayer().getLocation(),
                Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 1) {
            teleported.getPlayer().playSound(teleported.getPlayer().getLocation(),
                Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 0) {
            teleported.getStats().getStatistic(Type.WALK_LENGTH_CM).update(teleported);
            teleported.getPlayer().playSound(teleported.getPlayer().getLocation(),
                Sound.ENTITY_ENDERMAN_TELEPORT, 4.0F, 5.0F);
            teleported.getPlayer().teleport(location);
            teleported.setTeleport(false);
            cancel();
          }
          i -= 1;
        } else {
          teleported.getPlayer().sendMessage(Messages.TELEPORT_CANCELED);
          teleported.setTeleport(false);
          cancel();
        }
      }
    }.runTaskTimer(Survival.getInstance(), 0L, 20L);
  }
}
