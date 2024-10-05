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
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles server session events, particularly the server stopping event,
 * to manage the persistence of player loot.
 */
@Mod.EventBusSubscriber(modid = ModConstants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ServerSessionEvents {

    /**
     * Called when the server is stopping. It saves all players' loot
     * to ensure data persistence.
     *
     * @param serverStoppingEvent The event triggered when the server is stopping.
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent serverStoppingEvent) {
        MinecraftServer server = serverStoppingEvent.getServer();
        LootPersistenceManager.saveAllPlayersLoot(server);
    }
}
