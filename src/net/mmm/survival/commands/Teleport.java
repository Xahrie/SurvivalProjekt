package net.mmm.survival.commands;

import net.mmm.survival.Survival;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.Messages;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Teleport fuehrt die Teleportation durch
 */
public class Teleport {

  /**
   * Teleportation wir durchgefuehrt
   *
   * @param p Spieler
   * @param loc Target-Location
   */
  void teleport(final Player p, final Location loc) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(p);

    survivalPlayer.setTeleport(true);

    new BukkitRunnable() {

      int i = 3;

      @Override
      public void run() {
        if (survivalPlayer.isTeleport()) {
          if (i == 3) {
            p.sendMessage(Messages.PREFIX + " §7Du wirst teleportiert.. §e§o» Bewege dich nicht..");
          }
          if (i == 2) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 1) {
            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 0) {
            p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 4.0F, 5.0F);
            p.teleport(loc);
            survivalPlayer.setTeleport(false);
            cancel();
          }
          i -= 1;
        } else {
          p.sendMessage(Messages.PREFIX + " §cDie Teleportation wurde abgebrochen.. §7§o» Du hast dich bewegt.");
          survivalPlayer.setTeleport(false);
          cancel();
        }
      }
    }.runTaskTimer(Survival.getInstance(), 0L, 20L);
  }

}
