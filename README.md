[![Fabric](https://github.com/intergrav/devins-badges/raw/refs/heads/v3/assets/cozy/requires/fabric-api_vector.svg)](https://fabricmc.net/)
[![Modrinth](https://github.com/intergrav/devins-badges/raw/refs/heads/v3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/quick-stash)
# Quick Stash ⚡

Quick Stash is a lightweight Fabric mod that lets players instantly stash matching items from their inventory into nearby chests/containers with a single key press — without stealing hotbar tools or armor. It’s designed to be simple, server-safe, and respectful of vanilla behaviour.

### 🚀 What Quick Stash does
Press the configured hotkey (default: `X`) while in-game and Quick Stash will:

* Search around the player for container blocks (Chests, Barrels, Shulker Boxes, etc.).
* For each item in the player's inventory (excluding Hotbar, Armor and Offhand), attempt to move it into containers **that already contain the same item** and have available stack space.
* The transfer happens entirely **server-side** to avoid ghost items and desyncs.
* If any items were moved, the player hears a pleasant confirmation sound (`ENTITY_EXPERIENCE_ORB_PICKUP`).

### ⚙️ Features
* **One-press stashing:** user-friendly single-key workflow.
* **Server-side logic:** all item movement is performed on the server to eliminate synchronization issues.
* **Hotbar-safe:** items in the hotbar (first 9 slots), armor and offhand are never touched.
* **Container-aware:** only containers that already have at least one stack of the same item are considered — preserves expected player behaviour.

### 🔧 Compatibility
* Built for Fabric Loader and the Fabric API.

---

## Example usage
1. Join or host a Fabric server with Quick Stash installed.
2. Stand near your chests.
3. Press the Quick Stash hotkey (`X` by default).
4. Watch items move into the chests that already hold the same items.

---

## Short FAQ
**Q:** Does Quick Stash take items from my hotbar or armor?  
**A:** No — the mod intentionally ignores the hotbar (first 9 slots), armor slots and offhand so you won't lose tools or held items.

**Q:** Is this mod singleplayer-safe?  
**A:** Yes — the networking packet is handled locally in singleplayer because a local integrated server receives the packet and executes the server logic.
