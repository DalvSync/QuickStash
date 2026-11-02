package org.dalvsync.client;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class InventoryProfilesNextCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("quickstash");
    private static final String IPN_LOCK_HANDLER = "org.anti_ad.mc.ipnext.event.LockSlotsHandler";
    private static boolean warnedAboutUnavailableApi;

    private InventoryProfilesNextCompat() {
    }

    static long getLockedSlotsMask() {
        if (!FabricLoader.getInstance().isModLoaded("inventoryprofilesnext")) {
            return 0L;
        }

        try {
            Class<?> handlerClass = Class.forName(IPN_LOCK_HANDLER);
            Object handler = handlerClass.getField("INSTANCE").get(null);
            Object lockedSlots = handlerClass.getMethod("getLockedInvSlots").invoke(handler);
            if (!(lockedSlots instanceof Iterable<?> slots)) {
                return 0L;
            }

            long mask = 0L;
            for (Object slot : slots) {
                if (slot instanceof Integer inventorySlot && inventorySlot >= 9 && inventorySlot < 36) {
                    mask |= 1L << inventorySlot;
                }
            }
            return mask;
        } catch (ReflectiveOperationException | LinkageError exception) {
            if (!warnedAboutUnavailableApi) {
                LOGGER.warn("Could not read Inventory Profiles Next locked slots; Quick Stash will ignore IPN locks.", exception);
                warnedAboutUnavailableApi = true;
            }
            return 0L;
        }
    }
}
