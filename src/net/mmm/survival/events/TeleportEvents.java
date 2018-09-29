package net.mmm.survival.events;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import net.mmm.survival.SurvivalData;
import net.mmm.survival.player.Licence;
import net.mmm.survival.player.SurvivalPlayer;
import net.mmm.survival.util.SurvivalWorld;

public class TeleportEvents implements Listener {

  /* Created by: Suders
   * Date: 30.09.2018
   * Time: 00:47:23
   * Location: SurvivalProjekt 
  */
  
  @EventHandler
  public void onTeleport(PlayerTeleportEvent e) {
    final Player p = e.getPlayer();
    if(e.getTo().getWorld().getName().equals(e.getFrom().getWorld().getName())) return;
    final World world = p.getWorld();
    final SurvivalWorld sworld = SurvivalWorld.getWorld(world.getName());
    Licence licence = null;
    try {
      licence = Licence.getLicence(sworld);
    } catch(NullPointerException exc) {}
    final SurvivalPlayer sp = SurvivalData.getInstance().getPlayers().get(p.getUniqueId());
    final LicenceAccesEvent event = new LicenceAccesEvent(e.getPlayer(), sp.hasLicence(licence == null ? null : licence));
    Bukkit.getPluginManager().callEvent(event);
    if(!event.hasAcces()) {
      e.setCancelled(true);
      return;
    }
  }
}
