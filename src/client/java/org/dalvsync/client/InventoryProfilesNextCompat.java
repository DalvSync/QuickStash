package org.dalvsync.client;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class InventoryProfilesNextCompat {
    private static final Logger LOGGER = LoggerFactory.getLogger("quickstash");
    private static final String IPN_LOCK_HANDLER = "org.anti_ad.mc.ipnext.event.LockSlotsHandler";
    private static boolean warned;

    private InventoryProfilesNextCompat() {}

    static long getLockedSlotsMask() {
        if (!FabricLoader.getInstance().isModLoaded("inventoryprofilesnext")) return 0L;
        try {
            Class<?> type = Class.forName(IPN_LOCK_HANDLER);
            Object handler = type.getField("INSTANCE").get(null);
            Object value = type.getMethod("getLockedInvSlots").invoke(handler);
            if (!(value instanceof Iterable<?> slots)) return 0L;
            long mask = 0L;
            for (Object slot : slots) {
                if (slot instanceof Integer index && index >= 9 && index < 36) mask |= 1L << index;
            }
            return mask;
        } catch (ReflectiveOperationException | LinkageError e) {
            if (!warned) { LOGGER.warn("Could not read IPN locked slots; Quick Stash will ignore IPN locks.", e); warned = true; }
            return 0L;
        }
    }
}
