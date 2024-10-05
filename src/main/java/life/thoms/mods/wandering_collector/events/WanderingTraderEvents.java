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

import net.minecraft.entity.merchant.villager.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to interactions with wandering traders and player container actions.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WanderingTraderEvents {

    /**
     * Called when a player interacts with a wandering trader entity.
     * This method manages custom trades for the trader if they are not currently trading.
     *
     * @param event The event containing information about the player interaction.
     */
    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getTarget() instanceof WanderingTraderEntity) {
                WanderingTraderEntity trader = (WanderingTraderEntity) event.getTarget();
                PlayerEntity player = event.getPlayer();
                if (!trader.isTrading()) {
                    WanderingTraderHelper.manageTraderCustomTrades(player, trader);
                }
            }
        }
    }

    /**
     * Called when a player closes a container. This method checks if the container is a merchant's
     * container and updates the player's loot based on the merchant offers.
     *
     * @param event The event containing information about the player closing the container.
     */
    @SubscribeEvent
    public static void onPlayerContainerEvent(PlayerContainerEvent.Close event) {
        if (!event.getPlayer().level.isClientSide()) {
            PlayerEntity player = event.getPlayer();
            Container container = player.containerMenu;
            if (container instanceof MerchantContainer) {
                MerchantContainer merchantMenu = (MerchantContainer) container;
                MerchantOffers offers = merchantMenu.getOffers();

                for (MerchantOffer offer : offers) {
                    if (offer != null) {
                        if (offer.isOutOfStock()) {
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
}
