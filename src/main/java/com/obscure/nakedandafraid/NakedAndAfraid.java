package com.obscure.nakedandafraid;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.GameRules.Category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NakedAndAfraid implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MOD_ID = "naked-and-afraid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final GameRules.Key<GameRules.BooleanRule> NAKED_AND_AFRAID = GameRuleRegistry
			.register("nakedAndAfraid", Category.PLAYER, GameRuleFactory.createBooleanRule(false));

	@Override
	public void onInitialize() {
		ServerWorldEvents.LOAD.register((server, world) -> {
			DisablePvP(world);
			ApplyArmorDamage(world);
			DisableChatJoinLeaveMessage(world);
			DisableTotemOfUndying(world);
			HandleDeath(world);
		});

		ServerTickEvents.START_WORLD_TICK.register((world) -> {
			if (world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				// if naked and afraid is enabled, set the following game rules
				// reducedDebugInfo to true
				// playersSleepingPercentage to 100
				// set world to hardcore

				world.getGameRules().get(GameRules.REDUCED_DEBUG_INFO).set(true, world.getServer());
				world.getGameRules().get(GameRules.PLAYERS_SLEEPING_PERCENTAGE).set(100, world.getServer());

				world.getServer().getSaveProperties().setDifficulty(Difficulty.HARD);
			}
		});
	}

	private void DisablePvP(World world) {
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			if (entity instanceof PlayerEntity && source.getAttacker() instanceof PlayerEntity) {
				return false;
			}

			return true;
		});
	}

	private void ApplyArmorDamage(World world) {
		ServerTickEvents.END_SERVER_TICK.register((server) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return;
			}

			server.getPlayerManager().getPlayerList().forEach((player) -> {
				if (!player.getInventory().getArmorStack(0).isEmpty()
						|| !player.getInventory().getArmorStack(1).isEmpty()
						|| !player.getInventory().getArmorStack(2).isEmpty()
						|| !player.getInventory().getArmorStack(3).isEmpty()) {
					DamageSource damageSource = new DamageSource(
							server.getRegistryManager()
									.get(RegistryKeys.DAMAGE_TYPE)
									.entryOf(DamageTypes.MAGIC));
					player.damage(damageSource, 1);
				}

			});
		});
	}

	private void DisableChatJoinLeaveMessage(World world) {
		ServerMessageEvents.ALLOW_COMMAND_MESSAGE.register((message, source, parameters) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			if (source.isExecutedByPlayer()) {
				source.getPlayer().sendMessage(Text.of("Chat is disabled!"), true);
			}

			return false;
		});

		ServerMessageEvents.ALLOW_GAME_MESSAGE.register((server, message, overlay) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			return false;
		});

		ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			if (sender.isPlayer()) {
				sender.sendMessage(Text.of("Chat is disabled!"), true);
			}

			return false;
		});

		ServerMessageEvents.COMMAND_MESSAGE.register((message, source, params) -> {
			if (world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				source.getPlayer().sendMessage(Text.of("Commands are disabled!"), true);
				message = new SignedMessage(null, null,
						null, Text.of("Commands are disabled"), null);
			}
		});

		ServerMessageEvents.CHAT_MESSAGE.register((message, sender, params) -> {
			if (world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				sender.sendMessage(Text.of("Chat is disabled!"), true);
				message = new SignedMessage(null, null,
						null, Text.of("Chat is disabled"), null);
			}
		});

		ServerMessageEvents.GAME_MESSAGE.register((server, message, overlay) -> {
			if (world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				message = Text.of("Game messages are disabled");
			}
		});
	}

	private void DisableTotemOfUndying(World world) {
		ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			if (!(entity instanceof PlayerEntity)) {
				return true;
			}

			PlayerEntity player = (PlayerEntity) entity;
			int totemSlot = player.getInventory().getSlotWithStack(Items.TOTEM_OF_UNDYING.getDefaultStack());
			if (totemSlot >= 1) {
				ItemStack totemStack = player.getInventory().getStack(totemSlot);
				totemStack.setCount(0);
			} else if (totemSlot == 0) {
				ItemStack totemStack = player.getInventory().getStack(totemSlot);
				totemStack.setCount(0);
			} else if (totemSlot == -1) {
				// get the offhand slot
				var offhandSlot = player.getInventory().offHand.get(0);
				if (offhandSlot.getItem() == Items.TOTEM_OF_UNDYING) {
					offhandSlot.setCount(0);
				}
			}

			return true;
		});
	}

	private void HandleDeath(World world) {
		ServerLivingEntityEvents.ALLOW_DEATH.register((entity, damageSource, damageAmount) -> {
			if (!world.getGameRules().get(NAKED_AND_AFRAID).get()) {
				return true;
			}

			if (world.getLevelProperties().isHardcore() && entity instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entity;

				player.sendMessage(Text.literal("You should have been more careful!")
						.styled(style -> style.withColor(0xFF0000)), true);

				// set gamemode to spectator
				((ServerPlayerEntity) player).changeGameMode(GameMode.SPECTATOR);

				// set health to 0
				player.setHealth(1);

				// drop all items
				for (int i = 0; i < player.getInventory().size(); i++) {
					player.dropItem(player.getInventory().getStack(i), true, true);
				}

				player.dropItem(player.getInventory().offHand.get(0), true, true);

				player.dropItem(player.getInventory().getArmorStack(0), true, true);
				player.dropItem(player.getInventory().getArmorStack(1), true, true);
				player.dropItem(player.getInventory().getArmorStack(2), true, true);
				player.dropItem(player.getInventory().getArmorStack(3), true, true);

				// clear inventory
				player.getInventory().clear();
				player.getInventory().offHand.clear();
				player.getInventory().armor.clear();

				return false;
			}

			return true;
		});
	}
}