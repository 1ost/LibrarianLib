package com.teamwizardry.librarianlib.core.mixin;

import com.google.common.collect.ImmutableList;
import com.teamwizardry.librarianlib.core.bridge.IMutableRenderTypeState;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.ArrayList;

@Mixin(RenderType.State.class)
public abstract class RenderTypeStateMixin implements IMutableRenderTypeState {
    @Override
    public void addState(RenderState state) {
        ArrayList<RenderState> states = new ArrayList<>(getStates());
        states.add(state);
        setStates(ImmutableList.copyOf(states));
    }

    @Accessor
    @Mutable
    @Override
    public abstract ImmutableList<RenderState> getStates();

    @Accessor
    @Mutable
    @Override
    public abstract void setStates(ImmutableList<RenderState> renderStates);
}
