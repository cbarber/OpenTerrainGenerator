package com.pg85.otg.bukkit.generator.structures;

import com.google.common.base.Preconditions;
import com.pg85.otg.LocalWorld;
import com.pg85.otg.bukkit.BukkitWorld;
import com.pg85.otg.generator.SpawnableObject;
import com.pg85.otg.util.Rotation;
import net.minecraft.server.v1_13_R1.*;

import java.util.Random;

/**
 * Makes a Minecraft {@link DefinedStructure} useable as a
 * {@link SpawnableObject}.
 *
 */
public final class MojangStructurePart implements SpawnableObject
{
    private final DefinedStructure spawnObject;
    private final String name;

    public MojangStructurePart(String name, DefinedStructure spawnObject)
    {
        Preconditions.checkNotNull(name, "name");
        Preconditions.checkNotNull(spawnObject, "spawnObject");
        this.name = name;
        this.spawnObject = spawnObject;
    }

    @Override
    public boolean spawnForced(LocalWorld world, Random random, Rotation rotation, int x, int y, int z)
    {
        World worldMc = ((BukkitWorld) world).getWorld();
        BlockPosition blockPos = new BlockPosition(x, y, z);
        EnumBlockRotation rotationMc = toMinecraftRotation(rotation);

        DefinedStructureInfo spawnSettings = new DefinedStructureInfo().a(rotationMc) // withRotation
                .a(new ChunkCoordIntPair(blockPos)) // inChunk
                .a(random); // withRandom
        spawnObject.a(worldMc, blockPos, spawnSettings, 4);
        return true;
    }

    private EnumBlockRotation toMinecraftRotation(Rotation rotation)
    {
        switch (rotation)
        {
            case EAST:
                return EnumBlockRotation.CLOCKWISE_90;
            case NORTH:
                return EnumBlockRotation.NONE;
            case SOUTH:
                return EnumBlockRotation.CLOCKWISE_180;
            case WEST:
                return EnumBlockRotation.COUNTERCLOCKWISE_90;
            default:
                throw new IllegalArgumentException("Unknown rotation: " + rotation);
        }
    }

    @Override
    public String getName()
    {
        return name;
    }

}
