package com.jrz;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jrz.Bm25.Result;
import com.jrz.models.Db;
import com.jrz.models.Document;

import lombok.extern.slf4j.Slf4j;

@Slf4j (topic = "c.AppTest")
public class Bm25Test {
    @Test 
    public void testBm25() {
        List<Document> docs = new ArrayList<>(){{
            add(new Document(1, "java programming language"));
            add(new Document(2, "java java programming"));
            add(new Document(3, "go programming language"));
            add(new Document(4, "python programming language"));
            add(new Document(5, "java virtual programming"));
        }};
        Db db = Bm25.storeDocuments(docs);
        List<Result> results = Bm25.processBM25("java programming", db);
        log.info("results: {}", results);
    }
}