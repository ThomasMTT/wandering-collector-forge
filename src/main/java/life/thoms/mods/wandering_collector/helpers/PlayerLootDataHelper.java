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

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Helper class for managing player loot data and handling interactions
 * with item stacks.
 */
public class PlayerLootDataHelper {

    /**
     * Handles adding stackable loot to the player's loot list.
     *
     * @param currentPlayerLoot the current list of the player's loot
     * @param eventStack the ItemStack that was triggered in the event
     * @param player the player involved in the event
     */
    public static void handleStackableLoot(List<ItemStack> currentPlayerLoot, ItemStack eventStack, Player player) {
        if (eventStack.getCount() <= 0) return;

        int maxPrice = 640;
        int price = StackPriceCalculator.getStackPrice(eventStack);
        int stackSize = eventStack.getCount();
        int pricePerItem = price / stackSize;

        for (ItemStack stackFromList : currentPlayerLoot) {
            if (stackFromList.getItem().equals(eventStack.getItem())) {
                int priceFromList = StackPriceCalculator.getStackPrice(stackFromList);
                int stackSizeFromList = stackFromList.getCount();
                int maxStackSize = stackFromList.getMaxStackSize();

                if (stackSizeFromList >= maxStackSize) continue;

                if (maxStackSize >= stackSize + stackSizeFromList && maxPrice >= price + priceFromList) {
                    stackFromList.setCount(stackSizeFromList + stackSize);
                    return;
                } else {
                    int allowedAmountBySize = maxStackSize - stackSizeFromList;
                    int allowedAmountByPrice = (pricePerItem > 0)
                            ? (maxPrice - priceFromList) / pricePerItem
                            : 0;
                    int allowedAmount = Math.min(allowedAmountBySize, allowedAmountByPrice);
                    int additionAmount = Math.min(stackSize, allowedAmount);

                    stackFromList.setCount(stackSizeFromList + additionAmount);
                    eventStack.setCount(stackSize - additionAmount);

                    if (eventStack.getCount() <= 0) {
                        return;
                    }
                }
            }
        }

        int remainingStackSize = eventStack.getCount();
        while (remainingStackSize > 0) {
            int remainingPrice = StackPriceCalculator.getStackPrice(eventStack);
            int pricePerItemRemaining = remainingPrice / remainingStackSize;

            int allowedAmountByPrice = (maxPrice / pricePerItemRemaining);
            int amountToAdd = Math.min(remainingStackSize, allowedAmountByPrice);

            ItemStack newStack = eventStack.copy();
            newStack.setCount(amountToAdd);

            ItemStack stackWithCustomData = CustomLootDataUtil.addStackCustomData(newStack, player.level.getGameTime());
            addNewItemToPlayerLoot(player, stackWithCustomData);

            remainingStackSize -= amountToAdd;
            eventStack.setCount(remainingStackSize);
        }
    }

    /**
     * Adds a new item to the player's loot list if it is considered valuable.
     *
     * @param player the player whose loot is being modified
     * @param stack the ItemStack to add to the player's loot
     */
    public static void addNewItemToPlayerLoot(Player player, ItemStack stack) {
        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());
        UUID stackUniqueIdentifier = CustomLootDataUtil.getStackUniqueIdentifier(stack);

        if (ItemValueFilter.filterValuableItems(stack)) {
            if (CustomLootDataUtil.isUniqueIdentifierInList(stackUniqueIdentifier, playerLoot)) {
                return;
            }

            playerLoot.add(stack);
            ModConstants.SERVER_LOOT.put(player.getUUID(), playerLoot);
        }
    }
}
