package org.dalvsync.client;

import org.dalvsync.quickstash;
import org.dalvsync.StashPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class quickstashClient implements ClientModInitializer {
    private static KeyMapping stashKeyBinding;

    @Override
    public void onInitializeClient() {
        stashKeyMapping = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.quickstash.stash",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.category.quickstash.general"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (stashKeyBinding.consumeClick()) {
                if (client.player != null && client.screen == null) {

                    boolean hasItemsToSort = false;
                    for (int i = 9; i < 36; i++) {
                        if (!client.player.getInventory().getItem(i).isEmpty()) {
                            hasItemsToSort = true;
                            break;
                        }
                    }

                    if (hasItemsToSort) {
                        ClientPlayNetworking.send(new StashPayload());
                    } else {
                        client.player.displayClientMessage(Component.translatable("message.quickstash.empty_inventory").withStyle(ChatFormatting.YELLOW), true);
                    }
                }
            }
        });
    }
}