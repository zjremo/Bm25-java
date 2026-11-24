package com.jrz.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;

@Data 
public class Document {
    private int id;
    private String content;
    // terms 切分得到的keyword
    private List<String> terms;
    // term -> count 统计term在本文档中的出现次数
    private Map<String, Integer> termFreq;

    public Document(int id, String content) {
        this.id = id;
        this.content = content;
        this.terms = new ArrayList<>();
        this.termFreq = new HashMap<>();
    }

    public int getFreqOfTerm(String term) {
        return termFreq.getOrDefault(term, 0);
    }
}
