/*
 * Copyrights 2024 Playtika Ltd., all rights reserved to Playtika Ltd.
 * privacy+e17bb14d-edc1-4d26-930d-486fcc1ab8fe@playtika.com
 */

package org.art.vertex.infrastructure.util;

import lombok.experimental.UtilityClass;
import org.art.vertex.infrastructure.shared.BaseEntity;

import java.util.Collection;
import java.util.Objects;

/**
 * Collection updater utility class.
 */
@UtilityClass
public class CollectionUpdaterUtil {

    /**
     * Updates the existing collection by the new collection in the following way:
     * 1. Removes the existing collection elements that are not present in the new collection.
     * 2. Adds new collection elements to the existing collection, which are not present in the existing collection.
     * 3. Leaves the existing collection elements that are present in the new collection.
     * </p>
     * <b>Note that elements equality is defined by equals/hashcode, so should be implemented carefully</b>.
     * </p>
     * The utility method should be used to update persistent collections handled by Hibernate to avoid persistence issues.
     *
     * @param existingCollection existing collection to update
     * @param newCollection      new collection
     * @param <T>                collection element type
     */
    @SuppressWarnings("java:S2175")
    public static <T extends BaseEntity> void updateCollection(Collection<T> existingCollection, Collection<T> newCollection) {
        Objects.requireNonNull(existingCollection, "Existing collection should not be null");
        Objects.requireNonNull(newCollection, "New collection should not be null");

        // Retaining in the existing collection only the elements, which present in both collections
        existingCollection.retainAll(newCollection);

        // Retaining in the new collection only the new elements that should be added to the existing collection
        newCollection.removeAll(existingCollection);

        // Adding new elements to the existing collection
        existingCollection.addAll(newCollection);
    }

}
