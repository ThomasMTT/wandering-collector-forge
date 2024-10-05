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

import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to the Wandering Trader interactions and trade management.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WanderingTraderEvents {

    /**
     * Manages interactions between players and Wandering Traders.
     * Triggers custom trade management when the player interacts with a trader.
     *
     * @param event The event containing interaction details.
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getTarget() instanceof WanderingTrader trader) {
                Player player = event.getPlayer();
                if (!trader.isTrading()) {
                    WanderingTraderHelper.manageTraderCustomTrades(player, trader);
                }
            }
        }
    }

    /**
     * Handles player container events when a player closes a trading menu.
     * Updates the player's loot based on the merchant offers.
     *
     * @param event The event containing details about the player closing the container.
     */
    @SubscribeEvent
    public static void onPlayerContainerEvent(PlayerContainerEvent.Close event) {
        if (!event.getPlayer().level.isClientSide()) {
            Player player = event.getPlayer();
            AbstractContainerMenu container = player.containerMenu;
            if (container instanceof MerchantMenu merchantMenu) {
                MerchantOffers offers = merchantMenu.getOffers();

                for (MerchantOffer offer : offers) {
                    if (offer != null) {
                        ItemStack resultStack = offer.getResult();
                        List<ItemStack> playerLoot = ModConstants.SERVER_LOOT
                                .getOrDefault(player.getUUID(), new ArrayList<>());
                        UUID uuid = CustomLootDataUtil.getStackUniqueIdentifier(resultStack);
                        if (CustomLootDataUtil.isUniqueIdentifierInList(uuid, playerLoot)) {
                            ItemStack stack = CustomLootDataUtil.getStackFromList(uuid, playerLoot);
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
    }
}
