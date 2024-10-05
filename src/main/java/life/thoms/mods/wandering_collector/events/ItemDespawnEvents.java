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
import life.thoms.mods.wandering_collector.helpers.PlayerLootDataHelper;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import life.thoms.mods.wandering_collector.utils.ItemEntityUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to item entities despawning in the Wandering Collector mod.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemDespawnEvents {

    /**
     * Event handler for when an entity leaves the level.
     * <p>Checks if an item entity has despawned and updates player loot accordingly.</p>
     *
     * @param event the entity leave level event
     */
    @SubscribeEvent
    public static void onEntityLeaveLevel(EntityLeaveLevelEvent event) {
        if (!event.getLevel().isClientSide()) {
            if (event.getEntity() instanceof ItemEntity itemEntity) {

                if (ItemEntityUtil.itemEntityHasDespawned(itemEntity, event.getLevel().getMinBuildHeight())) {
                    ItemStack eventStack = itemEntity.getItem();
                    Entity owner = itemEntity.getOwner();
                    if (owner == null) {
                        UUID ownerUniqueIdentifier = CustomLootDataUtil.getStackOwner(eventStack);
                        if (ownerUniqueIdentifier != null) {
                            owner = event.getLevel().getPlayerByUUID(ownerUniqueIdentifier);
                        }
                    }
                    if (owner instanceof Player player) {
                        long gameTime = itemEntity.level().getGameTime();
                        List<ItemStack> currentPlayerLoot = ModConstants.SERVER_LOOT.getOrDefault(owner.getUUID(), new ArrayList<>());
                        if (eventStack.isStackable()) {
                            PlayerLootDataHelper.handleStackableLoot(currentPlayerLoot, eventStack, player);
                        } else {
                            ItemStack stack = CustomLootDataUtil.addStackCustomData(eventStack, gameTime);
                            PlayerLootDataHelper.addNewItemToPlayerLoot(player, stack);
                        }
                    }
                }
            }
        }
    }
}
