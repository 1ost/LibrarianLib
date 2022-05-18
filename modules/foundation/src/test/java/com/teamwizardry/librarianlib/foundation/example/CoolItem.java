package com.teamwizardry.librarianlib.foundation.example;

import com.teamwizardry.librarianlib.foundation.item.BaseItem;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.text.TranslationTextComponent;
import org.jetbrains.annotations.NotNull;

public class CoolItem extends BaseItem {
    public CoolItem(@NotNull Item.Properties properties) {
        super(properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        if (!context.getLevel().isClientSide) {
            ExampleModContainers.coolContainer.open(
                    (ServerPlayerEntity) context.getPlayer(),
                    new TranslationTextComponent("examplemod.cool_container.title"),
                    // additional constructor arguments:
                    context.getClickedPos()
            );
        }
        return ActionResultType.SUCCESS;
    }
}
