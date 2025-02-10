package org.flowable.bpm.examples.springboot.process.scenario;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.flowable.bpm.examples.springboot.report.formatter.ObjectArrayFormatter;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@JGivenStage
public class ProcessWhen<SELF extends ProcessWhen<SELF>> extends AbstractProcessStage<SELF> {

    @ExpectedScenarioState
    private String businessKey;

    @ExpectedScenarioState
    private Map<String, Object> model;

    @ProvidedScenarioState
    public String processId;

    @ProvidedScenarioState
    public Map<String, String> processNameToId = new HashMap<>();

    public SELF create_a_process(@SingleQuoted String processDefinitionKey) {

        processId = workflowHelper.createProcess(processDefinitionKey, model);
        assertThat(processId).isNotNull();

        LOG.info("Created process {}", processId);
        waitForJobExecutorToProcessAllJobs();

        return self();
    }

    public SELF referenced_as(@SingleQuoted String processName) {
        processNameToId.put(processName, processId);
        return self();
    }

    public SELF set_variables(Map<String, Object> values) {
        workflowHelper.setVariables(processId, values);
        return self();
    }

    @Hidden
    public SELF wait_for_tasks(String taskDefinitionKey, int number) {
        while (workflowHelper.getTasksByKey(taskDefinitionKey).size() != number) {
            waitForJobExecutorToProcessAllJobs();
        }
        return self();
    }

    @As("complete active task of type $taskDefinitionKey")
    public SELF complete_task(@SingleQuoted String taskDefinitionKey) {
        return complete_task(taskDefinitionKey, new HashMap<>());
    }

    @As("complete active task of type $taskDefinitionKey with $variables")
    public SELF complete_task(@SingleQuoted String taskDefinitionKey, Map<String, Object> variables) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).hasSizeGreaterThanOrEqualTo(1);

        workflowHelper.completeTask(tasks.get(0).getId(), variables);
        waitForJobExecutorToProcessAllJobs();

        return self();
    }

    @As("complete active task of type $taskDefinitionKey for user $assignee with $variables")
    public SELF complete_task_by_user(@SingleQuoted String taskDefinitionKey, @SingleQuoted String assignee, Map<String, Object> variables) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey).stream()
                .filter(task -> assignee.equals(task.getAssignee()))
                .collect(toList());
        assertThat(tasks).hasSize(1);

        workflowHelper.completeTask(tasks.get(0).getId(), variables);
        waitForJobExecutorToProcessAllJobs();

        return self();
    }

    @As("complete all active task(s)")
    public SELF complete_tasks() {
        workflowHelper.getTasks().forEach(task -> workflowHelper.completeTask(task.getId()));
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    @As("complete all active task(s) of type $taskDefinitionKey")
    public SELF complete_tasks(@SingleQuoted String taskDefinitionKey) {
        workflowHelper.completeTasks(taskDefinitionKey);
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    public SELF complete_tasks(@ObjectArrayFormatter String... taskDefinitionKeys) {
        Stream.of(taskDefinitionKeys).forEach(this::complete_tasks);
        return self();
    }

    @As("complete all active tasks of type $taskDefinitionKey with $variables")
    public SELF complete_tasks(@SingleQuoted String taskDefinitionKey, Map<String, Object> variables) {
        workflowHelper.completeTasks(taskDefinitionKey, variables);
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    @As("complete all active tasks with $taskName name")
    public SELF complete_tasks_with_name(@SingleQuoted String taskName) {
        return complete_tasks_with_name(taskName, null);
    }

    @As("complete all active tasks with $taskName name with $variables")
    public SELF complete_tasks_with_name(@SingleQuoted String taskName, Map<String, Object> variables) {
        workflowHelper.getTasks().stream()
                .filter(task -> task.getName().equals(taskName))
                .forEach(task -> workflowHelper.completeTask(task.getId(), variables));
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    @As("set all active task names of type $taskDefinitionKey to $name")
    public SELF set_active_user_tasks_name(@SingleQuoted String taskDefinitionKey, @SingleQuoted String name) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> workflowHelper.setTaskName(task.getId(), name));
        return self();
    }

    @As("add task candidate user $user to all active tasks of type $taskDefinitionKey")
    public SELF add_task_candidate_user(@SingleQuoted String taskDefinitionKey, @SingleQuoted String user) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> workflowHelper.addTaskCandidateUser(task.getId(), user));
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    @As("set task assignee $assignee for all active tasks of type $taskDefinitionKey")
    public SELF set_task_assignee(@SingleQuoted String taskDefinitionKey, @SingleQuoted String assignee) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> workflowHelper.setTaskAssignee(task.getId(), assignee));
        waitForJobExecutorToProcessAllJobs();
        return self();
    }

    // Task description

    @As("document on all active tasks of type $taskDefinitionKey with $description")
    public SELF document_on_tasks(@SingleQuoted String taskDefinitionKey, @SingleQuoted String description) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> workflowHelper.setTaskDescription(task.getId(), description));
        return self();
    }

    // Process/Task comments

    @As("comment on process with $comment")
    public SELF comment_on_process(@SingleQuoted String comment) {
        workflowHelper.addProcessComment(processId, comment);
        return self();
    }

    @As("comment on all active tasks of type $taskDefinitionKey with $comment")
    public SELF comment_on_tasks(@SingleQuoted String taskDefinitionKey, @SingleQuoted String comment) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskDefinitionKey);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> workflowHelper.addTaskComment(task.getId(), comment));
        return self();
    }

    // Clock

    @Hidden
    public SELF wait_for_process_completion(String processType) {
        while (!workflowHelper.getProcessInstances(processType).isEmpty()) {
            waitForJobExecutorToProcessAllJobs();
        }
        return self();
    }

}
