package de.PAS123.Survival.util;

import java.util.Iterator;

import org.bukkit.World;
import org.bukkit.block.Block;

public class CuboidIterator implements Iterator<Block> {
	
	
	private World w;
	private int baseX, baseY, baseZ;
	private int x, y, z;
	private int sizeX, sizeY, sizeZ;

	public CuboidIterator(World w, int x1, int y1, int z1, int x2, int y2, int z2) {
		this.w = w;
		this.baseX = x1;
		this.baseY = y1;
		this.baseZ = z1;
		this.sizeX = Math.abs(x2 - x1) + 1;
		this.sizeY = Math.abs(y2 - y1) + 1;
		this.sizeZ = Math.abs(z2 - z1) + 1;
		this.x = this.y = this.z = 0;
	}

	public boolean hasNext() {
		return this.x < this.sizeX && this.y < this.sizeY && this.z < this.sizeZ;
	}

	public Block next() {
		Block b = this.w.getBlockAt(this.baseX + this.x, this.baseY + this.y, this.baseZ + this.z);
		if (++x >= this.sizeX) {
			this.x = 0;
			if (++this.y >= this.sizeY) {
				this.y = 0;
				++this.z;
			}
		}
		return b;
	}

	public void remove() {}
	
}
