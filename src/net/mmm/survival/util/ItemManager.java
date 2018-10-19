package net.mmm.survival.util;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Build-Manager fuer Items
 */
public final class ItemManager {

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

}