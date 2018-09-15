package de.pas123.survival.util;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.GlobalProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import net.md_5.bungee.api.ChatColor;

public class Regions {
	public static String checkRegionId(String id, boolean allowGlobal)
		    throws CommandException
		  {
		    if (!ProtectedRegion.isValidId(id)) {
		      throw new CommandException("The region name of '" + id + "' contains characters that are not allowed.");
		    }
		    if ((!allowGlobal) && (id.equalsIgnoreCase("__global__"))) {
		      throw new CommandException("Sorry, you can't use __global__ here.");
		    }
		    return id;
		  }
	public static ProtectedRegion checkExistingRegion(RegionManager regionManager, String id, boolean allowGlobal)
	  {
	    try
	    {
	      checkRegionId(id, allowGlobal);
	    }
	    catch (CommandException e)
	    {
	      e.printStackTrace();
	    }
	    ProtectedRegion region = regionManager.getRegion(id);
	    if (region == null)
	    {
	      if (id.equalsIgnoreCase("__global__"))
	      {
	        region = new GlobalProtectedRegion(id);
	        regionManager.addRegion(region);
	        return region;
	      }
	      return null;
	    }
	    return region;
	  }
	  public static ProtectedRegion checkRegionStandingIn(RegionManager regionManager, Player player, boolean allowGlobal)
	  {
	    ApplicableRegionSet set = regionManager.getApplicableRegions(new Vector(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ()));
	    if (set.size() == 0)
	    {
	      if (allowGlobal)
	      {
	        ProtectedRegion global = null;
	        global = checkExistingRegion(regionManager, "__global__", true);
	        player.sendMessage(ChatColor.GRAY + "You're not standing in any " + "regions. Using the global region for this world instead.");
	        
	        return global;
	      }
	      return null;
	    }
	    if (set.size() > 1)
	    {
	      StringBuilder builder = new StringBuilder();
	      boolean first = true;
	      for (ProtectedRegion region : set)
	      {
	        if (!first) {
	          builder.append(", ");
	        }
	        first = false;
	        builder.append(region.getId());
	      }
	      try
	      {
	        throw new CommandException("You're standing in several regions (please pick one).\nYou're in: " + builder.toString());
	      }
	      catch (CommandException e)
	      {
	        e.printStackTrace();
	      }
	    }
	    return (ProtectedRegion)set.iterator().next();
	  }
	  public static ProtectedRegion checkRegionLocationIn(RegionManager regionManager, Location loc)
	  {
	    boolean allowGlobal = false;
	    ApplicableRegionSet set = regionManager.getApplicableRegions(new Vector(loc.getX(), loc.getY(), loc.getZ()));
	    if (set.size() == 0)
	    {
	      if (allowGlobal)
	      {
	        ProtectedRegion global = null;
	        global = checkExistingRegion(regionManager, "__global__", true);
	        
	        return global;
	      }
	      return null;
	    }
	    if (set.size() > 1)
	    {
	      StringBuilder builder = new StringBuilder();
	      boolean first = true;
	      for (ProtectedRegion region : set)
	      {
	        if (!first) {
	          builder.append(", ");
	        }
	        first = false;
	        builder.append(region.getId());
	      }
	      try
	      {
	        throw new CommandException("You're standing in several regions (please pick one).\nYou're in: " + builder.toString());
	      }
	      catch (CommandException e)
	      {
	        e.printStackTrace();
	      }
	    }
	    return (ProtectedRegion)set.iterator().next();
	  }
}
