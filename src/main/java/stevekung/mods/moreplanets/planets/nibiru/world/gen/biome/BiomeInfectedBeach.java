package stevekung.mods.moreplanets.planets.nibiru.world.gen.biome;

import stevekung.mods.moreplanets.init.MPBlocks;

public class BiomeInfectedBeach extends BiomeNibiru
{
    public BiomeInfectedBeach(BiomeProperties prop, boolean isStone)
    {
        super(prop);
        this.topBlock = isStone ? MPBlocks.NIBIRU_ROCK.getDefaultState() : MPBlocks.INFECTED_SAND.getDefaultState();
        this.fillerBlock = isStone ? MPBlocks.NIBIRU_ROCK.getDefaultState() : MPBlocks.INFECTED_SAND.getDefaultState();
        this.decorator.treesPerChunk = -999;
        this.decorator.deadBushPerChunk = 0;
        this.decorator.reedsPerChunk = 0;
        this.decorator.cactiPerChunk = 0;
    }
}