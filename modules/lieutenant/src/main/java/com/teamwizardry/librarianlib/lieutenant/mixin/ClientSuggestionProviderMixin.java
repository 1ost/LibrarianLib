package com.teamwizardry.librarianlib.lieutenant.mixin;

import com.teamwizardry.librarianlib.lieutenant.ClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ClientSuggestionProvider.class)
public abstract class ClientSuggestionProviderMixin implements ClientCommandSource {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Override
    public void logFeedback(@NotNull ITextComponent message) {
        minecraft.player.displayClientMessage(message, false);
    }

    @Override
    public void sendFeedback(@NotNull ITextComponent message, boolean actionBar) {
        minecraft.player.displayClientMessage(message, actionBar);
    }

    @Override
    public void sendErrorMessage(@NotNull ITextComponent text) {
        minecraft.player.displayClientMessage(new StringTextComponent("").append(text).withStyle(TextFormatting.RED), false);
    }
}
