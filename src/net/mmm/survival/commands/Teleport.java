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
   * @param player Spieler
   * @param loc Target-Location
   */
  void teleport(final Player player, final Location loc) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player);

    survivalPlayer.setTeleport(true);

    new BukkitRunnable() {

      int i = 3;

      @Override
      public void run() {
        if (survivalPlayer.isTeleport()) {
          if (i == 3) {
            player.sendMessage(Messages.PREFIX + " §7Du wirst teleportiert.. §e§o» Bewege dich nicht..");
          }
          if (i == 2) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 1) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 0) {
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 4.0F, 5.0F);
            player.teleport(loc);
            survivalPlayer.setTeleport(false);
            cancel();
          }
          i -= 1;
        } else {
          player.sendMessage(Messages.PREFIX + " §cDie Teleportation wurde abgebrochen.. §7§o» Du hast dich bewegt.");
          survivalPlayer.setTeleport(false);
          cancel();
        }
      }
    }.runTaskTimer(Survival.getInstance(), 0L, 20L);
  }

}
