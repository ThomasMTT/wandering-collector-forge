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

package life.thoms.mods.wandering_collector.utils;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.*;

/**
 * Utility class for handling custom data associated with ItemStacks.
 */
public class CustomLootDataUtil {

    /**
     * Retrieves the unique identifier for the given ItemStack.
     *
     * @param stack The ItemStack for which to retrieve the unique identifier.
     * @return The UUID associated with the ItemStack, or null if not found.
     */
    public static UUID getStackUniqueIdentifier(ItemStack stack) {
        DataComponentPatch dataComponentPatch = stack.getComponentsPatch();
        Optional<? extends CustomData> customData = dataComponentPatch.get(DataComponents.CUSTOM_DATA);

        if (customData.isPresent()) {
            CompoundTag dataTag = customData.get().copyTag();
            if (dataTag.contains(ModConstants.STACK_UUID)) {
                return dataTag.getUUID(ModConstants.STACK_UUID);
            }
        }
        return null;
    }

    /**
     * Retrieves the expiration time for the given ItemStack.
     *
     * @param stack The ItemStack for which to retrieve the expiration time.
     * @return The expiration time as a Long, or null if not found.
     */
    public static Long getStackExpirationTime(ItemStack stack) {
        DataComponentPatch dataComponentPatch = stack.getComponentsPatch();
        Optional<? extends CustomData> customData = dataComponentPatch.get(DataComponents.CUSTOM_DATA);

        if (customData.isPresent()) {
            CompoundTag dataTag = customData.get().copyTag();
            return dataTag.getLong(ModConstants.STACK_DISPOSAL_TIME);
        }
        return null;
    }

    /**
     * Adds custom data to the given ItemStack, including a unique identifier and an expiration time.
     *
     * @param stack    The ItemStack to which to add custom data.
     * @param gameTime The current game time to set as the expiration time.
     * @return The ItemStack with the added custom data.
     */
    public static ItemStack addStackCustomData(ItemStack stack, Long gameTime) {
        CompoundTag stackDataTag = new CompoundTag();
        stackDataTag.putUUID(ModConstants.STACK_UUID, UUID.randomUUID());
        stackDataTag.putLong(ModConstants.STACK_DISPOSAL_TIME, gameTime);
        TypedDataComponent<CustomData> comp =
                new TypedDataComponent<>(DataComponents.CUSTOM_DATA, CustomData.of(stackDataTag));
        DataComponentPatch patch = DataComponentPatch.builder().set(comp).build();
        stack.applyComponents(patch);
        return stack;
    }

    /**
     * Retrieves the owner of the given ItemStack.
     *
     * @param stack The ItemStack for which to retrieve the owner.
     * @return The UUID of the owner, or null if not found.
     */
    public static UUID getStackOwner(ItemStack stack) {
        DataComponentPatch dataComponentPatch = stack.getComponentsPatch();
        Optional<? extends CustomData> customData = dataComponentPatch.get(DataComponents.CUSTOM_DATA);

        if (customData.isPresent()) {
            CompoundTag dataTag = customData.get().copyTag();
            if (dataTag.contains(ModConstants.STACK_OWNER)) {
                return dataTag.getUUID(ModConstants.STACK_OWNER);
            }
        }
        return null;
    }

    /**
     * Sets the owner of the given ItemStack to the specified player.
     *
     * @param stack  The ItemStack for which to set the owner.
     * @param player The Player who will be set as the owner.
     */
    public static void setStackOwner(ItemStack stack, Player player) {
        CompoundTag stackDataTag = new CompoundTag();
        stackDataTag.putUUID(ModConstants.STACK_OWNER, player.getUUID());
        TypedDataComponent<CustomData> comp =
                new TypedDataComponent<>(DataComponents.CUSTOM_DATA, CustomData.of(stackDataTag));
        DataComponentPatch patch = DataComponentPatch.builder().set(comp).build();
        stack.applyComponents(patch);
    }

    /**
     * Retrieves the ItemStack from a list based on its unique identifier.
     *
     * @param stackUniqueIdentifier The unique identifier of the ItemStack to retrieve.
     * @param stackList            The list of ItemStacks to search through.
     * @return The matching ItemStack, or null if not found.
     */
    public static ItemStack getStackFromList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        if (stackUniqueIdentifier != null && stackList != null) {
            for (ItemStack stackFromPlayerLoot : stackList) {
                UUID stackUniqueIdentifierFromList = getStackUniqueIdentifier(stackFromPlayerLoot);
                if (stackUniqueIdentifier.equals(stackUniqueIdentifierFromList)) return stackFromPlayerLoot;
            }
        }
        return null;
    }

    /**
     * Checks if the unique identifier exists in the provided list of ItemStacks.
     *
     * @param stackUniqueIdentifier The unique identifier to search for.
     * @param stackList            The list of ItemStacks to check against.
     * @return True if the identifier is found in the list, false otherwise.
     */
    public static boolean isUniqueIdentifierInList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        return getStackFromList(stackUniqueIdentifier, stackList) != null;
    }
}
