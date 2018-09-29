package net.mmm.survival.worldedit;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Der CuboitIterator berechnet Regionen fuer WorldGuard mithilfe von WorldEdit
 */
public class CuboidIterator implements Iterator<Block> {
  private final int baseX, baseY, baseZ, sizeX, sizeY, sizeZ;
  private final World world;
  private int x, y, z;

  /**
   * Konstruktor
   *
   * @param world Minecraft-Welt
   * @param xStart X-Start
   * @param yStart Y-Start
   * @param zStart Z-Start
   * @param xEnd X-Ende
   * @param yEnd Y-Ende
   * @param zEnd Z-Ende
   */
  public CuboidIterator(final World world, final int xStart, final int yStart, final int zStart, final int xEnd, final int yEnd,
                        final int zEnd) {
    this.world = world;
    this.baseX = xStart;
    this.baseY = yStart;
    this.baseZ = zStart;
    this.sizeX = Math.abs(xEnd - xStart) + 1;
    this.sizeY = Math.abs(yEnd - yStart) + 1;
    this.sizeZ = Math.abs(zEnd - zStart) + 1;
    this.x = 0;
    this.y = 0;
    this.z = 0;
  }

  @Override
  public boolean hasNext() {
    return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
  }

  @Override
  public Block next() {
    final Block blockAt = this.world
        .getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
    if (++x >= this.sizeX) {
      this.x = 0;
      if (++this.y >= this.sizeY) {
        this.y = 0;
        this.z++;
      }
    }
    return blockAt;
  }

}
