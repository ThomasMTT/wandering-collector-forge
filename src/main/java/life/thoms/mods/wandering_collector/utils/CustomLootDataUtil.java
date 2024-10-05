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

package life.thoms.mods.wandering_collector.utils;

import life.thoms.mods.wandering_collector.constants.ModConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class CustomLootDataUtil {

    /**
     * Retrieves the unique identifier for a given ItemStack.
     *
     * @param stack The ItemStack from which to retrieve the unique identifier.
     * @return The unique identifier as a UUID, or null if it does not exist.
     */
    public static UUID getStackUniqueIdentifier(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_UUID)) {
            return tag.getUUID(ModConstants.STACK_UUID);
        }
        return null;
    }

    /**
     * Retrieves the expiration time for a given ItemStack.
     *
     * @param stack The ItemStack from which to retrieve the expiration time.
     * @return The expiration time as a Long, or null if it does not exist.
     */
    public static Long getStackExpirationTime(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_DISPOSAL_TIME)) {
            return tag.getLong(ModConstants.STACK_DISPOSAL_TIME);
        }
        return null;
    }

    /**
     * Adds custom data to a given ItemStack, including a unique identifier and disposal time.
     *
     * @param stack The ItemStack to which custom data will be added.
     * @param gameTime The current game time used for the disposal time.
     * @return The updated ItemStack with the added custom data.
     */
    public static ItemStack addStackCustomData(ItemStack stack, Long gameTime) {
        CompoundTag stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_UUID, UUID.randomUUID());
        stackDataTag.putLong(ModConstants.STACK_DISPOSAL_TIME, gameTime);
        return stack;
    }

    /**
     * Retrieves the owner of a given ItemStack.
     *
     * @param stack The ItemStack from which to retrieve the owner.
     * @return The owner as a UUID, or null if it does not exist.
     */
    public static UUID getStackOwner(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(ModConstants.STACK_OWNER)) {
            return tag.getUUID(ModConstants.STACK_OWNER);
        }
        return null;
    }

    /**
     * Sets the owner of a given ItemStack.
     *
     * @param stack The ItemStack for which to set the owner.
     * @param stackOwner The UUID of the owner to be set.
     */
    public static void setStackOwner(ItemStack stack, UUID stackOwner) {
        CompoundTag stackDataTag = stack.getOrCreateTag();
        stackDataTag.putUUID(ModConstants.STACK_OWNER, stackOwner);
    }

    /**
     * Retrieves an ItemStack from a list based on its unique identifier.
     *
     * @param stackUniqueIdentifier The unique identifier of the ItemStack.
     * @param stackList The list of ItemStacks to search.
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
     * Checks if a unique identifier is present in a list of ItemStacks.
     *
     * @param stackUniqueIdentifier The unique identifier to search for.
     * @param stackList The list of ItemStacks to check.
     * @return True if the identifier is found, false otherwise.
     */
    public static boolean isUniqueIdentifierInList(UUID stackUniqueIdentifier, List<ItemStack> stackList) {
        return getStackFromList(stackUniqueIdentifier, stackList) != null;
    }
}
