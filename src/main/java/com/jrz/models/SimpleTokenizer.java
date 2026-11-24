package com.jrz.models;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleTokenizer implements Tokenizer{

    @Override
    public List<String> tokenize(String text) {
        return Arrays.stream(text.toLowerCase().trim().split("\\s+"))
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());
    }
    
}
