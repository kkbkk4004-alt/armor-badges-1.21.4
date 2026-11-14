package com.example.armorbadges.mixin;

import com.example.armorbadges.ArmorBadgeRenderer;
import com.example.armorbadges.ArmorBadgesHolder;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {

	// Remember which player this render state belongs to.
	@Inject(
			method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V",
			at = @At("TAIL")
	)
	private void armorbadges$capturePlayer(AbstractClientPlayerEntity player, PlayerEntityRenderState state,
										   float tickDelta, CallbackInfo ci) {
		((ArmorBadgesHolder) state).armorbadges$setPlayer(player);
	}

	// Runs right after the vanilla nametag is drawn, so badges only appear
	// when (and for whom) a nametag is actually visible.
	@Inject(
			method = "renderLabelIfPresent(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			at = @At("TAIL")
	)
	private void armorbadges$renderBadges(PlayerEntityRenderState state, Text text, MatrixStack matrices,
										  VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
		ArmorBadgeRenderer.render(state, ((ArmorBadgesHolder) state).armorbadges$getPlayer(),
				matrices, vertexConsumers);
	}
}
