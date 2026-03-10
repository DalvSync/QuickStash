package org.dalvsync;

import net.minecraft.block.entity.HopperBlockEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StashLogic {

    private static final Map<UUID, Long> COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_TIME = 1000;

    public static void performStash(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();
        World world = player.getWorld();
        BlockPos playerPos = player.getBlockPos();

        int radius = QuickStashConfig.getInstance().radius;

        boolean movedAnyItem = false;
        boolean outOfSpace = false;

        if (COOLDOWNS.containsKey(playerId)) {
            long lastUsedTime = COOLDOWNS.get(playerId);
            if (currentTime - lastUsedTime < COOLDOWN_TIME) {
                player.sendMessage(Text.translatable("message.quickstash.cooldown").formatted(Formatting.RED), true);
                return;
            }
        }
        COOLDOWNS.put(playerId, currentTime);

        PlayerInventory playerInv = player.getInventory();

        Iterable<BlockPos> blocksInRadius = BlockPos.iterate(
                playerPos.add(-radius, -radius, -radius),
                playerPos.add(radius, radius, radius)
        );

        List<Inventory> nearbyInventories = new ArrayList<>();
        for (BlockPos pos : blocksInRadius) {
            Inventory targetInventory = HopperBlockEntity.getInventoryAt(world, pos);
            if (targetInventory != null) {
                nearbyInventories.add(targetInventory);
            }
        }

        if (nearbyInventories.isEmpty()) {
            player.sendMessage(Text.translatable("message.quickstash.no_containers").formatted(Formatting.YELLOW), true);
            return;
        }

        for (int i = 9; i < 36; i++) {
            ItemStack playerStack = playerInv.getStack(i);
            if (playerStack.isEmpty()) continue;

            int originalCount = playerStack.getCount();
            boolean hadMatch = false;

            for (Inventory targetInventory : nearbyInventories) {
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
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 1.2f);
            player.sendMessage(Text.translatable("message.quickstash.success").formatted(Formatting.GREEN), true);
            player.getInventory().markDirty();
        }

        if (outOfSpace) {
            player.sendMessage(Text.translatable("message.quickstash.full").formatted(Formatting.GOLD), false);
        }
    }

    private static boolean hasMatchingItem(ItemStack playerStack, Inventory targetInventory) {
        for (int i = 0; i < targetInventory.size(); i++) {
            ItemStack targetStack = targetInventory.getStack(i);
            if (!targetStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(playerStack, targetStack)) {
                return true;
            }
        }
        return false;
    }

    private static void stashIntoInventory(ItemStack playerStack, Inventory targetInventory) {
        for (int i = 0; i < targetInventory.size(); i++) {
            if (playerStack.isEmpty()) return;
            ItemStack targetStack = targetInventory.getStack(i);

            if (!targetStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(playerStack, targetStack)) {
                int spaceLeft = targetStack.getMaxCount() - targetStack.getCount();
                if (spaceLeft > 0) {
                    int amountToMove = Math.min(spaceLeft, playerStack.getCount());
                    targetStack.increment(amountToMove);
                    playerStack.decrement(amountToMove);
                    targetInventory.markDirty();
                }
            }
        }

        for (int i = 0; i < targetInventory.size(); i++) {
            if (playerStack.isEmpty()) return;
            ItemStack targetStack = targetInventory.getStack(i);

            if (targetStack.isEmpty()) {
                targetInventory.setStack(i, playerStack.copy());
                playerStack.setCount(0);
                targetInventory.markDirty();
            }
        }
    }
}