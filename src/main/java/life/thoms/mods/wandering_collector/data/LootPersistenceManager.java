/*
 * Copyright 2024 ThomasMTT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * you may obtain a copy of the License at
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
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Manages the persistence of player loot in the Wandering Collector mod.
 */
public class LootPersistenceManager {

    /**
     * Saves the loot of a specified player to persistent data.
     *
     * @param player The player whose loot will be saved.
     */
    public static void savePlayerLoot(PlayerEntity player) {
        CompoundNBT playerPersistentData = player.getPersistentData();
        CompoundNBT playerLootData = new CompoundNBT();
        CompoundNBT lootData = new CompoundNBT();

        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());

        for (ItemStack stack : playerLoot) {
            UUID itemStackUUID = CustomLootDataUtil.getStackUniqueIdentifier(stack);
            if (itemStackUUID != null) {
                lootData.put(itemStackUUID.toString(), stack.save(new CompoundNBT()));
            }
        }
        playerLootData.put(player.getStringUUID(), lootData);
        playerPersistentData.put(ModConstants.PLAYER_LOOT_KEY, playerLootData);
    }

    /**
     * Loads the loot of a specified player from persistent data.
     *
     * @param player The player whose loot will be loaded.
     */
    public static void loadPlayerLoot(ServerPlayerEntity player) {
        CompoundNBT playerData = player.getPersistentData();

        if (playerData.contains(ModConstants.PLAYER_LOOT_KEY)) {
            CompoundNBT currentPlayerLoot = playerData
                    .getCompound(ModConstants.PLAYER_LOOT_KEY)
                    .getCompound(player.getStringUUID());

            List<ItemStack> playerLoot = new ArrayList<>();

            for (String key : currentPlayerLoot.getAllKeys()) {
                CompoundNBT itemTag = currentPlayerLoot.getCompound(key);
                if (!itemTag.isEmpty()) {
                    ItemStack itemStack = ItemStack.of(itemTag);
                    playerLoot.add(itemStack);
                }
            }
            ModConstants.SERVER_LOOT.put(player.getUUID(), playerLoot);
        }
    }

    /**
     * Saves the loot of all players on the specified server.
     *
     * @param server The server whose players' loot will be saved.
     */
    public static void saveAllPlayersLoot(MinecraftServer server) {
        List<ServerPlayerEntity> serverPlayers = server.getPlayerList().getPlayers();
        for (PlayerEntity player : serverPlayers) {
            savePlayerLoot(player);
        }
    }

}
