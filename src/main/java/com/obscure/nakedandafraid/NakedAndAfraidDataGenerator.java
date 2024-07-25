package com.obscure.nakedandafraid;

import java.util.concurrent.CompletableFuture;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.registry.RegistryWrapper.WrapperLookup;

public class NakedAndAfraidDataGenerator implements DataGeneratorEntrypoint {
	public static CompletableFuture<WrapperLookup> registryLookup;

	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
	}
}
