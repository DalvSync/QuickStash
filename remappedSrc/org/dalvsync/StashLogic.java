package org.dalvsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.HopperBlockEntity;

public class StashLogic {

    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final Map<UUID, Long> WARNING_SOUNDS = new HashMap<>();
    private static final long COOLDOWN_TIME = 1000;

    public static void performStash(ServerPlayer player) {
        UUID playerId = player.getUUID();
        long currentTime = System.currentTimeMillis();
        Level world = player.level();
        BlockPos playerPos = player.blockPosition();

        int radius = QuickStashConfig.getInstance().radius;

        boolean movedAnyItem = false;
        boolean outOfSpace = false;

        if (COOLDOWNS.containsKey(playerId)) {
            long lastUsedTime = COOLDOWNS.get(playerId);
            if (currentTime - lastUsedTime < COOLDOWN_TIME) {
                Long lastWarningFor = WARNING_SOUNDS.get(playerId);
                if (lastWarningFor == null || lastWarningFor != lastUsedTime) {
                    world.playSound(null, player.blockPosition(), quickstash.WAIT_MESSAGE_EVENT, SoundSource.PLAYERS, 0.5f, 1.2f);
                    WARNING_SOUNDS.put(playerId, lastUsedTime);
                }

                player.displayClientMessage(Component.translatable("message.quickstash.cooldown").withStyle(ChatFormatting.DARK_RED), true);
                return;
            }
        }
        COOLDOWNS.put(playerId, currentTime);

        Inventory playerInv = player.getInventory();

        Iterable<BlockPos> blocksInRadius = BlockPos.betweenClosed(
                playerPos.offset(-radius, -radius, -radius),
                playerPos.offset(radius, radius, radius)
        );

        List<Container> nearbyInventories = new ArrayList<>();
        for (BlockPos pos : blocksInRadius) {
            Container targetInventory = HopperBlockEntity.getContainerAt(world, pos);
            if (targetInventory != null) {
                nearbyInventories.add(targetInventory);
            }
        }

        if (nearbyInventories.isEmpty()) {
            player.displayClientMessage(Component.translatable("message.quickstash.no_containers").withStyle(ChatFormatting.YELLOW), true);
            return;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack playerStack = playerInv.getItem(i);
            if (playerStack.isEmpty()) continue;

            int originalCount = playerStack.getCount();
            boolean hadMatch = false;

            for (Container targetInventory : nearbyInventories) {
                if (hasMatchingItem(playerStack, targetInventory)) {
                    hadMatch = true;
                    stashIntoInventory(playerStack, targetInventory);
                    if (playerStack.isEmpty()) break;
                }
            }

            if (hadMatch && !playerStack.isEmpty()) {
                outOfSpace = true;
            }
            if (originalCount > playerStack.getCount()) {
                movedAnyItem = true;
            }
        }

        if (movedAnyItem) {
            world.playSound(null, player.blockPosition(), quickstash.SUCCESS_EVENT, SoundSource.PLAYERS, 0.5f, 1.2f);
            player.displayClientMessage(Component.translatable("message.quickstash.success").withStyle(ChatFormatting.GREEN), true);
            player.getInventory().setChanged();
        }

        if (outOfSpace) {
            if (!movedAnyItem) {
                world.playSound(null, player.blockPosition(), quickstash.INVENTORY_FULL_EVENT, SoundSource.PLAYERS, 0.5f, 1.2f);
            }
            player.displayClientMessage(Component.translatable("message.quickstash.full").withStyle(ChatFormatting.GOLD), false);
        }
    }

    private static boolean hasMatchingItem(ItemStack playerStack, Container targetInventory) {
        for (int i = 0; i < targetInventory.getContainerSize(); i++) {
            ItemStack targetStack = targetInventory.getItem(i);
            if (!targetStack.isEmpty() && ItemStack.isSameItemSameComponents(playerStack, targetStack)) {
                return true;
            }
        }
        return false;
    }

    private static void stashIntoInventory(ItemStack playerStack, Container targetInventory) {
        for (int i = 0; i < targetInventory.getContainerSize(); i++) {
            if (playerStack.isEmpty()) return;
            ItemStack targetStack = targetInventory.getItem(i);

            if (!targetStack.isEmpty() && ItemStack.isSameItemSameComponents(playerStack, targetStack)) {
                int spaceLeft = targetStack.getMaxStackSize() - targetStack.getCount();
                if (spaceLeft > 0) {
                    int amountToMove = Math.min(spaceLeft, playerStack.getCount());
                    targetStack.grow(amountToMove);
                    playerStack.shrink(amountToMove);
                    targetInventory.setChanged();
                }
            }
        }

        for (int i = 0; i < targetInventory.getContainerSize(); i++) {
            if (playerStack.isEmpty()) return;
            ItemStack targetStack = targetInventory.getItem(i);

            if (targetStack.isEmpty()) {
                targetInventory.setItem(i, playerStack.copy());
                playerStack.setCount(0);
                targetInventory.setChanged();
            }
        }
    }
}