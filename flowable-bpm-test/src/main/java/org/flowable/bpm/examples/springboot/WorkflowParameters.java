package org.flowable.bpm.examples.springboot;

public class WorkflowParameters {

    private WorkflowParameters() {
        // Utility class
    }

    // --- Process Definitions
    public static final String PROCESS_REVIEW_DOCUMENT = "Review_Document";

    // --- Process Variables
    public static final String PARAM_DOCUMENT = "document";
    public static final String PARAM_REVIEW_USER = "reviewUser";

    // --- Task Definitions
    public static final String TASK_REVIEW_DOCUMENT = "Task_ReviewDocument";

    // --- Task Variables
    public static final String PARAM_APPROVED = "approved";
}
