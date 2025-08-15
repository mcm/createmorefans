package io.mcmaster.create_more_fans.kubejs;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public class KubeFanProcessingType implements FanProcessingType {
    KubeFanProcessingTypeBuilder builder;

    public KubeFanProcessingType(KubeFanProcessingTypeBuilder builder) {
        this.builder = builder;
    }

    @Override
    public boolean isValidAt(Level level, BlockPos pos) {
        TagKey<Fluid> fluidCatalystTag = builder.getCatalystFluidTag();
        if (fluidCatalystTag != null && level.getFluidState(pos).is(builder.getCatalystFluidTag())) {
            return true;
        }
        TagKey<Block> blockCatalystTag = builder.getCatalystBlockTag();
        if (blockCatalystTag != null && level.getBlockState(pos).is(builder.getCatalystBlockTag())) {
            return true;
        }
        return false;
    }

    public ResourceLocation getId() {
        return builder.id;
    }

    @Override
    public int getPriority() {
        return builder.getPriority();
    }

    public IRecipeTypeInfo getTypeInfo() {
        return builder.getTypeInfo();
    }

    @Override
    public boolean canProcess(ItemStack stack, Level level) {
        RecipeManager recipeManager = level.getRecipeManager();
        SingleRecipeInput input = new SingleRecipeInput(stack);
        boolean result = recipeManager.getRecipeFor(builder.getRecipeType(), input, level).isPresent();
        return result;
    }

    @Override
    public @Nullable List<ItemStack> process(ItemStack stack, Level level) {
        RecipeManager recipeManager = level.getRecipeManager();
        SingleRecipeInput input = new SingleRecipeInput(stack);
        return recipeManager.getRecipeFor(builder.getRecipeType(), input, level)
                .map(recipe -> RecipeApplier.applyRecipeOn(level, stack, recipe)).orElse(null);
    }

    @Nullable
    private <T> boolean safeCallback(Consumer<T> consumer, T value, String errorMessage) {
        try {
            consumer.accept(value);
        } catch (Throwable e) {
            ScriptType.STARTUP.console.error(errorMessage, e);
            return false;
        }

        return true;
    }

    @Override
    public void spawnProcessingParticles(Level level, Vec3 pos) {
        Consumer<KubeFanProcessingTypeBuilder.SpawnProcessingParticlesCallback> cb = builder
                .getSpawnProcessingParticlesCallback();
        if (cb != null) {
            safeCallback(cb, new KubeFanProcessingTypeBuilder.SpawnProcessingParticlesCallback(level, pos),
                    "Error while spawning processing particles ");
        }
    }

    @Override
    public void morphAirFlow(AirFlowParticleAccess particleAccess, RandomSource random) {
        Consumer<KubeFanProcessingTypeBuilder.MorphAirFlowCallback> cb = builder.getMorphAirFlowCallback();
        if (cb != null) {
            safeCallback(cb, new KubeFanProcessingTypeBuilder.MorphAirFlowCallback(particleAccess, random),
                    "Error while trying to morph air flow ");
        }
    }

    @Override
    public void affectEntity(Entity entity, Level level) {
        Consumer<KubeFanProcessingTypeBuilder.AffectEntityCallback> cb = builder.getAffectEntityCallback();
        if (cb != null) {
            safeCallback(cb, new KubeFanProcessingTypeBuilder.AffectEntityCallback(entity, level),
                    "Error while affecting entity ");
        }
    }

    public Supplier<ItemStack> getFan() {
        return () -> builder.getRecipeCatalystItem();
    }

    public Item getJeiCategoryDisplayItem() {
        return builder.getJeiCategoryDisplayItem();
    }

    public Consumer<GuiGraphics> getJeiRenderAttachedBlockCallback() {
        return builder.getJeiRenderAttachedBlockCallback();
    }

    public boolean getJeiBlockShadow() {
        return builder.getJeiBlockShadow();
    }
}
