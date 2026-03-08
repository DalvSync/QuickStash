package org.dalvsync.client;

import org.dalvsync.network.StashRequestPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

public class quickstashClient implements ClientModInitializer {
    private static KeyBinding stashKeyBinding;

    @Override
    public void onInitializeClient() {
        stashKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickstash.stash",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                // Ось єдиний правильний варіант для 4-го параметра:
                KeyBinding.Category.create(Identifier.of("quickstash", "general"))
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (stashKeyBinding.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {
                    ClientPlayNetworking.send(new StashRequestPayload());
                }
            }
        });
    }
}