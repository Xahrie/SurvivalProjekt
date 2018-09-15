package de.mmm.survival.commands;

import de.mmm.survival.Survival;
import de.mmm.survival.player.SurvivalPlayer;
import de.mmm.survival.util.Messages;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Teleport {

  void teleport(final Player p, final Location loc) {
    new BukkitRunnable() {

      int i = 3;

      @Override
      public void run() {
        if (SurvivalPlayer.move.contains(p)) {
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
            SurvivalPlayer.move.remove(p);
            cancel();
          }
          i -= 1;
        } else {
          p.sendMessage(Messages.PREFIX + " §cTeleportation wurde abgebrochen.. §7§o» Du hast dich bewegt.");
          SurvivalPlayer.move.remove(p);
          cancel();
        }
      }
    }.runTaskTimer(Survival.getInstance(), 0L, 20L);
  }
}
