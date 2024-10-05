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

package life.thoms.mods.wandering_collector;

import life.thoms.mods.wandering_collector.config.WanderingCollectorConfig;
import life.thoms.mods.wandering_collector.constants.ModConstants;
import life.thoms.mods.wandering_collector.events.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

/**
 * Main class for the Wandering Collector mod.
 * This class is responsible for initializing the mod and registering events.
 */
@Mod(ModConstants.MOD_ID)
public class WanderingCollector {

    /**
     * Constructor for the WanderingCollector class.
     * It registers the mod's configuration and events.
     */
    public WanderingCollector() {
        IEventBus modEventBus = MinecraftForge.EVENT_BUS;
        registerEvents(modEventBus);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, WanderingCollectorConfig.SPEC, ModConstants.MOD_ID + ".toml");
    }

    /**
     * Registers the event handlers for the mod.
     *
     * @param modEventBus The event bus to register handlers to.
     */
    private void registerEvents(IEventBus modEventBus) {
        modEventBus.register(PlayerSessionEvents.class);
        modEventBus.register(ServerSessionEvents.class);
        modEventBus.register(ItemDespawnEvents.class);
        modEventBus.register(PlayerEvents.class);
        modEventBus.register(WanderingTraderEvents.class);
    }
}