package io.mcmaster.create_more_fans.kubejs;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.jetbrains.annotations.Nullable;

import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;
import com.simibubi.create.foundation.recipe.RecipeApplier;

import dev.latvian.mods.kubejs.script.ScriptType;
import io.mcmaster.create_more_fans.CreateMoreFans;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;

public class KubeFanProcessingType implements FanProcessingType {
    private static final MethodHandle APPLY_RECIPE_ON;

    static {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle handle;
        try {
            // Create 6.0.7+ (4-arg with boolean returnProcessingRemainder)
            handle = lookup.findStatic(RecipeApplier.class, "applyRecipeOn",
                    MethodType.methodType(List.class, Level.class, ItemStack.class, Recipe.class, boolean.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            try {
                // Create 6.0.6 (3-arg)
                handle = lookup.findStatic(RecipeApplier.class, "applyRecipeOn",
                        MethodType.methodType(List.class, Level.class, ItemStack.class, Recipe.class));
            } catch (NoSuchMethodException | IllegalAccessException e2) {
                throw new RuntimeException("Could not find RecipeApplier.applyRecipeOn", e2);
            }
        }
        APPLY_RECIPE_ON = handle;
        CreateMoreFans.LOGGER.debug("RecipeApplier.applyRecipeOn resolved with {} parameters",
                handle.type().parameterCount());
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> invokeApplyRecipeOn(Level level, ItemStack stack, Recipe<?> recipe) {
        try {
            if (APPLY_RECIPE_ON.type().parameterCount() == 4) {
                return (List<ItemStack>) APPLY_RECIPE_ON.invoke(level, stack, recipe, false);
            } else {
                return (List<ItemStack>) APPLY_RECIPE_ON.invoke(level, stack, recipe);
            }
        } catch (Throwable e) {
            throw new RuntimeException("Failed to invoke RecipeApplier.applyRecipeOn", e);
        }
    }

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
                .map(recipe -> invokeApplyRecipeOn(level, stack, recipe.value())).orElse(null);
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
