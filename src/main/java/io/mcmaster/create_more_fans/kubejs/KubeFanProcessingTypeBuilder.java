package io.mcmaster.create_more_fans.kubejs;

import java.util.function.Consumer;
import java.util.function.Supplier;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType;
import com.simibubi.create.content.kinetics.fan.processing.FanProcessingType.AirFlowParticleAccess;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;

import dev.latvian.mods.kubejs.registry.BuilderBase;
import io.mcmaster.create_more_fans.KubeFanProcessingRecipe;
import net.createmod.catnip.gui.element.GuiGameElement;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.Lazy;

public class KubeFanProcessingTypeBuilder extends BuilderBase<FanProcessingType> {
    private TagKey<Block> catalystBlockTag = null;
    private TagKey<Fluid> catalystFluidTag = null;
    private int priority = 100;
    private Consumer<SpawnProcessingParticlesCallback> spawnProcessingParticlesCallback;
    private Consumer<MorphAirFlowCallback> morphAirFlowCallback;
    private Consumer<AffectEntityCallback> affectEntityCallback;
    private Lazy<KubeFanProcessingRecipeTypeInfo> typeInfo;
    private Lazy<StandardProcessingRecipe.Serializer<KubeFanProcessingRecipe>> serializer;
    private Lazy<RecipeType<KubeFanProcessingRecipe>> recipeType;
    private ResourceLocation recipeCatalystItem;
    private ResourceLocation jeiCategoryDisplayItem;
    private Supplier<GuiGameElement.GuiRenderBuilder> jeiRenderAttachedBlockCallback;
    private boolean jeiBlockShadow = true;

    public KubeFanProcessingTypeBuilder(ResourceLocation id) {
        super(id);
        typeInfo = Lazy.of(() -> new KubeFanProcessingRecipeTypeInfo(this));
        serializer = Lazy.of(() -> new StandardProcessingRecipe.Serializer<>(
                (params) -> new KubeFanProcessingRecipe(typeInfo.get(), params)));
        recipeType = Lazy.of(() -> RecipeType.simple(id));
    }

    public static class SpawnProcessingParticlesCallback {
        protected final Level level;
        protected final Vec3 pos;

        public SpawnProcessingParticlesCallback(Level level, Vec3 pos) {
            this.level = level;
            this.pos = pos;
        }

        public Level getLevel() {
            return level;
        }

        public Vec3 getPos() {
            return pos;
        }
    }

    public static class MorphAirFlowCallback {
        protected final AirFlowParticleAccess particleAccess;
        protected final RandomSource random;

        public MorphAirFlowCallback(AirFlowParticleAccess particleAccess, RandomSource random) {
            this.particleAccess = particleAccess;
            this.random = random;
        }

        public AirFlowParticleAccess getParticleAccess() {
            return particleAccess;
        }

        public RandomSource getRandom() {
            return random;
        }
    }

    public static class AffectEntityCallback {
        protected final Entity entity;
        protected final Level level;

        public AffectEntityCallback(Entity entity, Level level) {
            this.entity = entity;
            this.level = level;
        }

        public Entity getEntity() {
            return entity;
        }

        public Level getLevel() {
            return level;
        }
    }

    public TagKey<Block> getCatalystBlockTag() {
        return catalystBlockTag;
    }

    public KubeFanProcessingTypeBuilder setCatalystBlockTag(TagKey<Block> tag) {
        catalystBlockTag = tag;
        return this;
    }

    public TagKey<Fluid> getCatalystFluidTag() {
        return catalystFluidTag;
    }

    public KubeFanProcessingTypeBuilder setCatalystFluidTag(TagKey<Fluid> tag) {
        catalystFluidTag = tag;
        return this;
    }

    public int getPriority() {
        return priority;
    }

    public KubeFanProcessingTypeBuilder setPriority(int prio) {
        priority = prio;
        return this;
    }

    public Consumer<SpawnProcessingParticlesCallback> getSpawnProcessingParticlesCallback() {
        return spawnProcessingParticlesCallback;
    }

    public KubeFanProcessingTypeBuilder setSpawnProcessingParticlesCallback(
            Consumer<SpawnProcessingParticlesCallback> cb) {
        spawnProcessingParticlesCallback = cb;
        return this;
    }

    public KubeFanProcessingTypeBuilder setProcessingParticles(Supplier<ParticleOptions> particleData) {
        spawnProcessingParticlesCallback = (cb) -> {
            if (cb.level.random.nextInt(8) == 0) {
                cb.level.addParticle(particleData.get(), cb.pos.x + (cb.level.random.nextFloat() - .5f) * .5f,
                        cb.pos.y + .5f, cb.pos.z + (cb.level.random.nextFloat() - .5f) * .5f, 0, 1 / 8f, 0);
            }
        };
        return this;
    }

    public Consumer<MorphAirFlowCallback> getMorphAirFlowCallback() {
        return morphAirFlowCallback;
    }

    public KubeFanProcessingTypeBuilder setMorphAirFlowCallback(Consumer<MorphAirFlowCallback> cb) {
        morphAirFlowCallback = cb;
        return this;
    }

    public Consumer<AffectEntityCallback> getAffectEntityCallback() {
        return affectEntityCallback;
    }

    public KubeFanProcessingTypeBuilder setAffectEntityCallback(Consumer<AffectEntityCallback> cb) {
        affectEntityCallback = cb;
        return this;
    }

    public ItemStack getRecipeCatalystItem() {
        ItemStack stack = recipeCatalystItem == null ? AllBlocks.ENCASED_FAN.asStack()
                : BuiltInRegistries.ITEM.get(recipeCatalystItem).getDefaultInstance();

        if (displayName != null) {
            stack.set(DataComponents.CUSTOM_NAME, displayName.copy().withStyle(style -> style.withItalic(false)));
        } else {
            stack.set(DataComponents.CUSTOM_NAME,
                    Component.translatable(id.getNamespace() + "." + "fan_" + id.getPath())
                            .withStyle(style -> style.withItalic(false)));
        }

        return stack;
    }

    public KubeFanProcessingTypeBuilder setRecipeCatalystItem(ResourceLocation item) {
        recipeCatalystItem = item;
        return this;
    }

    public Item getJeiCategoryDisplayItem() {
        return jeiCategoryDisplayItem == null ? Items.AIR : BuiltInRegistries.ITEM.get(jeiCategoryDisplayItem);
    }

    public KubeFanProcessingTypeBuilder setJeiCategoryDisplayItem(ResourceLocation item) {
        jeiCategoryDisplayItem = item;
        return this;
    }

    public Consumer<GuiGraphics> getJeiRenderAttachedBlockCallback() {
        Supplier<GuiGameElement.GuiRenderBuilder> cb;
        if (jeiRenderAttachedBlockCallback == null) {
            cb = () -> GuiGameElement.of(Blocks.AIR.defaultBlockState());
        } else {
            cb = jeiRenderAttachedBlockCallback;
        }
        return (graphics) -> cb.get().scale(24).atLocal(0, 0, 2).lighting(AnimatedKinetics.DEFAULT_LIGHTING)
                .render(graphics);
    }

    public KubeFanProcessingTypeBuilder setJeiRenderAttachedBlockCallback(
            Supplier<GuiGameElement.GuiRenderBuilder> cb) {
        jeiRenderAttachedBlockCallback = cb;
        return this;
    }

    public KubeFanProcessingTypeBuilder setJeiAttachedBlock(ResourceLocation id) {
        return setJeiRenderAttachedBlockCallback(() -> {
            Block block = BuiltInRegistries.BLOCK.get(id);
            return GuiGameElement.of(block.defaultBlockState());
        });
    }

    public KubeFanProcessingTypeBuilder setJeiAttachedFluid(ResourceLocation id) {
        return setJeiRenderAttachedBlockCallback(() -> {
            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            return GuiGameElement.of(fluid);
        });
    }

    public boolean getJeiBlockShadow() {
        return jeiBlockShadow;
    }

    public KubeFanProcessingTypeBuilder setJeiBlockShadow(boolean shadow) {
        jeiBlockShadow = shadow;
        return this;
    }

    @Override
    public KubeFanProcessingType createObject() {
        return new KubeFanProcessingType(this);
    }

    public RecipeTypeBuilder getRecipeTypeBuilder() {
        return new RecipeTypeBuilder(this);
    }

    public static class RecipeTypeBuilder extends BuilderBase<RecipeType<?>> {
        KubeFanProcessingTypeBuilder builder;

        public RecipeTypeBuilder(KubeFanProcessingTypeBuilder builder) {
            super(builder.id);
            this.builder = builder;
            this.registryKey = Registries.RECIPE_TYPE;
        }

        @Override
        public RecipeType<KubeFanProcessingRecipe> createObject() {
            return builder.recipeType.get();
        }
    }

    public SerializerBuilder getSerializerBuilder() {
        return new SerializerBuilder(this);
    }

    public static class SerializerBuilder extends BuilderBase<RecipeSerializer<?>> {
        KubeFanProcessingTypeBuilder builder;

        public SerializerBuilder(KubeFanProcessingTypeBuilder builder) {
            super(builder.id);
            this.builder = builder;
            this.registryKey = Registries.RECIPE_SERIALIZER;
        }

        @Override
        public RecipeSerializer<KubeFanProcessingRecipe> createObject() {
            return builder.serializer.get();
        }
    }

    public StandardProcessingRecipe.Serializer<KubeFanProcessingRecipe> getSerializer() {
        return serializer.get();
    }

    public RecipeType<KubeFanProcessingRecipe> getRecipeType() {
        return recipeType.get();
    }

    public KubeFanProcessingRecipeTypeInfo getTypeInfo() {
        return typeInfo.get();
    }
}
