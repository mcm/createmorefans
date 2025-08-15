package io.mcmaster.create_more_fans.jei;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import io.mcmaster.create_more_fans.CreateMoreFans;
import io.mcmaster.create_more_fans.KubeFanProcessingRecipe;
import io.mcmaster.create_more_fans.kubejs.KubeFanProcessingType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
@ParametersAreNonnullByDefault
public class CreateMoreFansJEI implements IModPlugin {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(CreateMoreFans.MODID,
            "jei_plugin");
    private final List<CreateRecipeCategory<?>> categories = new ArrayList<>();

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    private void loadCategories() {
        categories.clear();

        CreateBuiltInRegistries.FAN_PROCESSING_TYPE.entrySet().forEach((entry) -> {
            if (!(entry.getValue() instanceof KubeFanProcessingType))
                return;

            KubeFanProcessingType processingType = (KubeFanProcessingType) entry.getValue();

            builder().addTypedRecipes(processingType.getTypeInfo()).catalystStack(processingType.getFan())
                    .doubleItemIcon(AllItems.PROPELLER.get(), processingType.getJeiCategoryDisplayItem())
                    .emptyBackground(178, 72)
                    .build(processingType.getId(), KubeFanProcessingCategory.factory(processingType));
        });
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        loadCategories();
        registration.addRecipeCategories(categories.toArray(IRecipeCategory[]::new));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        categories.forEach(category -> category.registerRecipes(registration));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        categories.forEach(category -> category.registerCatalysts(registration));
    }

    private CategoryBuilder builder() {
        return new CategoryBuilder();
    }

    private class CategoryBuilder extends CreateRecipeCategory.Builder<KubeFanProcessingRecipe> {
        public CategoryBuilder() {
            super(KubeFanProcessingRecipe.class);
        }

        @Override
        public CreateRecipeCategory<KubeFanProcessingRecipe> build(ResourceLocation id,
                CreateRecipeCategory.Factory<KubeFanProcessingRecipe> factory) {
            CreateRecipeCategory<KubeFanProcessingRecipe> category = super.build(id, factory);
            categories.add(category);
            return category;
        }
    }
}
