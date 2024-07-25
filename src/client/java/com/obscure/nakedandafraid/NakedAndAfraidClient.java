package com.obscure.nakedandafraid;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.option.Perspective;
import net.minecraft.text.Text;

public class NakedAndAfraidClient implements ClientModInitializer {
	public static boolean hasGameRule = true;

	@Override
	public void onInitializeClient() {
		ClientPlayConnectionEvents.INIT.register((handler, client) -> {
			// check if the server has game rule is present
			if (client.world == null) {
				NakedAndAfraid.LOGGER.info("Client World is null");
				return;
			}

			try {
				client.getServer().getGameRules().get(NakedAndAfraid.NAKED_AND_AFRAID).get();
				NakedAndAfraid.LOGGER.info("Game rule found");
			} catch (Exception e) {
				NakedAndAfraid.LOGGER.info("Game rule not found");
				hasGameRule = false;
			}
		});

		if (!hasGameRule) {
			return;
		}
		
		// Change perspective to first person at the end of each tick
		ClientTickEvents.END_CLIENT_TICK.register((client) -> {
			// check if the server has game rule is present
			if (client.world == null || client.getServer() == null) {
				return;
			}

			if (client.getServer().getGameRules().get(NakedAndAfraid.NAKED_AND_AFRAID).get()) {
				if (client.options.getPerspective() != Perspective.FIRST_PERSON) {
					client.options.setPerspective(Perspective.FIRST_PERSON);

					client.player.sendMessage(Text.of("You cannot change your perspective"), true);
				}
			}
		});
	}
}