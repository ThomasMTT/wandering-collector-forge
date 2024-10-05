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

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;

import java.util.*;

/**
 * Helper class for managing custom trades with wandering traders.
 */
public class WanderingTraderHelper {

    private static final Random random = new Random();
    private static final int probability = WanderingCollectorConfig.PROBABILITY_OF_LOST_ITEM_IN_TRADE.get();

    /**
     * Manages the custom trades available for the given player and trader.
     *
     * @param player The player interacting with the trader.
     * @param trader The wandering trader entity.
     */
    public static void manageTraderCustomTrades(PlayerEntity player, WanderingTraderEntity trader) {
        UUID playerUUID = player.getUUID();
        MerchantOffers offers = getMerchantOffers(trader);

        if (hasPlayerAlreadyInteracted(playerUUID, trader)) {
            Map<CompoundNBT, ItemStack> tradeItems = getTradeLostItemsFromNbt(player, trader);
            List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());
            for (CompoundNBT key : tradeItems.keySet()) {
                ItemStack stack = tradeItems.get(key);
                UUID stackUniqueIdentifier = CustomLootDataUtil.getStackUniqueIdentifier(stack);
                if (CustomLootDataUtil.isUniqueIdentifierInList(stackUniqueIdentifier, playerLoot)) {
                    offers.add(generateCustomMerchantOffer(stack));
                }
            }
        } else {
            generateLostItemTrades(player, trader);
        }
    }

    /**
     * Retrieves the merchant offers for the given trader.
     *
     * @param trader The wandering trader entity.
     * @return The MerchantOffers associated with the trader.
     */
    private static MerchantOffers getMerchantOffers(WanderingTraderEntity trader) {
        MerchantOffers offers = trader.getOffers();

        short offersSize;
        CompoundNBT persistentData = trader.getPersistentData();
        if (persistentData.contains(ModConstants.TRADER_DEFAULT_TRADE_COUNT)) {
            offersSize = persistentData.getShort(ModConstants.TRADER_DEFAULT_TRADE_COUNT);
        } else {
            offersSize = (short) offers.size();
            persistentData.putShort(ModConstants.TRADER_DEFAULT_TRADE_COUNT, offersSize);
        }

        while (offers.size() > offersSize) {
            offers.remove(offersSize);
        }
        return offers;
    }

    /**
     * Checks if the player has already interacted with the trader.
     *
     * @param playerUUID The UUID of the player.
     * @param trader The wandering trader entity.
     * @return True if the player has interacted, false otherwise.
     */
    private static boolean hasPlayerAlreadyInteracted(UUID playerUUID, WanderingTraderEntity trader) {
        CompoundNBT traderPersistentData = trader.getPersistentData();
        CompoundNBT playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);

        if (!playerTrades.isEmpty()) {
            return playerTrades.contains(playerUUID.toString());
        } else {
            return false;
        }
    }

    /**
     * Retrieves trade items lost by the player from NBT data.
     *
     * @param player The player whose lost items are being retrieved.
     * @param trader The wandering trader entity.
     * @return A map of lost item stacks identified by their NBT.
     */
    private static Map<CompoundNBT, ItemStack> getTradeLostItemsFromNbt(PlayerEntity player, WanderingTraderEntity trader) {
        Map<CompoundNBT, ItemStack> stackMap = new HashMap<>();
        CompoundNBT traderPersistentData = trader.getPersistentData();
        CompoundNBT playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);
        if (playerTrades.isEmpty()) {
            return stackMap;
        }
        CompoundNBT playerTradeItems = playerTrades.getCompound(player.getStringUUID());
        if (playerTradeItems.isEmpty()) {
            return stackMap;
        }

        for (String key : playerTradeItems.getAllKeys()) {
            CompoundNBT stackTag = playerTradeItems.getCompound(key);
            if (!stackTag.isEmpty()) {
                ItemStack stack = ItemStack.of(stackTag);
                stackMap.put(stackTag, stack);
            }
        }

        return stackMap;
    }

    /**
     * Generates lost item trades for the player and trader.
     *
     * @param player The player for whom to generate trades.
     * @param trader The wandering trader entity.
     */
    private static void generateLostItemTrades(PlayerEntity player, WanderingTraderEntity trader) {
        UUID playerUUID = player.getUUID();
        CompoundNBT traderPersistentData = trader.getPersistentData();
        MerchantOffers offers = trader.getOffers();
        CompoundNBT playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);
        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(playerUUID, new ArrayList<>());
        CompoundNBT stackTags = new CompoundNBT();

        if (playerLoot != null) {
            removeExpiredItems(playerLoot, player.level.getGameTime());
            if (!playerLoot.isEmpty()) {
                for (ItemStack stack : playerLoot) {
                    UUID uuid = CustomLootDataUtil.getStackUniqueIdentifier(stack);
                    if (uuid != null) {
                        if (random.nextInt(100) < probability) {
                            offers.add(generateCustomMerchantOffer(stack));
                            CompoundNBT stackTag = stack.save(new CompoundNBT());
                            stackTags.put(uuid.toString(), stackTag);
                        }
                    }
                }
            }
        }
        playerTrades.put(playerUUID.toString(), stackTags);
        traderPersistentData.put(ModConstants.TRADER_PLAYER_TRADES, playerTrades);
    }

    /**
     * Generates a custom merchant offer based on the given ItemStack.
     *
     * @param stack The ItemStack for which to create the offer.
     * @return A MerchantOffer created from the ItemStack.
     */
    private static MerchantOffer generateCustomMerchantOffer(ItemStack stack) {
        int price = StackPriceCalculator.getStackPrice(stack);
        int emeraldBlocks = price > 64 ? Math.floorDiv(price, 9) : 0;
        int emeralds = price > 64 ? price % 9 : price;

        if (emeraldBlocks > 64) {
            emeralds = emeralds + ((emeraldBlocks - 64) * 9);
            emeraldBlocks = 64;
            emeralds = Math.min(emeralds, 64);
        }

        ItemStack costItem = emeraldBlocks > 0
                ? new ItemStack(Items.EMERALD_BLOCK, emeraldBlocks)
                : new ItemStack(Items.EMERALD, emeralds);

        ItemStack secondaryCost = (emeraldBlocks > 0 && emeralds > 0)
                ? new ItemStack(Items.EMERALD, emeralds)
                : ItemStack.EMPTY;

        ItemStack offerStack = stack;

        if (stack.isStackable()) {
            offerStack = new ItemStack(stack.getItem(), stack.getCount());
        }

        return new MerchantOffer(
                costItem,
                secondaryCost,
                offerStack,
                1,
                stack.getCount(),
                0.05F
        );
    }

    /**
     * Removes expired items from the provided list based on the current game time.
     *
     * @param list The list of ItemStacks to clean up.
     * @param gameTime The current game time used to determine expiration.
     */
    private static void removeExpiredItems(List<ItemStack> list, Long gameTime) {
        Iterator<ItemStack> iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            Long expirationTime = CustomLootDataUtil.getStackExpirationTime(stack);
            Long itemDisposalTime = WanderingCollectorConfig.ITEM_DISPOSAL_TIME.get();
            if (expirationTime != null) {
                if (gameTime > (expirationTime + itemDisposalTime) || itemDisposalTime == 0) {
                    iterator.remove();
                }
            }
        }
    }
}
