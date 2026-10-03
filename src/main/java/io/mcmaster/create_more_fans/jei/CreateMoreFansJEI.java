package io.mcmaster.create_more_fans.jei;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.AllItems;
import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.compat.jei.DoubleItemIcon;
import com.simibubi.create.compat.jei.EmptyBackground;
import com.simibubi.create.compat.jei.category.CreateRecipeCategory;

import io.mcmaster.create_more_fans.CreateMoreFans;
import io.mcmaster.create_more_fans.KubeFanProcessingRecipe;
import io.mcmaster.create_more_fans.kubejs.KubeFanProcessingType;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

@JeiPlugin
@ParametersAreNonnullByDefault
public class CreateMoreFansJEI implements IModPlugin {
    private static final ResourceLocation ID = new ResourceLocation(CreateMoreFans.MODID, "jei_plugin");
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
            ResourceLocation id = processingType.getId();

            List<Supplier<? extends ItemStack>> catalysts = new ArrayList<>();
            catalysts.add(processingType.getFan());

            // Mirrors the Create 1.21.1 CreateRecipeCategory.Builder, which is private to CreateJEI on 1.20.1
            CreateRecipeCategory.Info<KubeFanProcessingRecipe> info = new CreateRecipeCategory.Info<>(
                    new RecipeType<>(id, KubeFanProcessingRecipe.class),
                    Component.translatable(id.getNamespace() + ".recipe." + id.getPath()),
                    new EmptyBackground(178, 72),
                    new DoubleItemIcon(() -> new ItemStack(AllItems.PROPELLER.get()),
                            () -> new ItemStack(processingType.getJeiCategoryDisplayItem())),
                    () -> getRecipes(processingType),
                    catalysts);

            categories.add(KubeFanProcessingCategory.factory(processingType).create(info));
        });
    }

    private static List<KubeFanProcessingRecipe> getRecipes(KubeFanProcessingType processingType) {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null)
            return Collections.emptyList();
        return connection.getRecipeManager().getAllRecipesFor(processingType.getRecipeType());
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
}
