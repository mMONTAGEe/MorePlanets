package stevekung.mods.moreplanets.planets.nibiru.world.gen.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import micdoodle8.mods.galacticraft.core.entities.EntityEvolvedWitch;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureStart;
import stevekung.mods.moreplanets.init.MPBiomes;
import stevekung.mods.moreplanets.utils.LoggerMP;

public class MapGenNibiruJungleTemple extends MapGenStructure
{
    private final List<Biome.SpawnListEntry> scatteredFeatureSpawnList;
    private final int maxDistanceBetweenScatteredFeatures;
    private final int minDistanceBetweenScatteredFeatures;

    static
    {
        MapGenStructureIO.registerStructure(Start.class, "NibiruJungleTemple");
        MapGenStructureIO.registerStructureComponent(StructureNibiruJungleTemplePieces.JungleTemple.class, "NibiruJungleTemple");
    }

    public MapGenNibiruJungleTemple()
    {
        this.scatteredFeatureSpawnList = new ArrayList<>();
        this.maxDistanceBetweenScatteredFeatures = 32;
        this.minDistanceBetweenScatteredFeatures = 8;
        this.scatteredFeatureSpawnList.add(new Biome.SpawnListEntry(EntityEvolvedWitch.class, 1, 1, 1));
    }

    @Override
    public String getStructureName()
    {
        return "NibiruJungleTemple";
    }

    @Override
    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int i = chunkX;
        int j = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.maxDistanceBetweenScatteredFeatures - 1;
        }
        if (chunkZ < 0)
        {
            chunkZ -= this.maxDistanceBetweenScatteredFeatures - 1;
        }

        int k = chunkX / this.maxDistanceBetweenScatteredFeatures;
        int l = chunkZ / this.maxDistanceBetweenScatteredFeatures;
        Random random = this.world.setRandomSeed(k, l, 14357617);
        k = k * this.maxDistanceBetweenScatteredFeatures;
        l = l * this.maxDistanceBetweenScatteredFeatures;
        k = k + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);
        l = l + random.nextInt(this.maxDistanceBetweenScatteredFeatures - this.minDistanceBetweenScatteredFeatures);

        if (i == k && j == l)
        {
            Biome biome = this.world.getBiomeProvider().getBiome(new BlockPos(i * 16 + 8, 0, j * 16 + 8));

            if (biome == null)
            {
                return false;
            }
            if (biome == MPBiomes.INFECTED_JUNGLE)
            {
                return true;
            }
        }
        return false;
    }

    @Override
    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new Start(this.world, this.rand, chunkX, chunkZ);
    }

    @Override
    public BlockPos getNearestStructurePos(World world, BlockPos pos, boolean findUnexplored)
    {
        this.world = world;
        return MapGenStructure.findNearestStructurePosBySpacing(world, this, pos, this.maxDistanceBetweenScatteredFeatures, 8, 14357617, false, 100, findUnexplored);
    }

    public boolean canMobSpawn(BlockPos pos)
    {
        StructureStart structurestart = this.getStructureAt(pos);

        if (structurestart != null && structurestart instanceof Start && !structurestart.getComponents().isEmpty())
        {
            StructureComponent structurecomponent = structurestart.getComponents().get(0);
            return structurecomponent instanceof StructureNibiruJungleTemplePieces.JungleTemple;
        }
        else
        {
            return false;
        }
    }

    public List<Biome.SpawnListEntry> getSpawnList()
    {
        return this.scatteredFeatureSpawnList;
    }

    public static class Start extends StructureStart
    {
        public Start() {}

        public Start(World world, Random rand, int chunkX, int chunkZ)
        {
            super(chunkX, chunkZ);
            LoggerMP.debug("Generate jungle temple at {} {}", chunkX * 16, chunkZ * 16);
            StructureNibiruJungleTemplePieces.JungleTemple component = new StructureNibiruJungleTemplePieces.JungleTemple(rand, chunkX * 16, chunkZ * 16);
            this.components.add(component);
            this.updateBoundingBox();
        }
    }
}