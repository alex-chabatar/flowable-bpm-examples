package org.flowable.bpm.examples.springboot.utils;

import static java.util.stream.Collectors.toList;

import org.apache.commons.lang3.StringUtils;
import org.flowable.dmn.api.DmnDecisionService;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricVariableUpdate;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.job.api.Job;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.*;
import java.util.function.Supplier;

@Service
public class WorkflowHelper {

    @Autowired
    private ProcessEngineConfiguration processEngineConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private DmnDecisionService decisionService;

    @Autowired
    private ManagementService managementService;

    public ProcessEngineConfiguration getProcessEngineConfiguration() {
        return processEngineConfiguration;
    }

    // --- Definitions

    public List<ProcessDefinition> getProcessDefinitions() {
        return repositoryService
                .createProcessDefinitionQuery()
                .latestVersion()
                .list();
    }

    public ProcessDefinition getProcessDefinition(String processDefinitionKey) {
        return repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .latestVersion()
                .singleResult();
    }

    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        if (!StringUtils.isEmpty(processDefinitionId)) {
            return repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId)
                    .singleResult();
        }
        return null;
    }

    public String getProcessDefinitionKeyById(String processDefinitionId) {
        ProcessDefinition definition = getProcessDefinitionById(processDefinitionId);
        return definition != null ? definition.getKey() : null;
    }

    // --- Create processes

    public String createProcess(String processDefinitionKey, Map<String, Object> values) {
        return createProcessInstance(processDefinitionKey, values).getId();
    }

    public ProcessInstance createProcessInstance(String processDefinitionKey, Map<String, Object> values) {
        return runtimeService.startProcessInstanceByKey(processDefinitionKey, values);
    }

    // --- Get

    public ProcessInstance getProcessInstance(String processId) {
        return runtimeService.createProcessInstanceQuery()
                .processInstanceId(processId)
                .singleResult();
    }

    public List<ProcessInstance> getProcessInstances(String processDefinitionKey) {
        return runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
    }

    public ProcessInstance getProcessInstanceByKey(String processDefinitionKey) {
        List<ProcessInstance> processInstances = getProcessInstances(processDefinitionKey);
        return processInstances.isEmpty() ? null : processInstances.get(0);
    }

    public Date getProcessInstanceStartTime(String processInstanceId) {
        if (StringUtils.isEmpty(processInstanceId)) {
            return null;
        }

        HistoricProcessInstance historicProcessInstance = getHistoricProcessInstance(processInstanceId);

        if (historicProcessInstance != null) {
            if (historicProcessInstance.getSuperProcessInstanceId() != null) {
                return getProcessInstanceStartTime(historicProcessInstance.getSuperProcessInstanceId());
            } else {
                return historicProcessInstance.getStartTime();
            }
        }

        return null;
    }

    public List<ProcessInstance> getProcessInstances() {
        return runtimeService
                .createProcessInstanceQuery()
                .list();
    }

    public Map<String, Object> getProcessVariables(String executionId) {
        Map<String, Object> variables = new HashMap<>();
//        runtimeService.createVariableInstanceQuery()
//                .executionIdIn(executionId)
//                .list()
//                .forEach(var -> variables.put(var.getName(), var.getValue()));
        return variables;
    }

    public HistoricProcessInstance getHistoricProcessInstance(String processId) {
        return historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceId(processId)
                .singleResult();
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances() {
        return historyService
                .createHistoricProcessInstanceQuery()
                .list();
    }

    public List<HistoricProcessInstance> getHistoricProcessInstances(String processDefinitionKey) {
        return historyService
                .createHistoricProcessInstanceQuery()
                .processDefinitionKey(processDefinitionKey)
                .list();
    }

    public HistoricProcessInstance getHistoricProcessInstanceByKey(String processDefinitionKey) {
        List<HistoricProcessInstance> processInstances = getHistoricProcessInstances(processDefinitionKey);
        return processInstances.isEmpty() ? null : processInstances.get(0);
    }

    public Map<String, Object> getProcessHistoryVariables(String executionId) {
        Map<String, Object> variables = new HashMap<>();
        historyService.createHistoricVariableInstanceQuery()
                .executionId(executionId)
                .list()
                .forEach(var -> variables.put(var.getVariableName(), var.getValue()));
        return variables;
    }

    public Map<String, Object> getProcessHistoryDetails(String executionId) {
        Map<String, Object> variables = new HashMap<>();
        historyService.createHistoricDetailQuery()
                .executionId(executionId)
                .list()
                .forEach(var -> {
                    if (var instanceof HistoricVariableUpdate) {
                        HistoricVariableUpdate variableUpdate = (HistoricVariableUpdate) var;
                        variables.put(variableUpdate.getVariableName(), variableUpdate.getValue());
                    }
                });
        return variables;
    }

    public Map<String, Object> getTaskHistoryVariables(String taskId) {
        Map<String, Object> variables = new HashMap<>();
        historyService.createHistoricVariableInstanceQuery()
                .taskId(taskId)
                .list()
                .forEach(var -> variables.put(var.getVariableName(), var.getValue()));
        return variables;
    }

    // --- Delete

    public void deleteProcess(String processId, String reason) {
        runtimeService.deleteProcessInstance(processId, reason);
    }

    public void deleteProcesses(String reason) {
        getProcessInstances().forEach(process -> deleteProcess(process.getId(), reason));
    }

    public void deleteHistoryProcess(String processId) {
        historyService.deleteHistoricProcessInstance(processId);
    }

    public void deleteHistoryProcesses() {
        getHistoricProcessInstances().forEach(process -> deleteHistoryProcess(process.getId()));
    }

    public void deleteProcesses() {
        deleteProcesses("cleanup");
        deleteHistoryProcesses();
//        closeCaseInstances();
//        deleteHistoryCaseInstances();
//        deleteHistoricDecisionProcesses();
    }

    // -- User tasks

    public List<Task> getTasks() {
        return taskService
                .createTaskQuery()
                .list();
    }

    public Task getTask(String taskId) {
        if (!StringUtils.isEmpty(taskId)) {
            return taskService
                    .createTaskQuery()
                    .taskId(taskId)
                    .singleResult();
        }
        return null;
    }

    public List<Task> getTasksByKey(String taskDefinitionKey) {
        return taskService
                .createTaskQuery()
                .taskDefinitionKey(taskDefinitionKey)
                .list();
    }

    public List<HistoricTaskInstance> getHistoryTasks() {
        return historyService
                .createHistoricTaskInstanceQuery()
                .list();
    }

    public HistoricTaskInstance getHistoryTask(String taskId) {
        if (!StringUtils.isEmpty(taskId)) {
            return historyService
                    .createHistoricTaskInstanceQuery()
                    .taskId(taskId)
                    .singleResult();
        }
        return null;
    }

    public List<HistoricTaskInstance> getHistoryTasksByKey(String taskDefinitionKey) {
        return historyService
                .createHistoricTaskInstanceQuery()
                .taskDefinitionKey(taskDefinitionKey)
                .list();
    }

    public Map<String, Object> getTaskLocalVariables(String taskId) {
        return taskService.getVariablesLocal(taskId);
    }

    public void setTaskName(String taskId, String taskName) {
        Task task = getTask(taskId);
        if (task != null) {
            task.setName(taskName);
            taskService.saveTask(task);
        } else {
            throw new IllegalArgumentException(String.format("Task '%s' not found in runtime", taskId));
        }
    }

    public List<String> getCandidateUsers(String taskId) {
        return getTaskUsers(taskId, IdentityLinkType.CANDIDATE);
    }

    public List<String> getAssignedUsers(String taskId) {
        return getTaskUsers(taskId, IdentityLinkType.ASSIGNEE);
    }

    public List<String> getTaskUsers(String taskId, String identityLinkType) {
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        if (identityLinks != null) {
            return identityLinks.stream()
                    .filter(id -> identityLinkType.equals(id.getType()) && id.getUserId() != null)
                    .map(IdentityLink::getUserId)
                    .collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void setTaskAssignee(String taskId, String user) {
        setTaskUser(taskId, user, IdentityLinkType.ASSIGNEE);
    }

    public void addTaskCandidateUser(String taskId, String user) {
        setTaskUser(taskId, user, IdentityLinkType.CANDIDATE);
    }

    public void setTaskOwner(String taskId, String user) {
        setTaskUser(taskId, user, IdentityLinkType.OWNER);
    }

    private void setTaskUser(String taskId, String user, String identityLinkType) {
        if (IdentityLinkType.ASSIGNEE.equals(identityLinkType)) {
            taskService.setAssignee(taskId, user);
        } else if (IdentityLinkType.CANDIDATE.equals(identityLinkType)) {
            taskService.addCandidateUser(taskId, user);
        } else if (IdentityLinkType.OWNER.equals(identityLinkType)) {
            taskService.setOwner(taskId, user);
        }
    }

    public List<String> getCandidateGroups(String taskId) {
        return getTaskGroups(taskId, IdentityLinkType.CANDIDATE);
    }

    public List<String> getAssignedGroups(String taskId) {
        return getTaskGroups(taskId, IdentityLinkType.ASSIGNEE);
    }

    public List<String> getTaskGroups(String taskId, String identityLinkType) {
        List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskId);
        if (identityLinks != null) {
            return identityLinks.stream()
                    .filter(id -> identityLinkType.equals(id.getType()) && id.getGroupId() != null)
                    .map(IdentityLink::getGroupId)
                    .collect(toList());
        } else {
            return Collections.emptyList();
        }
    }

    public void completeTask(String taskId) {
        taskService.complete(taskId);
    }

    public void completeTask(String taskId, Map<String, Object> variables) {
        taskService.complete(taskId, variables);
    }

    public void completeTasks(String taskDefinitionKey) {
        completeTasks(taskDefinitionKey, new HashMap<>());
    }

    public void completeTasks(String taskDefinitionKey, Map<String, Object> variables) {
        getTasksByKey(taskDefinitionKey)
                .stream()
                .forEach(task -> completeTask(task.getId(), variables));
    }

    // -- variables

    public boolean containsNotEmptyStringVariable(String executionId, String variableName) {
        String variable = getVariable(executionId, variableName);
        return StringUtils.isNotEmpty(variable);
    }

    public boolean containsVariable(String executionId, String variableName) {
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        return variables.containsKey(variableName);
    }

    public boolean containsNotNullVariable(String executionId, String variableName) {
        Map<String, Object> variables = runtimeService.getVariables(executionId);
        return variables.containsKey(variableName) && variables.get(variableName) != null;
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getVariable(String executionId, String variableName) {
        return (T) runtimeService.getVariable(executionId, variableName);
    }

    @SuppressWarnings("unchecked")
    public <T extends Serializable> T getVariableLocal(String executionId, String variableName) {
        return (T) runtimeService.getVariableLocal(executionId, variableName);
    }

    public void setVariable(String executionId, String name, Object value) {
        runtimeService.setVariable(executionId, name, value);
    }

    public void setVariables(String executionId, Map<String, Object> values) {
        runtimeService.setVariables(executionId, values);
    }

    public void removeVariable(String executionId, String name) {
        runtimeService.removeVariable(executionId, name);
    }

    // -- Decisions

    public List<Map<String, Object>> evaluateDecision(String decisionKey, Map<String, Object> variables) {
        return decisionService.createExecuteDecisionBuilder()
            .decisionKey(decisionKey)
            .variables(variables)
            .executeDecision();
    }

    // -- Activities

    public List<HistoricActivityInstance> getActivityTasks(String processId) {
        return historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processId)
                .orderByHistoricActivityInstanceStartTime().asc()
                .list();
    }

    // -- Description / Comments

    public List<Comment> getProcessComments(String processId) {
        return taskService.getProcessInstanceComments(processId);
    }

    public void addProcessComment(String processId, String comment) {
        taskService.addComment(null, processId, comment);
    }

    public String getTaskDescription(String taskId) {
        return Optional.ofNullable(getTask(taskId))
                .map(Task::getDescription)
                .orElse(Optional.ofNullable(getHistoryTask(taskId))
                        .map(HistoricTaskInstance::getDescription)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Task %s not found", taskId))));
    }

    public void setTaskDescription(String taskId, String description) {
        Optional.ofNullable(getTask(taskId)).ifPresent(task -> {
            task.setDescription(description);
            taskService.saveTask(task);
        });
    }

    public List<Comment> getTaskComments(String taskId) {
        return taskService.getTaskComments(taskId);
    }

    public void addTaskComment(String taskId, String comment) {
        taskService.addComment(taskId, null, comment);
    }

    // -- Jobs / Timers

    public List<Job> getJobs() {
        return managementService.createJobQuery().list();
    }

    public List<Job> getTimers() {
        return managementService.createJobQuery()
                .timers()
                .list();
    }

    // -- Execution

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public <T> T executeInNewTransaction(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            throw ex;
        }
    }

}
