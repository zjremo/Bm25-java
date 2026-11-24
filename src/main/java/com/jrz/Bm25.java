package com.jrz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jrz.models.Db;
import com.jrz.models.Document;
import com.jrz.models.ForwardIndex;
import com.jrz.models.InvertedIndex;
import com.jrz.models.SimpleTokenizer;
import com.jrz.models.Tokenizer;

public final class Bm25 {
    // 公式里面的一些参数
    private static double k1 = 1.2;
    private static double b = 0.75;

    private static Tokenizer tokenizer;
    static {
        tokenizer = new SimpleTokenizer();
    }

    static record Result(Document doc, double score) {}

    private static ForwardIndex buildForwardIndex(List<Document> docs) {
        ForwardIndex forwardIndex = new ForwardIndex();
        for (Document doc : docs)
            forwardIndex.add(doc.getId(), doc);

        return forwardIndex;
    }

    private static InvertedIndex buildInvertedIndex(List<Document> docs) {
        InvertedIndex invertedIndex = new InvertedIndex();
        for (Document doc : docs) {
            doc.getTerms().forEach(term -> invertedIndex.add(term, doc.getId()));
        }

        return invertedIndex;
    }

    public static Db storeDocuments(List<Document> docs) {
        // 1. 正向索引构建
        ForwardIndex forwardIndex = buildForwardIndex(docs);

        // 2. 正文切分和词频统计
        caculateTermsFreq(docs);

        // 3. 倒排索引构建
        InvertedIndex invertedIndex = buildInvertedIndex(docs);
        return new Db(forwardIndex, invertedIndex);
    }

    private static void caculateTermsFreq(List<Document> docs) {
        for (Document doc : docs) {
            List<String> terms = tokenizer.tokenize(doc.getContent());
            doc.setTerms(terms);
            for (String term : terms)
                doc.getTermFreq().merge(term, 1, Integer::sum);
        }
    }

    // query + Db
    public static List<Result> processBM25(String query, Db db) {
        List<Result> resList = new ArrayList<>();

        // 1. 切分terms
        List<String> terms = tokenizer.tokenize(query);
        // 2. 计算avgdl
        double avgdl = getAvgDocLength(db);
        // 3. 逐个term计算IDF和TFNorm, tf为0则score这项明显为0 
        // 3.1 以doc为键map
        Map<Integer, Double> docScoreMap = new HashMap<>();
        // 3.2 用term驱动来累加
        for (String term : terms) {
            Set<Integer> termDocs = db.getInvertedIndex().getDocs(term);
            for (int ind: termDocs) 
                docScoreMap.merge(ind, getIDF(term, db) * getTFNorm(term, db.getForwardIndex().getDocument(ind), avgdl), Double::sum);
        }

        // 4. 构造resList
        for (Map.Entry<Integer, Double> entry : docScoreMap.entrySet()) {
            int ind = entry.getKey();
            double score = entry.getValue();
            resList.add(new Result(db.getForwardIndex().getDocument(ind), score));
        }

        // 5. 降序排列
        Collections.sort(resList, (a, b) -> (Double.compare(b.score, a.score)));
        return resList;
    }

    private static double getAvgDocLength(Db db) {
        List<Document> docs = db.getAllDocs();

        if (docs.isEmpty()) {
            return 0.0;
        }

        int totalLength = 0;

        for (Document doc : docs) {
            totalLength += doc.getTerms().size();
        }

        return (double) totalLength / docs.size();
    }

    private static double getIDF(String term, Db db) {
        int n = db.getAllDocs().size();

        if (n == 0) {
            return 0.0;
        }

        // df: 包含该 term 的文档数量
        int df = db.getInvertedIndex().getDocs(term).size();

        return Math.log(
                1.0 + (n - df + 0.5) / (df + 0.5));
    }

    private static double getTFNorm(String term, Document doc, double avgdl) {
        int tf = doc.getFreqOfTerm(term);

        if (tf == 0 || avgdl == 0.0) {
            return 0.0;
        }

        int docLength = doc.getTerms().size();
        double numerator = tf * (k1 + 1);
        double denominator = tf + k1 * (1 - b + b * docLength / avgdl);
        return numerator / denominator;
    }
}