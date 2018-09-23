package net.mmm.survival.worldedit;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Der CuboitIterator berechnet Regionen fuer WorldGuard mithilfe von WorldEdit
 */
public class CuboidIterator implements Iterator<Block> {
  private final int baseX, baseY, baseZ, sizeX, sizeY, sizeZ;
  private final World w;
  private int x, y, z;

  /**
   * Konstruktor
   *
   * @param w Minecraft-Welt
   * @param x1 X-Start
   * @param y1 Y-Start
   * @param z1 Z-Start
   * @param x2 X-Ende
   * @param y2 Y-Ende
   * @param z2 Z-Ende
   */
  public CuboidIterator(final World w, final int x1, final int y1, final int z1, final int x2, final int y2, final int z2) {
    this.w = w;
    this.baseX = x1;
    this.baseY = y1;
    this.baseZ = z1;
    this.sizeX = Math.abs(x2 - x1) + 1;
    this.sizeY = Math.abs(y2 - y1) + 1;
    this.sizeZ = Math.abs(z2 - z1) + 1;
    this.x = this.y = this.z = 0;
  }

  @Override
  public boolean hasNext() {
    return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
  }

  @Override
  public Block next() {
    final Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
    if (++x >= this.sizeX) {
      this.x = 0;
      if (++this.y >= this.sizeY) {
        this.y = 0;
        ++this.z;
      }
    }
    return b;
  }

}
