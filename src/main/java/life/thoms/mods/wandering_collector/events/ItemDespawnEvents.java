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

package life.thoms.mods.wandering_collector.events;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.helpers.PlayerLootDataHelper;
import life.thoms.mods.wandering_collector.utils.CustomLootDataUtil;
import life.thoms.mods.wandering_collector.utils.ItemEntityUtil;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityLeaveWorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Handles events related to item entities despawning in the world.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ItemDespawnEvents {

    /**
     * Called when an entity leaves the world. This method specifically handles
     * item entities and their associated player loot data.
     *
     * @param event The event containing information about the entity leaving the world.
     */
    @SubscribeEvent
    public static void onEntityLeaveWorld(EntityLeaveWorldEvent event) {
        if (!event.getWorld().isClientSide()) {
            if (event.getEntity() instanceof ItemEntity) {
                ItemEntity itemEntity = (ItemEntity) event.getEntity();

                if (ItemEntityUtil.itemEntityHasDespawned(itemEntity, 0)) {
                    ItemStack eventStack = itemEntity.getItem();
                    UUID ownerUniqueIdentifier = itemEntity.getOwner();
                    PlayerEntity owner = null;
                    if (ownerUniqueIdentifier != null) {
                        owner = event.getWorld().getPlayerByUUID(ownerUniqueIdentifier);
                    }
                    if (owner == null) {
                        ownerUniqueIdentifier = itemEntity.getThrower();
                        if (ownerUniqueIdentifier != null) {
                            owner = event.getWorld().getPlayerByUUID(ownerUniqueIdentifier);
                        }
                    }
                    if (owner == null) {
                        ownerUniqueIdentifier = CustomLootDataUtil.getStackOwner(eventStack);
                        if (ownerUniqueIdentifier != null) {
                            owner = event.getWorld().getPlayerByUUID(ownerUniqueIdentifier);
                        }
                    }
                    if (owner instanceof PlayerEntity) {
                        long gameTime = itemEntity.level.getGameTime();
                        List<ItemStack> currentPlayerLoot = ModConstants.SERVER_LOOT.getOrDefault(owner.getUUID(), new ArrayList<>());
                        if (eventStack.isStackable()) {
                            PlayerLootDataHelper.handleStackableLoot(currentPlayerLoot, eventStack, owner);
                        } else {
                            ItemStack stack = CustomLootDataUtil.addStackCustomData(eventStack, gameTime);
                            PlayerLootDataHelper.addNewItemToPlayerLoot(owner, stack);
                        }
                    }
                }
            }
        }
    }
}
