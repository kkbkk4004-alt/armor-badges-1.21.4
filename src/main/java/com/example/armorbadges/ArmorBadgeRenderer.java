package com.example.armorbadges;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws a row of armor icons with durability percentages above a player's nametag.
 * Called from {@link com.example.armorbadges.mixin.PlayerEntityRendererMixin}.
 *
 * Coordinates below are in "nametag units": 1 unit = 0.025 blocks, +y points DOWN
 * (the same convention vanilla uses when it draws the nametag text).
 */
public final class ArmorBadgeRenderer {
	private static final EquipmentSlot[] SLOTS = {
			EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
	};

	private static final int FULL_BRIGHT = 0xF000F0;
	private static final int ICON = 10;      // icon size in units
	private static final int TEXT_GAP = 2;   // icon -> text
	private static final int CELL_GAP = 6;   // between armor pieces
	private static final int LIFT = 2;       // gap between row and nametag
	private static final float SCALE = 0.75f;

	private ArmorBadgeRenderer() {}

	public static void render(PlayerEntityRenderState state, AbstractClientPlayerEntity player,
							  MatrixStack matrices, VertexConsumerProvider vertexConsumers) {
		if (!ArmorBadgesClient.enabled || player == null) return;

		Vec3d anchor = state.nameLabelPos;
		if (anchor == null) return;

		List<ItemStack> stacks = new ArrayList<>(4);
		for (EquipmentSlot slot : SLOTS) {
			ItemStack stack = player.getEquippedStack(slot);
			if (!stack.isEmpty()) stacks.add(stack);
		}
		if (stacks.isEmpty()) return;

		MinecraftClient mc = MinecraftClient.getInstance();
		TextRenderer textRenderer = mc.textRenderer;

		// Measure the row so it can be centered.
		String[] labels = new String[stacks.size()];
		int total = 0;
		for (int i = 0; i < stacks.size(); i++) {
			labels[i] = label(stacks.get(i));
			int w = ICON;
			if (!labels[i].isEmpty()) w += TEXT_GAP + textRenderer.getWidth(labels[i]);
			total += w;
		}
		total += CELL_GAP * (stacks.size() - 1);

		// Vanilla shifts the nametag up when a "below name" scoreboard objective is shown.
		float extra = 0f;
		if (player.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME) != null
				&& mc.getEntityRenderDispatcher().getSquaredDistanceToCamera(player) < 100.0) {
			extra = 9.0f * 1.15f;
		}

		matrices.push();
		matrices.translate(anchor.x, anchor.y + 0.5, anchor.z);
		matrices.multiply(mc.getEntityRenderDispatcher().getRotation());
		matrices.scale(0.025f, -0.025f, 0.025f);
		matrices.translate(0.0f, -(LIFT + extra), 0.0f);
		matrices.scale(SCALE, SCALE, SCALE);

		// Row occupies y in [-ICON, 0]; nametag text sits just below it.
		float x = -total / 2f;
		for (int i = 0; i < stacks.size(); i++) {
			ItemStack stack = stacks.get(i);

			matrices.push();
			matrices.translate(x + ICON / 2f, -ICON / 2f, 0.0f);
			matrices.scale(ICON, -ICON, ICON);
			ItemRenderState itemState = new ItemRenderState();
			mc.getItemModelManager().update(itemState, stack, ModelTransformationMode.GUI, false,
					mc.world, player, player.getId());
			itemState.render(matrices, vertexConsumers, FULL_BRIGHT, OverlayTexture.DEFAULT_UV);
			matrices.pop();

			x += ICON;
			if (!labels[i].isEmpty()) {
				int color = 0xFF000000 | stack.getItemBarColor();
				textRenderer.draw(labels[i], x + TEXT_GAP, -ICON + 1, color, true,
						matrices.peek().getPositionMatrix(), vertexConsumers,
						TextRenderer.TextLayerType.NORMAL, 0, FULL_BRIGHT);
				x += TEXT_GAP + textRenderer.getWidth(labels[i]);
			}
			x += CELL_GAP;
		}

		matrices.pop();
	}

	/** "85%" for damageable items, empty for things like pumpkins that never break. */
	private static String label(ItemStack stack) {
		if (!stack.isDamageable()) return "";
		int max = stack.getMaxDamage();
		int left = Math.max(0, max - stack.getDamage());
		return Math.round(left * 100f / max) + "%";
	}
}
