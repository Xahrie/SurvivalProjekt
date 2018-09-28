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
public class Teleport {

  /**
   * Teleportation wir durchgefuehrt
   *
   * @param player Spieler
   * @param loc Target-Location
   */
  void teleport(final Player player, final Location loc) {
    final SurvivalPlayer survivalPlayer = SurvivalPlayer.findSurvivalPlayer(player, player.getName());
    survivalPlayer.setTeleport(true);
    countdown(loc, survivalPlayer);
  }

  private void countdown(final Location loc, final SurvivalPlayer executor) {
    new BukkitRunnable() {

      int i = 3;

      @Override
      public void run() {
        if (executor.isTeleport()) {
          if (i == 3) {
            executor.getPlayer().sendMessage(Messages.TELEPORT_DONT_MOVE);
          }
          if (i == 2) {
            executor.getPlayer().playSound(executor.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 1) {
            executor.getPlayer().playSound(executor.getPlayer().getLocation(), Sound.UI_BUTTON_CLICK, 4.0F, 5.0F);
          }
          if (i == 0) {
            executor.getStats().getStatistic(Type.WALK_LENGTH_CM).update(executor);
            executor.getPlayer().playSound(executor.getPlayer().getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 4.0F, 5.0F);
            executor.getPlayer().teleport(loc);
            executor.setTeleport(false);
            cancel();
          }
          i -= 1;
        } else {
          executor.getPlayer().sendMessage(Messages.TELEPRT_CANCELED);
          executor.setTeleport(false);
          cancel();
        }
      }
    }.runTaskTimer(Survival.getInstance(), 0L, 20L);
  }

}
