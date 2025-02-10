package org.flowable.bpm.examples.springboot.process.scenario;

import static org.assertj.core.api.Assertions.assertThat;

import com.tngtech.jgiven.annotation.*;
import com.tngtech.jgiven.integration.spring.JGivenStage;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;

import java.util.*;

@JGivenStage
public class ProcessThen<SELF extends ProcessThen<SELF>> extends AbstractProcessStage<SELF> {

    private enum ModelType {
        RUNTIME, HISTORY, DETAIL, TASK_RUNTIME, TASK_HISTORY
    }

    @ExpectedScenarioState
    private String processId;

    @ExpectedScenarioState
    private Map<String, String> processNameToId;

    @ProvidedScenarioState
    public Map<String, Object> runtimeModel;

    @ProvidedScenarioState
    public Map<String, Object> historyModel;

    @ProvidedScenarioState
    public Map<String, Object> historyDetailModel;

    @ProvidedScenarioState
    public String taskId;

    @ProvidedScenarioState
    public String externalTaskId;

    @ProvidedScenarioState
    public String incidentId;

    @ProvidedScenarioState
    public String historyIncidentId;

    @ProvidedScenarioState
    public Map<String, Object> taskRuntimeModel;

    @ProvidedScenarioState
    public Map<String, Object> taskHistoryModel;

    private ModelType modelType;

    @AfterScenario
    public void cleanUp() {
        // cleanup
        workflowHelper.deleteProcesses();
    }

    protected Map<String, Object> getModel() {
        if (ModelType.RUNTIME.equals(modelType)) {
            return runtimeModel;
        } else if (ModelType.HISTORY.equals(modelType)) {
            return historyModel;
        } else if (ModelType.DETAIL.equals(modelType)) {
            return historyDetailModel;
        } else if (ModelType.TASK_RUNTIME.equals(modelType)) {
            return taskRuntimeModel;
        } else if (ModelType.TASK_HISTORY.equals(modelType)) {
            return taskHistoryModel;
        }
        return new HashMap<>();
    }

    @As("$number deployed BPMN process definition(s)")
    public SELF deployed_process_definitions(int number) {
        assertThat(workflowHelper.getProcessDefinitions()).hasSize(number);
        return self();
    }

    @As("no deployed BPMN process definitions")
    public SELF no_deployed_process_definitions() {
        return deployed_process_definitions(0);
    }

    @As("BPMN process definition of type $processType")
    public SELF deployed_process_definition(@SingleQuoted String processType) {
        assertThat(workflowHelper.getProcessDefinition(processType)).isNotNull();
        return self();
    }

    @As("$number active BPMN process(es)")
    public SELF active_processes(int number) {
        assertThat(workflowHelper.getProcessInstances()).hasSize(number);
        return self();
    }

    @As("$number active BPMN process(es) of type $processType")
    public SELF active_processes(int number, @SingleQuoted String processType) {
        assertThat(workflowHelper.getProcessInstances(processType)).hasSize(number);
        return self();
    }

    @As("1 active BPMN process of type $processType referenced as $processName")
    public SELF active_process_of_type_$_referenced_as_$(@SingleQuoted String processType,
                                                         @SingleQuoted String processName) {
        ProcessInstance process = workflowHelper.getProcessInstanceByKey(processType);
        assertThat(process).isNotNull();
        processNameToId.put(processName, process.getId());
        return self();
    }

    @As("no active BPMN processes")
    public SELF no_active_processes() {
        return active_processes(0);
    }

    @As("no active BPMN process(es) of type $processType")
    public SELF no_active_processes(@SingleQuoted String processType) {
        return active_processes(0, processType);
    }

    @As("$number completed BPMN process(es)")
    public SELF completed_processes(int number) {
        List<HistoricProcessInstance> historicProcessInstances = workflowHelper.getHistoricProcessInstances();
        List<ProcessInstance> processInstances = workflowHelper.getProcessInstances();
        assertThat(historicProcessInstances.size() - processInstances.size()).isEqualTo(number);
        return self();
    }

    @As("$number active BPMN process(es) in history")
    public SELF active_processes_in_history(int number) {
        assertThat(workflowHelper.getHistoricProcessInstances().stream()
                .filter(instance -> Objects.isNull(instance.getEndTime())))
                .hasSize(number);
        return self();
    }

    @As("no active BPMN processes in history")
    public SELF no_active_processes_in_history() {
        return active_processes_in_history(0);
    }

    @As("$number completed BPMN process(es) of type $processType")
    public SELF completed_processes(int number, @SingleQuoted String processType) {
        List<HistoricProcessInstance> historicProcessInstances = workflowHelper.getHistoricProcessInstances(processType);
        List<ProcessInstance> processInstances = workflowHelper.getProcessInstances(processType);
        assertThat(historicProcessInstances.size() - processInstances.size()).isEqualTo(number);
        return self();
    }

    @As("1 completed BPMN process of type $processType referenced as $processName")
    public SELF completed_process_of_type_$_referenced_as_$(@SingleQuoted String processType,
                                                            @SingleQuoted String processName) {
        HistoricProcessInstance process = workflowHelper.getHistoricProcessInstanceByKey(processType);
        assertThat(process).isNotNull();
        assertThat(process.getEndTime()).isNotNull();
        processNameToId.put(processName, process.getId());
        return self();
    }

    @As("no completed BPMN processes")
    public SELF no_completed_processes() {
        return completed_processes(0);
    }

    @As("no completed BPMN processes of type $processType")
    public SELF no_completed_processes(@SingleQuoted String processType) {
        return completed_processes(0, processType);
    }

    public SELF process_$_has_business_key_$(@SingleQuoted String processName, @SingleQuoted String businessKey) {
        assertThat(processNameToId).containsKey(processName);
        HistoricProcessInstance process = workflowHelper.getHistoricProcessInstance(processNameToId.get(processName));
        assertThat(process).isNotNull();
        assertThat(businessKey).isEqualTo(process.getBusinessKey());
        return self();
    }

    public SELF process_runtime() {
        modelType = ModelType.RUNTIME;
        runtimeModel = workflowHelper.getProcessVariables(processId);
        return self();
    }

    @As("process runtime for $processName")
    public SELF process_runtime(@SingleQuoted String processName) {
        assertThat(processNameToId).containsKey(processName);
        modelType = ModelType.RUNTIME;
        runtimeModel = workflowHelper.getProcessVariables(processNameToId.get(processName));
        return self();
    }

    public SELF process_history() {
        modelType = ModelType.HISTORY;
        historyModel = workflowHelper.getProcessHistoryVariables(processId);
        return self();
    }

    @As("process history for $processName")
    public SELF process_history(@SingleQuoted String processName) {
        assertThat(processNameToId).containsKey(processName);
        modelType = ModelType.HISTORY;
        historyModel = workflowHelper.getProcessHistoryVariables(processNameToId.get(processName));
        return self();
    }

    public SELF process_history_detail() {
        modelType = ModelType.DETAIL;
        historyDetailModel = workflowHelper.getProcessHistoryDetails(processId);
        return self();
    }

    @As("process history detail for $processName")
    public SELF process_history_detail(@SingleQuoted String processName) {
        assertThat(processNameToId).containsKey(processName);
        modelType = ModelType.DETAIL;
        historyDetailModel = workflowHelper.getProcessHistoryDetails(processNameToId.get(processName));
        return self();
    }

    public SELF task_history(@SingleQuoted String taskType) {
        List<HistoricTaskInstance> tasks = workflowHelper.getHistoryTasksByKey(taskType);
        assertThat(tasks).isNotEmpty();

        modelType = ModelType.TASK_HISTORY;
        taskHistoryModel = workflowHelper.getTaskHistoryVariables(tasks.get(0).getId());

        return self();
    }

    @As("contains $key")
    public SELF containsKey(@SingleQuoted String key) {
        assertThat(getModel()).containsKey(key);
        return self();
    }

    @As("contains $key with not empty value")
    public SELF containsKeyWithNotEmptyValue(@SingleQuoted String key) {
        containsKey(key);
        assertThat(getModel().get(key)).isNotNull();
        return self();
    }

    @As("contains $key with empty value")
    public SELF containsKeyWithEmptyValue(@SingleQuoted String key) {
        containsKey(key);
        assertThat(getModel().get(key)).isNull();
        return self();
    }

    @As("contains $keys")
    public SELF containsKeys(@SingleQuoted String... keys) {
        for (String key : keys) {
            containsKey(key);
        }
        return self();
    }

    @As("contains $keys with not empty values")
    public SELF containsKeysWithNotEmptyValue(@SingleQuoted String... keys) {
        for (String key : keys) {
            containsKeyWithNotEmptyValue(key);
        }
        return self();
    }

    @As("contains $keys with empty values")
    public SELF containsKeysWithEmptyValue(@SingleQuoted String... keys) {
        for (String key : keys) {
            containsKeyWithEmptyValue(key);
        }
        return self();
    }

    @As("contains $key = $value")
    public SELF contains(@SingleQuoted String key, @SingleQuoted Object value) {
        assertThat(getModel()).containsEntry(key, value);
        return self();
    }

    public SELF contains(Map<String, Object> model) {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            contains(entry.getKey(), entry.getValue());
        }
        return self();
    }

    public SELF contains_$_with_no_value_check_for_$(Map<String, Object> model, String... keys) {
        Set<String> exclusionSet = Set.of(keys);
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            if (!exclusionSet.contains(entry.getKey())) {
                contains(entry.getKey(), entry.getValue());
            }
        }
        return self();
    }

    public SELF containsOnly(Map<String, Object> model) {
        assertThat(getModel()).isEqualTo(model);
        return self();
    }

    @As("does not contain $key")
    public SELF does_not_contain(@SingleQuoted String key) {
        assertThat(getModel()).doesNotContainKey(key);
        return self();
    }

    @As("does not contain $key = $value")
    public SELF does_not_contain(@SingleQuoted String key, @SingleQuoted Object value) {
        assertThat(getModel()).doesNotContainEntry(key, value);
        return self();
    }

    public SELF does_not_contain(Map<String, Object> model) {
        for (Map.Entry<String, Object> entry : model.entrySet()) {
            does_not_contain(entry.getKey(), entry.getValue());
        }
        return self();
    }

    // -- User tasks

    @As("$number active task(s)")
    public SELF active_tasks(int number) {
        assertThat(workflowHelper.getTasks()).hasSize(number);
        return self();
    }

    @As("active task of type $taskType")
    public SELF active_task(@SingleQuoted String taskType) {
        active_tasks(1, taskType);
        taskId = workflowHelper.getTasksByKey(taskType).get(0).getId();
        return self();
    }

    public SELF with_task_name(@SingleQuoted String taskName) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getTask(taskId).getName()).isEqualTo(taskName);
        return self();
    }

    public SELF with_task_local_variable(@SingleQuoted String key, @SingleQuoted Object value) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getTaskLocalVariables(taskId)).containsEntry(key, value);
        return self();
    }

    public SELF with_candidate_user(@SingleQuoted String user) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getCandidateUsers(taskId)).contains(user);
        return self();
    }

    public SELF with_assignee(@SingleQuoted String assignee) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getAssignedUsers(taskId)).contains(assignee);
        return self();
    }

    public SELF no_users_assigned() {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getAssignedUsers(taskId)).isEmpty();
        return self();
    }

    public SELF with_candidate_group(@SingleQuoted String group) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getCandidateGroups(taskId)).contains(group);
        return self();
    }

    public SELF no_groups_assigned() {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getAssignedGroups(taskId)).isEmpty();
        return self();
    }

    public SELF with_form_key(@SingleQuoted String formKey) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getTask(taskId).getFormKey()).isEqualTo(formKey);
        return self();
    }

    public SELF with_no_form_key() {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getTask(taskId).getFormKey()).isNull();
        return self();
    }

    public SELF with_priority(@SingleQuoted int priority) {
        assertThat(taskId).isNotNull();
        assertThat(workflowHelper.getTask(taskId).getPriority()).isEqualTo(priority);
        return self();
    }

    @As("$number active task(s) of type $taskType")
    public SELF active_tasks(int number, @SingleQuoted String taskType) {
        assertThat(workflowHelper.getTasksByKey(taskType)).hasSize(number);
        return self();
    }

    @As("$number active task(s) with $taskName name")
    public SELF active_tasks_with_name(int number, @SingleQuoted String taskName) {
        assertThat(workflowHelper.getTasks().stream()
                .filter(task -> task.getName().equals(taskName)))
                .hasSize(number);
        return self();
    }

    @As("$number active task(s) of type $taskType assigned to $assignee")
    public SELF active_tasks_by_user(int number, @SingleQuoted String taskType, @SingleQuoted String assignee) {
        assertThat(workflowHelper.getTasksByKey(taskType).stream()
                .filter(task -> task.getAssignee().equals(assignee)))
                .hasSize(number);
        return self();
    }

    @As("$number active not assigned task(s) of type $taskType")
    public SELF active_not_assigned_tasks(int number, @SingleQuoted String taskType) {
        assertThat(workflowHelper.getTasksByKey(taskType).stream()
                .filter(task -> Objects.isNull(task.getAssignee())))
                .hasSize(number);
        return self();
    }

    public SELF active_tasks_of_type_$_have_candidate_user(@SingleQuoted String taskType, @SingleQuoted String user) {
        if (user != null) {
            List<Task> tasks = workflowHelper.getTasksByKey(taskType);
            assertThat(tasks).isNotEmpty();
            tasks.stream().forEach(task -> assertThat(workflowHelper.getCandidateUsers(task.getId())).contains(user));
        }
        return self();
    }

    public SELF active_tasks_of_type_$_have_candidate_group(@SingleQuoted String taskType, @SingleQuoted String group) {
        if (group != null) {
            List<Task> tasks = workflowHelper.getTasksByKey(taskType);
            assertThat(tasks).isNotEmpty();
            tasks.stream().forEach(task -> assertThat(workflowHelper.getCandidateGroups(task.getId())).contains(group));
        }
        return self();
    }

    public SELF no_active_tasks() {
        return active_tasks(0);
    }

    @As("no active task(s) of type $taskType")
    public SELF no_active_tasks(@SingleQuoted String taskType) {
        return active_tasks(0, taskType);
    }

    @As("$number history task(s)")
    public SELF completed_tasks(int number) {
        assertThat(workflowHelper.getHistoryTasks().stream()
                .filter(task -> Objects.nonNull(task.getEndTime())))
                .hasSize(number);
        return self();
    }

    @As("$number history task(s) of type $taskType")
    public SELF completed_tasks(int number, @SingleQuoted String taskType) {
        assertThat(workflowHelper.getHistoryTasksByKey(taskType).stream()
                .filter(task -> Objects.nonNull(task.getEndTime())))
                .hasSize(number);
        return self();
    }

    public SELF no_completed_tasks() {
        return completed_tasks(0);
    }

    public SELF active_tasks_of_type_$_have_name_$(@SingleQuoted String taskType, @SingleQuoted String name) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskType);
        assertThat(tasks).isNotEmpty();
        tasks.stream().forEach(task -> assertThat(task.getName()).isEqualTo(name));
        return self();
    }

    public SELF active_tasks_of_type_$_have_$_in_name(@SingleQuoted String taskType, @SingleQuoted String name) {
        List<Task> tasks = workflowHelper.getTasksByKey(taskType);
        assertThat(tasks).isNotEmpty();
        tasks.stream().forEach(task -> assertThat(task.getName()).contains(name));
        return self();
    }

    public SELF history_tasks_of_type_$_have_name_$(@SingleQuoted String taskType, @SingleQuoted String name) {
        List<HistoricTaskInstance> tasks = workflowHelper.getHistoryTasksByKey(taskType);
        assertThat(tasks).isNotEmpty();
        tasks.forEach(task -> assertThat(task.getName()).isEqualTo(name));
        return self();
    }

    // -- Activities

    public SELF withActivity(@SingleQuoted String activityName) {
        assertThat(workflowHelper.getActivityTasks(processId).stream()
                .anyMatch(p -> activityName.equals(p.getActivityName()) || activityName.equals(p.getActivityId())))
                .isTrue();
        return self();
    }

    public SELF withActivities(String... activityNames) {
        for (String activityName : activityNames) {
            withActivity(activityName);
        }
        return self();
    }

    // -- Description

    public SELF active_tasks_of_type_$_have_description(@SingleQuoted String taskType, @SingleQuoted String description) {
        List<Task> historicTaskInstances = workflowHelper.getTasksByKey(taskType);
        assertThat(historicTaskInstances).isNotEmpty();
        historicTaskInstances
                .forEach(task -> assertThat(description.equals(task.getDescription())));
        return self();
    }

    public SELF history_tasks_of_type_$_have_description(@SingleQuoted String taskType,
                                                         @SingleQuoted String description) {
        List<HistoricTaskInstance> historicTaskInstances = workflowHelper.getHistoryTasksByKey(taskType);
        assertThat(historicTaskInstances).isNotEmpty();
        historicTaskInstances
                .forEach(task -> assertThat(description.equals(task.getDescription())));
        return self();
    }

    // -- Comments

    public SELF processes_of_type_$_have_comment(@SingleQuoted String processType, @SingleQuoted String comment) {
        List<HistoricProcessInstance> historicProcessInstances = workflowHelper.getHistoricProcessInstances(processType);
        assertThat(historicProcessInstances).isNotEmpty();
        historicProcessInstances
                .forEach(process -> assertThat(workflowHelper.getProcessComments(process.getId()).stream()
                        .allMatch(item -> comment.equals(item.getFullMessage()))));
        return self();
    }

    public SELF tasks_of_type_$_have_comment(@SingleQuoted String taskType, @SingleQuoted String comment) {
        List<HistoricTaskInstance> historicTaskInstances = workflowHelper.getHistoryTasksByKey(taskType);
        assertThat(historicTaskInstances).isNotEmpty();
        historicTaskInstances
                .forEach(task -> assertThat(workflowHelper.getTaskComments(task.getId()).stream()
                        .allMatch(item -> comment.equals(item.getFullMessage()))));
        return self();
    }

}
