package com.teamwizardry.librarianlib.foundation.recipe.kotlin

import net.minecraft.data.IFinishedRecipe
import net.minecraft.data.ShapelessRecipeBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.Ingredient
import net.minecraft.tags.ITag
import net.minecraft.util.IItemProvider
import net.minecraft.util.ResourceLocation
import java.util.function.Consumer

/**
 * ```kotlin
 * dsl.shapeless("dirt_to_diamonds", Items.DIAMOND, 10) {
 *     inputs = 1 * Items.DIRT + 3 * ItemTags.COALS
 *
 *     criteria {
 *         hasItem("has_dirt", Items.DIRT)
 *     }
 * }
 * ```
 */
@RecipeDslMarker
public class ShapelessRecipeDsl(result: IItemProvider, count: Int) {
    private val builder: ShapelessRecipeBuilder = ShapelessRecipeBuilder.shapeless(result, count)

    public var inputs: List<Ingredient> = emptyList()
    public var group: String = ""
        set(value) {
            field = value
            builder.group(value)
        }

    public fun ingredient(tag: ITag<Item>): Ingredient = Ingredient.of(tag)
    public fun ingredient(vararg items: IItemProvider): Ingredient = Ingredient.of(*items)
    public fun ingredient(vararg stacks: ItemStack): Ingredient = Ingredient.of(*stacks)

    public operator fun Int.times(other: Ingredient): List<Ingredient> = List(this) { other }
    public operator fun Int.times(other: ITag<Item>): List<Ingredient> = List(this) { ingredient(other) }
    public operator fun Int.times(other: IItemProvider): List<Ingredient> = List(this) { ingredient(other) }

    public fun criteria(config: RecipeCriteriaDsl.() -> Unit) {
        RecipeCriteriaDsl(builder::unlockedBy).config()
    }

    public fun buildRecipe(consumer: Consumer<IFinishedRecipe>, id: ResourceLocation) {
        for(ingredient in inputs) {
            builder.requires(ingredient)
        }
        builder.save(consumer, id)
    }
}