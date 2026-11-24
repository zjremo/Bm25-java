package com.jrz.models;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InvertedIndex {
    private Map<String, Set<Integer>> termMap;

    public InvertedIndex() {
        this.termMap = new HashMap<>();
    }

    public void addBatch(String term, Collection<Integer> docIds) {
        for (int ind : docIds) 
            termMap.computeIfAbsent(term, k -> new HashSet<>())
                .add(ind);
    }

    public void add(String term, int ind) {
        termMap.computeIfAbsent(term, k -> new HashSet<>())
            .add(ind);
    }

    public void remove(String term) {
        termMap.remove(term);
    }

    public Set<Integer> getDocs(String term) {
        Set<Integer> docIds = new HashSet<>(termMap.getOrDefault(term, Collections.emptySet()));
        return docIds;
    }
}
