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

public class StashLogic {

    public static void performStash(ServerPlayerEntity player) {
        // Використовуємо правильний метод для 1.21.11
        World world = player.getEntityWorld();
        BlockPos playerPos = player.getBlockPos();
        int radius = 8;
        boolean movedAnyItem = false;

        PlayerInventory playerInv = player.getInventory();

        // Генеруємо всі координати блоків навколо гравця в радіусі 8х8х8
        Iterable<BlockPos> blocksInRadius = BlockPos.iterate(
                playerPos.add(-radius, -radius, -radius),
                playerPos.add(radius, radius, radius)
        );

        // Скануємо лише основний інвентар (слоти 9-35), ігноруючи Hotbar (0-8) та броню
        for (int i = 9; i < 36; i++) {
            ItemStack playerStack = playerInv.getStack(i);
            if (playerStack.isEmpty()) continue;

            for (BlockPos pos : blocksInRadius) {
                // HopperBlockEntity.getInventoryAt коректно обробляє подвійні скрині
                Inventory targetInventory = HopperBlockEntity.getInventoryAt(world, pos);
                if (targetInventory == null) continue;

                if (tryStashItem(playerStack, targetInventory)) {
                    movedAnyItem = true;
                    if (playerStack.isEmpty()) break; // Якщо стак порожній, йдемо до наступного предмета
                }
            }
        }

        // ЗВУКОВИЙ ВІДГУК: Відтворюємо звук досвіду, якщо сортування пройшло успішно
        if (movedAnyItem) {
            world.playSound(
                    null, // null означає, що звук буде відправлено всім гравцям поруч, зокрема і нашому
                    player.getBlockPos(),
                    SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS,
                    0.5f, // Гучність
                    1.2f  // Висота (Pitch)
            );
            player.getInventory().markDirty(); // Оновлюємо інвентар
        }
        if (movedAnyItem) {
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.PLAYERS, 0.5f, 1.2f);

            // ДОБАВЛЕНА ЭТА СТРОКА (отправка сообщения)
            // false = обычный чат. Если поставить true, текст появится красиво над хотбаром (Action Bar)
            player.sendMessage(Text.translatable("message.quickstash.success"), false);

            player.getInventory().markDirty();
        }
    }

    private static boolean tryStashItem(ItemStack playerStack, Inventory targetInventory) {
        boolean containsMatchingItem = false;
        boolean stashed = false;

        // Крок 1: Перевіряємо, чи є в контейнері ТАКИЙ САМИЙ предмет (Data Components)
        for (int i = 0; i < targetInventory.size(); i++) {
            ItemStack targetStack = targetInventory.getStack(i);
            if (!targetStack.isEmpty() && ItemStack.areItemsAndComponentsEqual(playerStack, targetStack)) {
                containsMatchingItem = true;
                break;
            }
        }

        // Крок 2: Якщо знайшли збіг, перекладаємо предмети у вільні слоти або доповнюємо стаки
        if (containsMatchingItem) {
            for (int i = 0; i < targetInventory.size(); i++) {
                if (playerStack.isEmpty()) break;
                ItemStack targetStack = targetInventory.getStack(i);

                if (targetStack.isEmpty()) {
                    targetInventory.setStack(i, playerStack.copy());
                    playerStack.setCount(0);
                    stashed = true;
                    targetInventory.markDirty();
                } else if (ItemStack.areItemsAndComponentsEqual(playerStack, targetStack) && targetStack.getCount() < targetStack.getMaxCount()) {
                    int spaceLeft = targetStack.getMaxCount() - targetStack.getCount();
                    int amountToMove = Math.min(spaceLeft, playerStack.getCount());

                    targetStack.increment(amountToMove);
                    playerStack.decrement(amountToMove);
                    stashed = true;
                    targetInventory.markDirty();
                }
            }
        }
        return stashed;
    }
}