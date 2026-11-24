package com.jrz.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data 
public class ForwardIndex {
    // ind -> content
    private Map<Integer, Document> indMap;

    public ForwardIndex() {
        indMap = new HashMap<>();
    }

    public void add(int ind, Document doc) {
        indMap.put(ind, doc);
    }

    public void remove(int ind) {
        indMap.remove(ind);
    }

    public List<Document> getAllDocs() {
        return new ArrayList<>(indMap.values());
    }

    public Document getDocument(int ind) {
        return indMap.get(ind);
    }
}
