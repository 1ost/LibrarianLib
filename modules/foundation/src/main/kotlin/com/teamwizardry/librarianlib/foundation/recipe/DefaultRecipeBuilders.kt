package com.teamwizardry.librarianlib.foundation.recipe

import net.minecraft.data.*
import net.minecraft.item.crafting.IRecipeSerializer
import net.minecraft.item.crafting.Ingredient
import net.minecraft.util.IItemProvider

public class DefaultRecipeBuilders {
    @JvmOverloads
    public fun shaped(result: IItemProvider, count: Int = 1): ShapedRecipeBuilder =
        ShapedRecipeBuilder.shaped(result, count)

    @JvmOverloads
    public fun shapeless(result: IItemProvider, count: Int = 1): ShapelessRecipeBuilder =
        ShapelessRecipeBuilder.shapeless(result, count)

    /**
     * Used for furnaces
     */
    public fun smelting(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cooking(ingredient, result, experience, cookingTime, IRecipeSerializer.SMELTING_RECIPE)

    /**
     * Used for ores and general "melting" recipes, with a 0.5x cooking time multiplier
     */
    public fun blasting(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cooking(ingredient, result, experience, cookingTime, IRecipeSerializer.BLASTING_RECIPE)

    /**
     * Used for food, with a 0.5x cooking time multiplier
     */
    public fun smoker(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder =
        CookingRecipeBuilder.cooking(ingredient, result, experience, cookingTime, IRecipeSerializer.SMOKING_RECIPE)

    /**
     * Used for food, with a 3x cooking time multiplier
     */
    public fun campfire(
        ingredient: Ingredient,
        result: IItemProvider,
        experience: Float,
        cookingTime: Int
    ): CookingRecipeBuilder = CookingRecipeBuilder.cooking(
        ingredient,
        result,
        experience,
        cookingTime,
        IRecipeSerializer.CAMPFIRE_COOKING_RECIPE
    )

    @JvmOverloads
    public fun stonecutting(ingredient: Ingredient, result: IItemProvider, count: Int = 1): SingleItemRecipeBuilder =
        SingleItemRecipeBuilder.stonecutting(ingredient, result, count)

    public fun smithing(base: Ingredient, addition: Ingredient, output: IItemProvider): SmithingRecipeBuilder =
        SmithingRecipeBuilder.smithing(base, addition, output.asItem())
}