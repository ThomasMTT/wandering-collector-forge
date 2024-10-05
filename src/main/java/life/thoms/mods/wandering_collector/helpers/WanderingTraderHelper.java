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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.*;

/**
 * Helper class for managing trades with wandering traders.
 */
public class WanderingTraderHelper {

    private static final Random random = new Random();
    private static final int probability = WanderingCollectorConfig.PROBABILITY_OF_LOST_ITEM_IN_TRADE.get();

    /**
     * Manages custom trades for a wandering trader based on player interaction.
     *
     * @param player the player interacting with the trader
     * @param trader the wandering trader
     */
    public static void manageTraderCustomTrades(Player player, WanderingTrader trader) {
        UUID playerUUID = player.getUUID();
        MerchantOffers offers = getMerchantOffers(trader);

        if (hasPlayerAlreadyInteracted(playerUUID, trader)) {
            Map<Tag, ItemStack> tradeItems = getTradeLostItemsFromNbt(player, trader);
            List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());
            for (Tag key : tradeItems.keySet()) {
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
     * Retrieves the merchant offers for a wandering trader.
     *
     * @param trader the wandering trader
     * @return the merchant offers
     */
    private static MerchantOffers getMerchantOffers(WanderingTrader trader) {
        MerchantOffers offers = trader.getOffers();

        short offersSize;
        CompoundTag persistentData = trader.getPersistentData();
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
     * @param playerUUID the UUID of the player
     * @param trader the wandering trader
     * @return true if the player has interacted, false otherwise
     */
    private static boolean hasPlayerAlreadyInteracted(UUID playerUUID, WanderingTrader trader) {
        CompoundTag traderPersistentData = trader.getPersistentData();
        CompoundTag playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);

        return !playerTrades.isEmpty() && playerTrades.contains(playerUUID.toString());
    }

    /**
     * Retrieves lost items for trade from the trader's NBT data.
     *
     * @param player the player interacting with the trader
     * @param trader the wandering trader
     * @return a map of lost items available for trade
     */
    private static Map<Tag, ItemStack> getTradeLostItemsFromNbt(Player player, WanderingTrader trader) {
        Map<Tag, ItemStack> stackMap = new HashMap<>();
        CompoundTag traderPersistentData = trader.getPersistentData();
        CompoundTag playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);
        if (playerTrades.isEmpty()) {
            return stackMap;
        }
        CompoundTag playerTradeItems = playerTrades.getCompound(player.getStringUUID());
        if (playerTradeItems.isEmpty()) {
            return stackMap;
        }

        for (String key : playerTradeItems.getAllKeys()) {
            CompoundTag stackTag = playerTradeItems.getCompound(key);
            if (!stackTag.isEmpty()) {
                ItemStack stack = ItemStack.of(stackTag);
                stackMap.put(stackTag, stack);
            }
        }

        return stackMap;
    }

    /**
     * Generates lost item trades for the player with the wandering trader.
     *
     * @param player the player interacting with the trader
     * @param trader the wandering trader
     */
    private static void generateLostItemTrades(Player player, WanderingTrader trader) {
        UUID playerUUID = player.getUUID();
        CompoundTag traderPersistentData = trader.getPersistentData();
        MerchantOffers offers = trader.getOffers();
        CompoundTag playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);
        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(playerUUID, new ArrayList<>());
        CompoundTag stackTags = new CompoundTag();

        if (playerLoot != null) {
            removeExpiredItems(playerLoot, player.level.getGameTime());
            if (!playerLoot.isEmpty()) {
                for (ItemStack stack : playerLoot) {
                    UUID uuid = CustomLootDataUtil.getStackUniqueIdentifier(stack);
                    if (uuid != null) {
                        if (random.nextInt(100) < probability) {
                            offers.add(generateCustomMerchantOffer(stack));
                            CompoundTag stackTag = stack.save(new CompoundTag());
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
     * Generates a custom merchant offer based on an item stack.
     *
     * @param stack the item stack to generate an offer for
     * @return the generated merchant offer
     */
    private static MerchantOffer generateCustomMerchantOffer(ItemStack stack) {
        int price = StackPriceCalculator.getStackPrice(stack);
        int emeraldBlocks = price > 64 ? Math.floorDiv(price, 9) : 0;
        int emeralds = price > 64 ? price % 9 : price;

        if (emeraldBlocks > 64) {
            emeralds += (emeraldBlocks - 64) * 9;
            emeraldBlocks = 64;
            emeralds = Math.min(emeralds, 64);
        }

        ItemStack costItem = emeraldBlocks > 0
                ? new ItemStack(Items.EMERALD_BLOCK, emeraldBlocks)
                : new ItemStack(Items.EMERALD, emeralds);

        ItemStack secondaryCost = (emeraldBlocks > 0 && emeralds > 0)
                ? new ItemStack(Items.EMERALD, emeralds)
                : ItemStack.EMPTY;

        ItemStack offerStack = stack.isStackable() ? new ItemStack(stack.getItem(), stack.getCount()) : stack;

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
     * Removes expired items from a list based on game time.
     *
     * @param list the list of item stacks
     * @param gameTime the current game time
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
