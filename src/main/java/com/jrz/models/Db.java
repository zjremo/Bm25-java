package com.jrz.models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data 
@AllArgsConstructor 
public class Db {
    private ForwardIndex forwardIndex;
    private InvertedIndex invertedIndex;

    public List<Document> getAllDocs() {
        return forwardIndex.getAllDocs();
    }
}
