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
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class quickstashClient implements ClientModInitializer {
    private static KeyBinding stashKeyBinding;

    @Override
    public void onInitializeClient() {
        stashKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickstash.stash",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.category.quickstash.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (stashKeyBinding.wasPressed()) {
                if (client.player != null && client.currentScreen == null) {

                    boolean hasItemsToSort = false;
                    for (int i = 9; i < 36; i++) {
                        if (!client.player.getInventory().getStack(i).isEmpty()) {
                            hasItemsToSort = true;
                            break;
                        }
                    }

                    if (hasItemsToSort) {
                        ClientPlayNetworking.send(new StashRequestPayload());
                    } else {
                        client.player.sendMessage(Text.translatable("message.quickstash.empty_inventory").formatted(Formatting.YELLOW), true);
                    }
                }
            }
        });
    }
}