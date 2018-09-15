package de.mmm.survival.util;

import com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Build-Manager fuer Items
 */
public class ItemManager {

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param ma   Material
   * @param name Displayname
   * @param lore Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material ma, final String name, final List<String> lore) {

    final ItemStack item = new ItemStack(ma);
    final ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param ma     Material
   * @param damage Schadenswert
   * @param name   Displayname
   * @param lore   Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material ma, final int damage, final String name, final List<String> lore) {

    //noinspection deprecation
    final ItemStack item = new ItemStack(ma, 1, (short) damage);
    final ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param ma    Material
   * @param ench  Verzauberung
   * @param level Level der Verzauberung
   * @param name  Displayname
   * @param lore  Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material ma, final Enchantment ench, final int level, final String name, final
  List<String> lore) {

    final ItemStack item = new ItemStack(ma, 1);
    final ItemMeta meta = item.getItemMeta();
    meta.setDisplayName(name);
    meta.setLore(lore);
    meta.addEnchant(ench, level, true);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Build-Methode fuer Kopf mit folgenden Angaben
   *
   * @param profile Spielprofil
   * @param name    Name
   * @param lore    Eigenschaften der Lore
   * @return Kopf
   */
  @SuppressWarnings("deprecation")
  public static ItemStack getSkull(final GameProfile profile, final String name, final List<String> lore) {
    final ItemStack skull = new ItemStack(Material.LEGACY_SKULL_ITEM, 1, (short) 3);
    final SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
    skullMeta.setDisplayName(name);
    skullMeta.setLore(lore);
    Field profileField = null;
    try {
      profileField = skullMeta.getClass().getDeclaredField("profile");
    } catch (final NoSuchFieldException | SecurityException ignored) {
    }
    assert (profileField != null);
    profileField.setAccessible(true);
    try {
      profileField.set(skullMeta, profile);
    } catch (final IllegalArgumentException | IllegalAccessException e) {
      e.printStackTrace();
    }
    skull.setItemMeta(skullMeta);
    return skull;
  }

}
