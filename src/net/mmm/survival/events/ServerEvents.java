package net.mmm.survival.events;

import net.mmm.survival.Survival;
import net.mmm.survival.SurvivalData;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;

public class ServerEvents implements Listener {
  /**
   * Event beim enablen des Plugins
   *
   * @param event Event
   */
  @EventHandler(priority = EventPriority.MONITOR)
  public void onPluginEnable(final PluginEnableEvent event) {
    final Plugin plugin = event.getPlugin();
    final String name = plugin.getDescription().getName();

    if ((name.equals("dynmap") || name.equals("WorldGuard")) && Survival.getInstance().isEnabled() && SurvivalData.getInstance().getDynmap()
        .getWorldGuard().isEnabled()) {
      SurvivalData.getInstance().getDynmap().activate();
    }
  }

}
