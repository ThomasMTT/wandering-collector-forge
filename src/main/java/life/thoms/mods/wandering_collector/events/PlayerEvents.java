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

import it.unimi.dsi.fastutil.longs.LongSet;
import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.helpers.InventoryDataHelper;
import life.thoms.mods.wandering_collector.helpers.TraderSummoningHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.VillageFeature;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

/**
 * Handles player-related events such as player death and interaction with blocks.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerEvents {

    /**
     * Called when a player dies. This event sets the owner of inventory items to the player.
     *
     * @param playerDeathEvent the event triggered on player death
     */
    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent playerDeathEvent) {
        if (!playerDeathEvent.getEntity().level.isClientSide()) {
            if (playerDeathEvent.getEntity() instanceof Player player) {
                InventoryDataHelper.setOwnerToInventoryItems(player);
            }
        }
    }

    /**
     * Called when a player right-clicks a block. This event checks if the player is interacting with a bell
     * while holding an emerald, and summons a trader if conditions are met.
     *
     * @param rightClickBlockEvent the event triggered on player right-clicking a block
     */
    @SubscribeEvent
    public static void onPlayerRightClickBlock(PlayerInteractEvent.RightClickBlock rightClickBlockEvent) {
        if (!rightClickBlockEvent.getWorld().isClientSide()) {
            if (rightClickBlockEvent.getWorld().getBlockEntity(rightClickBlockEvent.getHitVec().getBlockPos()) instanceof BellBlockEntity) {
                Player player = rightClickBlockEvent.getPlayer();
                if (player.getItemInHand(player.getUsedItemHand()).getItem() == Items.EMERALD) {
                    BlockPos pos = rightClickBlockEvent.getPos();
                    Level level = rightClickBlockEvent.getWorld();

                    BlockState bellState = level.getBlockState(pos);
                    Direction bellDirection = bellState.getValue(BellBlock.FACING);
                    Direction bellHitDirection = rightClickBlockEvent.getHitVec().getDirection();
                    boolean bellWillRing = false;

                    // Determine if the bell will ring based on its state and direction
                    switch (bellState.getValue(BellBlock.ATTACHMENT)) {
                        case FLOOR -> {
                            if (bellDirection == bellHitDirection || bellDirection == bellHitDirection.getOpposite()) {
                                bellWillRing = true;
                            }
                        }
                        case CEILING -> {
                            if (bellDirection != Direction.UP && bellDirection != Direction.DOWN) {
                                bellWillRing = true;
                            }
                        }
                        default -> {
                            if (bellDirection.getClockWise() == bellHitDirection ||
                                    bellDirection.getCounterClockWise() == bellHitDirection) {
                                bellWillRing = true;
                            }
                        }
                    }

                    if (bellWillRing) {
                        Map<ConfiguredStructureFeature<?, ?>, LongSet> structureMap = rightClickBlockEvent.getWorld().getChunk(pos).getAllReferences();
                        for (ConfiguredStructureFeature<?, ?> structure : structureMap.keySet()) {
                            if (structure.feature instanceof VillageFeature villageFeature) {
                                if (structureMap.get(structure).toLongArray().length > 0) {
                                    Long villageId = structureMap.get(structure).toLongArray()[0];
                                    TraderSummoningHelper.summonTrader(villageId, pos, level, rightClickBlockEvent.getWorld().getGameTime(), player);
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
