package com.vincentmet.rhodonite.worldgen;

import com.vincentmet.rhodonite.Objects;
import java.util.*;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.template.RuleTest;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class WorldGen {
    public static void registerFeature(Block block, RuleTest surroundingBlock, int veinSize, int maxY, int veinsPerChunk){
        OreFeatureConfig feature = new OreFeatureConfig(surroundingBlock, block.getDefaultState(), veinSize);
        Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, block.getRegistryName(), Feature.ORE.withConfiguration(feature).range(maxY).func_242731_b(veinsPerChunk).square());
    
    }
    
    public static void setupGen() {
        for(Map.Entry<RegistryKey<Biome>, Biome> biome : WorldGenRegistries.BIOME.getEntries()){
            if(!biome.getValue().getCategory().equals(Biome.Category.NETHER) && !biome.getValue().getCategory().equals(Biome.Category.THEEND)){ // Overworld
                addFeatureToBiome(biome.getValue(), GenerationStage.Decoration.UNDERGROUND_ORES, WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(Objects.Blocks.blockOreFluorite.getRegistryName()));
            }
            if(biome.getValue().getCategory().equals(Biome.Category.THEEND)){
                addFeatureToBiome(biome.getValue(), GenerationStage.Decoration.UNDERGROUND_ORES, WorldGenRegistries.CONFIGURED_FEATURE.getOrDefault(Objects.Blocks.blockOreRhodonite.getRegistryName()));
            }
        }
    }
    
    public static void addFeatureToBiome(Biome biome, GenerationStage.Decoration decoration, ConfiguredFeature<?, ?> configuredFeature){
        List<List<Supplier<ConfiguredFeature<?, ?>>>> biomeFeatures = new ArrayList<>(biome.getGenerationSettings().getFeatures());
    
        while (biomeFeatures.size() <= decoration.ordinal()) {
            biomeFeatures.add(new ArrayList<>());
        }
        List<Supplier<ConfiguredFeature<?, ?>>> features = new ArrayList<>(biomeFeatures.get(decoration.ordinal()));
        features.add(() -> configuredFeature);
        biomeFeatures.set(decoration.ordinal(), features);
        ObfuscationReflectionHelper.setPrivateValue(BiomeGenerationSettings.class, biome.getGenerationSettings(), biomeFeatures, "field_242484_f");
    }
}