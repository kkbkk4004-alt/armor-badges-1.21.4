package com.example.armorbadges.mixin;

import com.example.armorbadges.ArmorBadgesHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(PlayerEntityRenderState.class)
public class PlayerEntityRenderStateMixin implements ArmorBadgesHolder {
	@Unique
	private AbstractClientPlayerEntity armorbadges$player;

	@Override
	public AbstractClientPlayerEntity armorbadges$getPlayer() {
		return armorbadges$player;
	}

	@Override
	public void armorbadges$setPlayer(AbstractClientPlayerEntity player) {
		this.armorbadges$player = player;
	}
}
