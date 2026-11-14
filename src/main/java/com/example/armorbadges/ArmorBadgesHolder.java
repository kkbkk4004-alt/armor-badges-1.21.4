package com.example.armorbadges;

import net.minecraft.client.network.AbstractClientPlayerEntity;

/**
 * Lets us carry the player entity along on the render state, because in 1.21.2+
 * the label renderer only receives the state object, not the entity.
 */
public interface ArmorBadgesHolder {
	AbstractClientPlayerEntity armorbadges$getPlayer();

	void armorbadges$setPlayer(AbstractClientPlayerEntity player);
}
