package net.mmm.survival.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

/**
 * Build-Manager fuer Items
 */
public class ItemManager {

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param material Material
   * @param name Displayname
   * @param lore Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material material, final String name, final List<String> lore) {
    final ItemStack itemStack = new ItemStack(material);
    final ItemMeta itemMeta = itemStack.getItemMeta();

    itemMeta.setDisplayName(name);
    itemMeta.setLore(lore);
    itemStack.setItemMeta(itemMeta);

    return itemStack;
  }

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param material Material
   * @param damage Schadenswert
   * @param name Displayname
   * @param lore Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material material, final int damage, final String name, final List<String> lore) {
    final ItemStack item = new ItemStack(material);
    final ItemMeta meta = item.getItemMeta();

    ((Damageable) meta).setDamage(damage);
    meta.setDisplayName(name);
    meta.setLore(lore);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Build-Methode fuer Item mit folgenden Angaben
   *
   * @param material Material
   * @param enchantment Verzauberung
   * @param level Level der Verzauberung
   * @param name Displayname
   * @param lore Eigenschaften der Lore
   * @return Item
   */
  public static ItemStack build(final Material material, final Enchantment enchantment, final int level, final String name,
                                final List<String> lore) {
    final ItemStack item = build(material, name, lore);
    final ItemMeta meta = item.getItemMeta();

    meta.addEnchant(enchantment, level, true);
    item.setItemMeta(meta);

    return item;
  }

  /**
   * Build-Methode fuer Kopf mit folgenden Angaben
   *
   * @param playerName Name
   * @param lore Eigenschaften der Lore
   * @return Kopf
   */
  public static ItemStack getSkull(final String playerName, final List<String> lore) {
    final ItemStack playerSkull = new ItemStack(Material.PLAYER_HEAD);
    final SkullMeta skullMeta = (SkullMeta) playerSkull.getItemMeta();

    ((Damageable) skullMeta).setDamage(3);
    skullMeta.setOwningPlayer(UUIDUtils.getPlayer(playerName));
    playerSkull.setItemMeta(skullMeta);

    return playerSkull;
  }

}
