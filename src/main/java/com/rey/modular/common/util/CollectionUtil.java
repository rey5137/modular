package com.rey.modular.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionUtil {

    public static <T> List<T> combineList(Collection<T>... collections) {
        List<T> result = new ArrayList<>();
        for(Collection<T> collection : collections) {
            result.addAll(collection);
        }
        return result;
    }

    public static <T> Set<T> combineSet(Collection<T>... collections) {
        Set<T> result = new HashSet<>();
        for(Collection<T> collection : collections) {
            result.addAll(collection);
        }
        return result;
    }

    public static <T> Collection<T> addIfMissing(Collection<T> collection, T element) {
        return addIfMissing(collection, List.of(element));
    }

    public static <T> Collection<T> addIfMissing(Collection<T> collection, Collection<T> elements) {
        if(elements.isEmpty()) {
            return collection;
        }
        T[] missingElements = (T[]) elements.stream()
                .filter(e -> !collection.contains(e))
                .toArray();
        if(missingElements.length > 0) {
            Collection<T> result = new ArrayList<>(collection);
            Collections.addAll(result, missingElements);
            return result;
        }
        return collection;
    }

}
