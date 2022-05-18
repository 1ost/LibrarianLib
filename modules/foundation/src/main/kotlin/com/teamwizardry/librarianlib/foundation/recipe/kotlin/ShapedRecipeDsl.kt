package com.teamwizardry.librarianlib.foundation.recipe.kotlin

import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.ShapedRecipeBuilder
import net.minecraft.data.ShapelessRecipeBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.tags.ITag
import net.minecraft.util.IItemProvider
import net.minecraft.util.ResourceLocation
import java.util.function.Consumer

/**
 *
 */
@RecipeDslMarker
public class ShapedRecipeDsl(result: IItemProvider, count: Int) {
    private val builder: ShapedRecipeBuilder = ShapedRecipeBuilder.shaped(result, count)

    public var group: String = ""
        set(value) {
            field = value
            builder.group(value)
        }

    public fun ingredient(tag: ITag<Item>): Ingredient = Ingredient.of(tag)
    public fun ingredient(vararg items: IItemProvider): Ingredient = Ingredient.of(*items)
    public fun ingredient(vararg stacks: ItemStack): Ingredient = Ingredient.of(*stacks)

    public operator fun String.unaryPlus() {
        builder.pattern(this)
    }

    public operator fun Char.timesAssign(other: Ingredient) { builder.define(this, other) }
    public operator fun Char.timesAssign(other: ITag<Item>) { builder.define(this, other) }
    public operator fun Char.timesAssign(other: IItemProvider) { builder.define(this, other) }

    public fun criteria(config: RecipeCriteriaDsl.() -> Unit) {
        RecipeCriteriaDsl(builder::unlockedBy).config()
    }

    public fun buildRecipe(consumer: Consumer<IFinishedRecipe>, id: ResourceLocation) {
        builder.save(consumer, id)
    }
}