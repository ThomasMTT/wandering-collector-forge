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

package life.thoms.mods.wandering_collector.helpers;

import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * A helper class for managing inventory data related to players.
 */
public class InventoryDataHelper {

    /**
     * Sets the owner of valuable items in the player's inventory to the player's UUID.
     *
     * @param player The player whose inventory items will be processed.
     */
    public static void setOwnerToInventoryItems(PlayerEntity player) {
        PlayerInventory inventory = player.inventory;
        List<ItemStack> itemInInventory = new ArrayList<>(inventory.items);
        itemInInventory.addAll(inventory.armor);
        itemInInventory.add(player.getOffhandItem());

        for (ItemStack stack : itemInInventory) {
            if (ItemValueFilter.filterValuableItems(stack)) {
                CustomLootDataUtil.setStackOwner(stack, player.getUUID());
            }
        }
    }
}
