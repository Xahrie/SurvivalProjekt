package net.mmm.survival.events;

import net.mmm.survival.player.Licence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.SurvivalWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

/**
 * Created by: Suders
 * Date: 30.09.2018
 * Time: 00:47:23
 * Location: SurvivalProjekt
 */
public class TeleportEvents implements Listener {
  @EventHandler
  public void onTeleport(final PlayerTeleportEvent teleportEvent) {
    if (teleportEvent.getTo().getWorld().equals(SurvivalWorld.NETHER.get())) {
      travel(SurvivalWorld.NETHER, teleportEvent);
    } else if (teleportEvent.getTo().getWorld().equals(SurvivalWorld.END.get())) {
      travel(SurvivalWorld.END, teleportEvent);
    }
  }

  private void travel(final SurvivalWorld targetWorld, final PlayerTeleportEvent event) {
    final SurvivalPlayer traveler = SurvivalPlayer.findSurvivalPlayer(event.getPlayer(), event.getPlayer().getName());
    final Licence licence = Licence.getLicence(targetWorld);
    if (!traveler.hasLicence(licence)) {
      event.setCancelled(true);
    }
  }
}
