package br.com.agilize.flow.rest;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiObjectNotFoundException;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.agilizeware.enums.WorkFlowEnum;

@RestController
@RequestMapping("/instance")
public class AgilizeFlowInstanceRest {
	
	private static final Logger log = LogManager.getLogger(AgilizeFlowInstanceRest.class);
	private static ProcessEngine processEngine;
	
	static {
		if(processEngine == null) {
			processEngine = ProcessEngines.getDefaultProcessEngine();
		}
	}

    @RequestMapping(value = { "/init/{idWFEnum}" }, method = RequestMethod.POST)
	@ResponseBody
	public String initFlow(@PathVariable("idWFEnum") Integer idWFEnum, @RequestBody(required=false) Map<String, Object> variables) {
    	
    	WorkFlowEnum wf = WorkFlowEnum.findByCode(idWFEnum);
    	
    	RuntimeService runtimeService = processEngine.getRuntimeService();
    	ProcessInstance processInstance;
    	try {
        	processInstance = runtimeService.startProcessInstanceByKey(wf.getDescription(), variables);
    	}
    	catch(ActivitiObjectNotFoundException acne) {
    		RepositoryService repositoryService = processEngine.getRepositoryService();
    		String path = Paths.get(".").toAbsolutePath().normalize().toString() + wf.getPath();
    		repositoryService.createDeployment().name(wf.getDescription()).
				//addClasspathResource("C:\\Projetos\\ELASTICCODE\\SPRING\\WKSP\\agilize-flow\\src\\main\\resources\\process\\isobrou\\StoreWF.bpmn20.xml").deploy();
				addClasspathResource("C:\\Projetos\\ELASTICCODE\\SPRING\\WKSP\\agilize-flow\\src\\main\\resources\\process\\isobrou\\StoreWF.bpmn20.xml").deploy();
    		repositoryService.createProcessDefinitionQuery();
    		repositoryService.activateProcessDefinitionByKey(wf.getDescription());
        	processInstance = runtimeService.startProcessInstanceByKey(wf.getDescription(), variables);
    	}
    	
    	// Verify that we started a new process instance
    	String ret = "**** Init Process **** Number of process instances: " + runtimeService.createProcessInstanceQuery().count() +
    			" | getBusinessKey = " + processInstance.getBusinessKey() +
    			" | getDeploymentId = " + processInstance.getDeploymentId() +
    			" | getId = " + processInstance.getId() +
    			" | getName = " + processInstance.getName() +
    			" | getProcessInstanceId = " + processInstance.getProcessInstanceId();
    	log.info(ret);
    	return processInstance.getProcessInstanceId();
    }
    
    @RequestMapping(value = { "/completeTask/{idInstance}" }, method = RequestMethod.GET)
	@ResponseBody
	public String completeTask(@PathVariable("idInstance") String idInstance) {
    	
    	TaskService taskService = processEngine.getTaskService();
    	Task task = taskService.createTaskQuery().processInstanceId(idInstance).singleResult();
    	
    	Map<String, Object> taskVariables = new HashMap<String, Object>();
    	taskVariables.put("vacationApproved", "false");
    	taskVariables.put("managerMotivation", "We have a tight deadline!");
    	
    	String ret = "---- Complete Tasks ----- Task ID = " + task.getId() +
    			" | getAssignee = " + task.getAssignee() +
    			" | getCategory = " + task.getCategory() +
    			" | getDescription = " + task.getDescription() +
    			" | getExecutionId = " + task.getExecutionId() +
    			" | getFormKey = " + task.getFormKey() +
    			" | getExecutionId = " + task.getId() +
    			" | getName = " + task.getName() +
    			" | getOwner = " + task.getOwner() +
    			" | getProcessDefinitionId = " + task.getProcessDefinitionId() +
    			" | getTaskDefinitionKey = " + task.getTaskDefinitionKey() +
    			" | getProcessInstanceId = " + task.getProcessInstanceId() +
    			" | getDelegationState = " + task.getDelegationState() +
    			" | getProcessVariables = " + task.getProcessVariables() +
    			" | getDueDate = " + task.getDueDate();
    	
    	taskService.complete(task.getId(), taskVariables);
    	
    	
    	log.info(ret);
    	return ret;
    }
    
    @RequestMapping(value = { "/findTasks/{nameFlow}" }, method = RequestMethod.GET)
	@ResponseBody
	public String findTasks(@PathVariable("nameFlow") String nameFlow/*, @RequestBody Map<String, String> variables*/) {
    	
    	TaskService taskService = processEngine.getTaskService();
    	TaskQuery taskQuery = taskService.createTaskQuery().processDefinitionKey(nameFlow);
    	/*if(variables != null && !variables.isEmpty()) {
    		for(String key : variables.keySet()) {
    			if(variables.get(key) != null && !variables.get(key).isEmpty()) {
    				taskQuery = taskQuery.processVariableValueEqualsIgnoreCase(key, variables.get(key));
    			}
    		}
    	}*/

taskQuery = taskQuery.processVariableValueEqualsIgnoreCase("employeeName", "Kermit");
    	
    	List<Task> tasks = taskQuery.list();
    	String ret = "";
    	if(tasks != null && !tasks.isEmpty()) {
    		for(Task task : tasks) {
    			ret = ret + "((((((((((((((((((((((((((((((((((((((((((((((((((((((((((";
    			ret = ret + System.lineSeparator();
    			ret = ret + "---- Complete Tasks ----- Task ID = " + task.getId() +
    	    			" | getAssignee = " + task.getAssignee() +
    	    			" | getCategory = " + task.getCategory() +
    	    			" | getDescription = " + task.getDescription() +
    	    			" | getExecutionId = " + task.getExecutionId() +
    	    			" | getFormKey = " + task.getFormKey() +
    	    			" | getExecutionId = " + task.getId() +
    	    			" | getName = " + task.getName() +
    	    			" | getOwner = " + task.getOwner() +
    	    			" | getProcessDefinitionId = " + task.getProcessDefinitionId() +
    	    			" | getTaskDefinitionKey = " + task.getTaskDefinitionKey() +
    	    			" | getProcessInstanceId = " + task.getProcessInstanceId() +
    	    			" | getDelegationState = " + task.getDelegationState() +
    	    			" | getProcessVariables = " + task.getProcessVariables() +
    	    			" | getDueDate = " + task.getDueDate();
    			ret = ret + System.lineSeparator();
    			ret = ret + "))))))))))))))))))))))))))))))))))))))))))))))))))))))))))";
    			ret = ret + System.lineSeparator();
    		}
    	}
    	
    	log.info(ret);
    	return ret;
    }
    
}
