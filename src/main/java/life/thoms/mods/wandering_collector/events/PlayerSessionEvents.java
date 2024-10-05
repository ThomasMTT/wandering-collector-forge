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
import life.thoms.mods.wandering_collector.data.LootPersistenceManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles events related to player sessions, including login and logout actions.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PlayerSessionEvents {

    /**
     * Called when a player logs in. This method removes any existing loot data
     * for the player and loads their loot from persistence.
     *
     * @param loginEvent The event containing information about the player login.
     */
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent loginEvent) {
        if (!loginEvent.getPlayer().level.isClientSide()) {
            ModConstants.SERVER_LOOT.remove(loginEvent.getPlayer().getUUID());
            LootPersistenceManager.loadPlayerLoot((ServerPlayerEntity) loginEvent.getPlayer());
        }
    }

    /**
     * Called when a player logs out. This method saves the player's current loot
     * to persistence and removes their loot data from the server.
     *
     * @param logoutEvent The event containing information about the player logout.
     */
    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent logoutEvent) {
        if (!logoutEvent.getPlayer().level.isClientSide()) {
            LootPersistenceManager.savePlayerLoot(logoutEvent.getPlayer());
            ModConstants.SERVER_LOOT.remove(logoutEvent.getPlayer().getUUID());
        }
    }
}
