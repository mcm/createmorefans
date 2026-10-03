package io.mcmaster.create_more_fans;

import javax.annotation.ParametersAreNonnullByDefault;

import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeBuilder.ProcessingRecipeParams;
import com.simibubi.create.foundation.recipe.IRecipeTypeInfo;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class KubeFanProcessingRecipe extends ProcessingRecipe<RecipeWrapper> {
    public KubeFanProcessingRecipe(IRecipeTypeInfo recipeTypeInfo, ProcessingRecipeParams params) {
        super(recipeTypeInfo, params);
    }

    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        if (inv.isEmpty())
            return false;
        return getIngredients().get(0).test(inv.getItem(0));
    }

    @Override
    protected int getMaxInputCount() {
        return 1;
    }

    @Override
    protected int getMaxOutputCount() {
        return 12;
    }

    public static RecipeWrapper wrap(ItemStack stack) {
        RecipeWrapper wrapper = new RecipeWrapper(new ItemStackHandler(1));
        wrapper.setItem(0, stack);
        return wrapper;
    }
}
