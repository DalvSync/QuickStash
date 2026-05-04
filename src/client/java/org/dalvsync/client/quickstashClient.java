package org.dalvsync.client;

import org.dalvsync.network.StashRequestPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

public class quickstashClient implements ClientModInitializer {

    // 1. Офіційно реєструємо категорію як окремий об'єкт
    public static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath("quickstash", "general")
    );

    private static KeyMapping stashKeyBinding;

    @Override
    public void onInitializeClient() {

        // 2. Передаємо об'єкт CATEGORY як останній аргумент
        stashKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.quickstash.stash",
                InputConstants.Type.KEYSYM, // Явна вказівка, що це кнопка клавіатури
                GLFW.GLFW_KEY_X,
                CATEGORY
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
                        ClientPlayNetworking.send(new StashRequestPayload());
                    } else {
                        client.player.sendSystemMessage(Component.translatable("message.quickstash.empty_inventory").withStyle(ChatFormatting.YELLOW));
                    }
                }
            }
        });
    }
}