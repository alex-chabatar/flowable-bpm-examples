package org.flowable.bpm.examples.springboot.process.test;

import static org.flowable.bpm.examples.springboot.WorkflowParameters.*;

import java.util.Map;
import java.util.UUID;

public class TestDataProvider {

    public static final String ALEX = "alex";
    public static final String ZACK = "zack";
    public static final String JAREK = "jarek";
    public static final String YAO = "yao";

    public static Map<String, Object> reviewDocumentModel(String document, String reviewer) {
        return Map.of(PARAM_DOCUMENT, document, PARAM_REVIEW_USER, reviewer);
    }

     public static Map<String, Object> approveDocument(boolean approved) {
        return Map.of(PARAM_APPROVED, approved);
    }

    public static String document() {
        return UUID.randomUUID().toString();
    }

}
