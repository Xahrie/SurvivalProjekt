package de.pas123.survival.util;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;

public class ItemManager {
	
	public static ItemStack build(Material ma, String name, List<String> lore) {
		
		ItemStack item = new ItemStack(ma);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack build(Material ma, int damage, String name, List<String> lore) {
		
		ItemStack item = new ItemStack(ma, 1, (short) damage);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		
		return item;
	}
	public static ItemStack build(Material ma, Enchantment ench, int level, String name, List<String> lore) {
		
		ItemStack item = new ItemStack(ma, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.addEnchant(ench, level, true);
		item.setItemMeta(meta);
		
		return item;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack getSkull(GameProfile profile, String name, List<String> lore) {
		 ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
		 SkullMeta skullMeta = (SkullMeta)skull.getItemMeta();
		 skullMeta.setDisplayName(name);
		 skullMeta.setLore(lore);
		 Field profileField = null;
		 try {
			 profileField = skullMeta.getClass().getDeclaredField("profile");
		 } catch (NoSuchFieldException|SecurityException e) {}
		 assert (profileField != null);
		 	profileField.setAccessible(true);
		 try {
			 profileField.set(skullMeta, profile);
		 } catch (IllegalArgumentException|IllegalAccessException e) {
			 e.printStackTrace();
		 }
		 skull.setItemMeta(skullMeta);
		 return skull;
	}
	
}
