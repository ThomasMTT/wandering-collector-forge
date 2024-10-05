/*
 * Copyright 2024 ThomasMTT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package life.thoms.mods.wandering_collector.data;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Manages the persistence of player loot in the Wandering Collector mod.
 */
public class LootPersistenceManager {

    /**
     * Saves the loot of a player to persistent data.
     *
     * @param player the player whose loot is to be saved
     */
    public static void savePlayerLoot(Player player) {
        CompoundTag playerPersistentData = player.getPersistentData();
        CompoundTag playerLootData = new CompoundTag();
        CompoundTag lootData = new CompoundTag();

        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());

        for (ItemStack stack : playerLoot) {
            UUID itemStackUUID = CustomLootDataUtil.getStackUniqueIdentifier(stack);
            if (itemStackUUID != null) {
                lootData.put(itemStackUUID.toString(), stack.save(player.level().registryAccess()));
            }
        }
        playerLootData.put(player.getStringUUID(), lootData);
        playerPersistentData.put(ModConstants.PLAYER_LOOT_KEY, playerLootData);
    }

    /**
     * Loads the loot of a player from persistent data.
     *
     * @param player the player whose loot is to be loaded
     */
    public static void loadPlayerLoot(ServerPlayer player) {
        CompoundTag playerData = player.getPersistentData();

        if (playerData.contains(ModConstants.PLAYER_LOOT_KEY, Tag.TAG_COMPOUND)) {
            CompoundTag currentPlayerLoot = playerData
                    .getCompound(ModConstants.PLAYER_LOOT_KEY)
                    .getCompound(player.getStringUUID());

            List<ItemStack> playerLoot = new ArrayList<>();

            for (String key : currentPlayerLoot.getAllKeys()) {
                Tag itemTag = currentPlayerLoot.get(key);
                if (itemTag != null) {
                    Optional<ItemStack> itemStackOpt = ItemStack.parse(player.level().registryAccess(), itemTag);
                    itemStackOpt.ifPresent(playerLoot::add);
                }
            }
            ModConstants.SERVER_LOOT.put(player.getUUID(), playerLoot);
        }
    }

    /**
     * Saves the loot of all players on the server.
     *
     * @param server the Minecraft server containing the players
     */
    public static void saveAllPlayersLoot(MinecraftServer server) {
        List<ServerPlayer> serverPlayers = server.getPlayerList().getPlayers();
        for (Player player : serverPlayers) {
            savePlayerLoot(player);
        }
    }
}
