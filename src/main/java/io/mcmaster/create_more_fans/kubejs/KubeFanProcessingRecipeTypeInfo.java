package io.mcmaster.create_more_fans.kubejs;

import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import io.mcmaster.create_more_fans.KubeFanProcessingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class KubeFanProcessingRecipeTypeInfo implements IRecipeTypeInfo {
    private KubeFanProcessingTypeBuilder builder;

    public KubeFanProcessingRecipeTypeInfo(KubeFanProcessingTypeBuilder builder) {
        this.builder = builder;
    }

    @Override
    public ResourceLocation getId() {
        return builder.id;
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecipeSerializer<KubeFanProcessingRecipe> getSerializer() {
        return (RecipeSerializer<KubeFanProcessingRecipe>) builder.getSerializer();
    }

    @SuppressWarnings("unchecked")
    @Override
    public RecipeType<KubeFanProcessingRecipe> getType() {
        return (RecipeType<KubeFanProcessingRecipe>) builder.getRecipeType();
    }
}
