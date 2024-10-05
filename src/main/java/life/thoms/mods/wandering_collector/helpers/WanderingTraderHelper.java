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

package life.thoms.mods.wandering_collector.helpers;

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

import java.util.*;

/**
 * Helper class for managing trades with Wandering Traders.
 */
public class WanderingTraderHelper {

    private static final Random random = new Random();
    private static final double probability = WanderingCollectorConfig.PROBABILITY_OF_LOST_ITEM_IN_TRADE.get();

    /**
     * Manages custom trades for a Wandering Trader based on the player's inventory.
     *
     * @param player The player interacting with the trader.
     * @param trader The Wandering Trader being interacted with.
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
     * Retrieves the MerchantOffers for the given Wandering Trader.
     *
     * @param trader The Wandering Trader whose offers are to be retrieved.
     * @return The MerchantOffers associated with the trader.
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
     * @param playerUUID The UUID of the player.
     * @param trader     The Wandering Trader to check against.
     * @return True if the player has interacted with the trader, false otherwise.
     */
    private static boolean hasPlayerAlreadyInteracted(UUID playerUUID, WanderingTrader trader) {
        CompoundTag traderPersistentData = trader.getPersistentData();
        CompoundTag playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);

        return !playerTrades.isEmpty() && playerTrades.contains(playerUUID.toString());
    }

    /**
     * Retrieves trade items lost by the player from the trader's persistent NBT data.
     *
     * @param player The player whose lost items are to be retrieved.
     * @param trader The Wandering Trader whose data is being accessed.
     * @return A map of lost trade items, where each entry consists of the item's NBT tag and the corresponding ItemStack.
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

        HolderLookup.Provider provider = player.level().registryAccess();
        for (String key : playerTradeItems.getAllKeys()) {
            Tag stackTag = playerTradeItems.get(key);
            if (stackTag != null) {
                Optional<ItemStack> stackOpt = ItemStack.parse(provider, stackTag);
                stackOpt.ifPresent(stack -> stackMap.put(stackTag, stack));
            }
        }

        return stackMap;
    }

    /**
     * Generates lost item trades for the player based on their inventory.
     *
     * @param player The player for whom lost item trades are to be generated.
     * @param trader The Wandering Trader for which trades are generated.
     */
    private static void generateLostItemTrades(Player player, WanderingTrader trader) {
        UUID playerUUID = player.getUUID();
        CompoundTag traderPersistentData = trader.getPersistentData();
        MerchantOffers offers = trader.getOffers();
        CompoundTag playerTrades = traderPersistentData.getCompound(ModConstants.TRADER_PLAYER_TRADES);
        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(playerUUID, new ArrayList<>());
        HolderLookup.Provider provider = player.level().registryAccess();
        CompoundTag stackTags = new CompoundTag();

        if (playerLoot != null) {
            removeExpiredItems(playerLoot, player.level().getGameTime());
            if (!playerLoot.isEmpty()) {
                for (ItemStack stack : playerLoot) {
                    UUID uuid = CustomLootDataUtil.getStackUniqueIdentifier(stack);
                    if (uuid != null && random.nextInt(100) < probability) {
                        offers.add(generateCustomMerchantOffer(stack));
                        Tag stackTag = stack.save(provider);
                        stackTags.put(uuid.toString(), stackTag);
                    }
                }
            }
        }
        playerTrades.put(playerUUID.toString(), stackTags);
        traderPersistentData.put(ModConstants.TRADER_PLAYER_TRADES, playerTrades);
    }

    /**
     * Generates a custom MerchantOffer based on the given ItemStack.
     *
     * @param stack The ItemStack for which to generate the offer.
     * @return A MerchantOffer created based on the stack's value.
     */
    private static MerchantOffer generateCustomMerchantOffer(ItemStack stack) {
        int price = StackPriceCalculator.getStackPrice(stack);
        int emeraldBlocks = price > 64 ? Math.floorDiv(price, 9) : 0;
        int emeralds = price > 64 ? price % 9 : price;

        if (emeraldBlocks > 64) {
            emeralds += ((emeraldBlocks - 64) * 9);
            emeraldBlocks = 64;
            emeralds = Math.min(emeralds, 64);
        }

        ItemStack offerStack = stack.isStackable() ? new ItemStack(stack.getItem(), stack.getCount()) : stack;

        return new MerchantOffer(
                new ItemCost(emeraldBlocks > 0 ? Items.EMERALD_BLOCK : Items.EMERALD, emeraldBlocks > 0 ? emeraldBlocks : emeralds),
                emeraldBlocks > 0 && emeralds > 0 ? Optional.of(new ItemCost(Items.EMERALD, emeralds)) : Optional.empty(),
                offerStack,
                1,
                stack.getCount(),
                0.05F
        );
    }

    /**
     * Removes expired items from the player's loot list.
     *
     * @param list     The list of ItemStacks to check for expiration.
     * @param gameTime The current game time in ticks.
     */
    private static void removeExpiredItems(List<ItemStack> list, Long gameTime) {
        Iterator<ItemStack> iterator = list.iterator();

        while (iterator.hasNext()) {
            ItemStack stack = iterator.next();
            Long expirationTime = CustomLootDataUtil.getStackExpirationTime(stack);
            Long itemDisposalTime = WanderingCollectorConfig.ITEM_DISPOSAL_TIME.get();
            if (expirationTime != null && (gameTime > (expirationTime + itemDisposalTime) || itemDisposalTime == 0)) {
                iterator.remove();
            }
        }
    }
}
