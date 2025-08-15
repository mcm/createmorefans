package io.mcmaster.create_more_fans.jei;

import javax.annotation.Nonnull;

import com.simibubi.create.compat.jei.category.ProcessingViaFanCategory;
import com.simibubi.create.foundation.gui.AllGuiTextures;

import io.mcmaster.create_more_fans.KubeFanProcessingRecipe;
import io.mcmaster.create_more_fans.kubejs.KubeFanProcessingType;
import net.minecraft.client.gui.GuiGraphics;

public class KubeFanProcessingCategory extends ProcessingViaFanCategory.MultiOutput<KubeFanProcessingRecipe> {
    private KubeFanProcessingType processingType;

    public KubeFanProcessingCategory(Info<KubeFanProcessingRecipe> info, KubeFanProcessingType processingType) {
        super(info);
        this.processingType = processingType;
    }

    public static Factory<KubeFanProcessingRecipe> factory(KubeFanProcessingType processingType) {
        return (info) -> new KubeFanProcessingCategory(info, processingType);
    }

    @Override
    protected AllGuiTextures getBlockShadow() {
        return processingType.getJeiBlockShadow() ? AllGuiTextures.JEI_SHADOW : AllGuiTextures.JEI_LIGHT;
    }

    @Override
    protected void renderAttachedBlock(@Nonnull GuiGraphics graphics) {
        processingType.getJeiRenderAttachedBlockCallback().accept(graphics);
    }
}
