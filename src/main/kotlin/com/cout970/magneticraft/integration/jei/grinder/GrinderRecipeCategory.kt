package com.cout970.magneticraft.integration.jei.crushingtable

import com.cout970.magneticraft.integration.jei.JEIPlugin
import com.cout970.magneticraft.util.resource
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.gui.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.client.Minecraft

/**
 * Created by cout970 on 23/07/2016.
 */
object GrinderRecipeCategory : IRecipeCategory<GrinderRecipeWrapper> {

    private val title = Translator.translateToLocal("text.magneticraft.jei.crushing_table")
    private val background = DrawableResource(resource("textures/gui/jei/gui.png"), 0, 0, 64, 64, 5, 5, 25, 25)

    override fun drawAnimations(minecraft: Minecraft) {
    }

    override fun drawExtras(minecraft: Minecraft) {
    }

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: GrinderRecipeWrapper) {
        recipeLayout.itemStacks.init(0, true, 48, 15 - 5)
        recipeLayout.itemStacks.init(1, false, 48, 51 - 5)
        recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
        recipeLayout.itemStacks.set(1, recipeWrapper.recipe.primaryOutput)
        if (recipeWrapper.recipe.probability == 0f) return
        recipeLayout.itemStacks.init(2, false, 48 + 18, 51 - 5)
        recipeLayout.itemStacks.set(2, recipeWrapper.recipe.secondaryOutput)
        recipeLayout.itemStacks.addTooltipCallback { slot, input, stack, list -> if (slot == 2) list.add("Probability: %.1f%%".format(recipeWrapper.recipe.probability * 100)) }
    }

    override fun getTitle(): String = title

    override fun getUid(): String = JEIPlugin.GRINDER_ID

    override fun getBackground(): IDrawable = background
}