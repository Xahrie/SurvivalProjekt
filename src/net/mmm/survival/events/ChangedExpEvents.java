package net.mmm.survival.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/* 
 * @author Suders
 * Date: 05.10.2018
 * Time: 14:55:28
 * Location: SurvivalProjekt 
*/

public class ChangedExpEvents implements Listener {

  @EventHandler
  public void onLevelUp(ChangedExpEvent e) {
    if(e.changedLevel()) {
      //TODO (Akzeptiert:) Skillsystem 
    }
  }
  
}
