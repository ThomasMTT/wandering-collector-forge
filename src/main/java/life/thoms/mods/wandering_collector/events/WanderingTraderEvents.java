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

package life.thoms.mods.wandering_collector.events;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.helpers.WanderingTraderHelper;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;

import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.TradeWithVillagerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to the Wandering Trader, including player interactions
 * and trades with the trader.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WanderingTraderEvents {

    /**
     * Called when a player interacts with a Wandering Trader. If the trader
     * is not currently trading, it manages custom trades for the player.
     *
     * @param event The event triggered when a player interacts with an entity.
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getTarget() instanceof WanderingTrader trader) {
                Player player = event.getEntity();
                if (!trader.isTrading()) {
                    WanderingTraderHelper.manageTraderCustomTrades(player, trader);
                }
            }
        }
    }

    /**
     * Called when a player trades with a villager. If the villager is a
     * Wandering Trader, it manages the player's loot based on the trade.
     *
     * @param event The event triggered when a player trades with a villager.
     */
    @SubscribeEvent
    public static void onTradeWithVillager(TradeWithVillagerEvent event) {
        if (!event.getEntity().level().isClientSide()) {
            Player player = event.getEntity();
            AbstractVillager villager = event.getAbstractVillager();
            if (villager instanceof WanderingTrader) {
                MerchantOffer offer = event.getMerchantOffer();
                ItemStack resultStack = offer.getResult();
                List<ItemStack> playerLoot = ModConstants.SERVER_LOOT.getOrDefault(player.getUUID(), new ArrayList<>());
                UUID stackUniqueIdentifier = CustomLootDataUtil.getStackUniqueIdentifier(resultStack);
                if (CustomLootDataUtil.isUniqueIdentifierInList(stackUniqueIdentifier, playerLoot)) {
                    ItemStack stack = CustomLootDataUtil.getStackFromList(stackUniqueIdentifier, playerLoot);
                    playerLoot.remove(stack);
                } else {
                    if (!resultStack.isStackable()) return;

                    for (ItemStack stackFromList : playerLoot) {
                        if (stackFromList.getItem().equals(resultStack.getItem()) &&
                                stackFromList.getCount() == resultStack.getCount()) {
                            playerLoot.remove(stackFromList);
                            break;
                        }
                    }
                }
            }
        }
    }
}
