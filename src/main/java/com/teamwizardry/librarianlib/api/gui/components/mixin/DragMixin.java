package com.teamwizardry.librarianlib.api.gui.components.mixin;

import java.util.function.Function;

import com.teamwizardry.librarianlib.api.gui.EnumMouseButton;
import com.teamwizardry.librarianlib.api.gui.GuiComponent;
import com.teamwizardry.librarianlib.api.gui.HandlerList;
import com.teamwizardry.librarianlib.math.Vec2;

public class DragMixin<T extends GuiComponent<?>> {
	
	protected T component;
	protected Function<Vec2, Vec2> constraints;
	
	public boolean mouseDown = false;
	public Vec2 clickPos = Vec2.ZERO;
	
	public final HandlerList<IDragCancelableEvent<T>> pickup = new HandlerList<>();
	public final HandlerList<IDragCancelableEvent<T>> drop = new HandlerList<>();
	public final HandlerList<IDragEvent<T>> drag = new HandlerList<>();
	
	public DragMixin(T component, Function<Vec2, Vec2> constraints) {
		this.component = component;
		this.constraints = constraints;
		init();
	}
	
	private void init() {
		component.mouseDown.add( (c, pos, button) -> {
			if(!mouseDown && c.isMouseOver(pos) && !pickup.fireCancel((h) -> h.handle(component, button, pos))) {
				mouseDown = true;
				clickPos = pos;
				return true;
			}
			return false;
		});
		component.mouseUp.add( (c, pos, button) -> {
			if(mouseDown && !drop.fireCancel((h) -> h.handle(component, button, pos)))
				mouseDown = false;
			return false;
		});
		component.preDraw.add( (c, pos, partialTicks) -> {
			if(mouseDown) {
				Vec2 newPos = constraints.apply(  c.getPos().add(pos).sub(clickPos)  );
				
				if(!newPos.equals(c.getPos())) {
					c.setPos(newPos);
					drag.fireAll((h) -> h.handle(component, newPos));
				}
			}
		});
	}
	
	@FunctionalInterface
	public static interface IDragEvent<T> {
		void handle(T component, Vec2 pos);
	}
	
	@FunctionalInterface
	public static interface IDragCancelableEvent<T> {
		boolean handle(T component, EnumMouseButton button, Vec2 pos);
	}
}
