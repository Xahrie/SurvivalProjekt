package net.mmm.survival.worldedit;

import java.util.Iterator;

import com.sk89q.worldedit.BlockVector;
import org.bukkit.World;
import org.bukkit.block.Block;

/**
 * Der CuboitIterator berechnet Regionen fuer WorldGuard mithilfe von WorldEdit
 */
public class CuboidIterator implements Iterator<Block> {
  private final BlockVector maximumPoint, minimumPoint;
  private final World world;
  private int x, y, z;

  /**
   * Konstruktor
   *
   * @param world Minecraft-Welt
   * @param minimumPoint Punkt 1
   * @param maximumPoint Punkt 2
   */
  public CuboidIterator(final World world, final BlockVector minimumPoint, final BlockVector maximumPoint) {
    this.minimumPoint = minimumPoint;
    this.maximumPoint = maximumPoint;
    this.world = world;
  }

  private int getSizeX() {
    return Math.abs(maximumPoint.getBlockX() - minimumPoint.getBlockX()) + 1;
  }

  private int getSizeY() {
    return Math.abs(maximumPoint.getBlockY() - minimumPoint.getBlockY()) + 1;
  }

  private int getSizeZ() {
    return Math.abs(maximumPoint.getBlockZ() - minimumPoint.getBlockZ()) + 1;
  }

  @Override
  public boolean hasNext() {
    return this.x < getSizeX() && this.y < getSizeY() && this.z < getSizeZ();
  }

  @Override
  public Block next() {
    final Block blockAt = world.getBlockAt(minimumPoint.getBlockX() + x,
        minimumPoint.getBlockY() + y, minimumPoint.getBlockZ() + z);
    if (++x >= getSizeX()) {
      this.x = 0;
      if (++this.y >= getSizeY()) {
        this.y = 0;
        this.z++;
      }
    }
    return blockAt;
  }
}
