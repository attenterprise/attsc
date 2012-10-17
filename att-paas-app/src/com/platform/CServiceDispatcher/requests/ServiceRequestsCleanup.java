package com.platform.CServiceDispatcher.requests;

import java.util.*;

import com.platform.api.*;

/**
 * Class that implements service requests archiving.
 * It can be scheduled as a job to be executed at a desired time intervals, 
 * e.g. monthly, under Settings->DataManagement->JobScheduler.
 * The class with retrieve all completed or cancelled service requests and its
 * agents details. The records will be moved from Service Requests object
 * to Service Requests Archive object.
 * 
 * @author Marina Bashtovaya
 *
 */
public class ServiceRequestsCleanup implements Schedulable{
	
	public void execute(){
		try{
		
			Map<String, Parameters> agentToRequest = new HashMap<String, Parameters>();
			
			Result searchResult = Functions.searchRecords("SDRequests", 
					"record_id, customer_name, contact_phone, request_status, description, "+
					"address, city, state, zip_code, date_created, date_modified, related_to_sdagents", 
					"request_status='Completed' OR request_status='Cancelled' ");
			
			int resultCode = searchResult.getCode();
			if (resultCode < 0){
			     String msg = "Service requests could not be retrieved for archiving";
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), this.getClass());  // Log details
			     throw new Exception(searchResult.getMessage());
			}
			else if (resultCode == 0){
				// No records found. Take action according to your business logic
				com.platform.api.Logger.debug("No requests to archive", this.getClass());
			}
			else{
				//Records retrieved successfully
				String cause = null;
				
				ParametersIterator iterator = searchResult.getIterator();
				while(iterator.hasNext()){
					Parameters p = iterator.next();
					
					Parameters archiveParams = Functions.getParametersInstance();
					archiveParams.add("customer_name", p.get("customer_name"));
					archiveParams.add("contact_phone", p.get("contact_phone"));
					archiveParams.add("request_status", p.get("request_status"));
					archiveParams.add("description", p.get("description"));
					archiveParams.add("address", p.get("address"));
					archiveParams.add("city", p.get("city"));
					archiveParams.add("state", p.get("state"));
					archiveParams.add("zip_code", p.get("zip_code"));
					archiveParams.add("request_creation_date", p.get("date_created"));
					archiveParams.add("request_completion_date", p.get("date_modified"));
					archiveParams.add("orig_record_id", p.get("record_id"));
					
					String agent_id = p.get("related_to_sdagents");
					if (agent_id != null && !agent_id.isEmpty()){
						if (cause == null){
							cause = "record_id="+ agent_id;
						}
						else{
							cause += " OR record_id="+ agent_id;
						}
						agentToRequest.put(agent_id, archiveParams);
					}
					else{
						moveServiceRequest(archiveParams); // archive the record that has no agent assigned
					}
				}
				
				// retrieve the agents info
				Result agentsResult = null;
				if (cause != null){
					agentsResult = Functions.searchRecords("SDAgents", "record_id, first_name, last_name, phone", cause);
					resultCode = agentsResult.getCode();
					if (resultCode < 0){
					     String msg = "Agents details could not be retrieved for archiving";
					     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + agentsResult.getMessage(), this.getClass());  // Log details
					     throw new Exception(agentsResult.getMessage());
					}
					else if (resultCode == 0){
						// No records found. Take action according to your business logic
						com.platform.api.Logger.debug("No agents info found", this.getClass());
					}
					else{
						// add the agent's info to the request
						iterator = agentsResult.getIterator();
						while(iterator.hasNext()){
							Parameters p = iterator.next();
							
							String agent_id = p.get("record_id");
							Parameters archiveParams = agentToRequest.get(agent_id);
							if (archiveParams != null){
								archiveParams.add("agent_first_name", p.get("first_name"));
								archiveParams.add("agent_last_name", p.get("last_name"));
								archiveParams.add("agent_phone", p.get("phone"));
								
								moveServiceRequest(archiveParams); // archive the record with agent info
							}							
						}
					}
				}
				
			} 
			
		}
		catch(Exception e){
			com.platform.api.Logger.error("Exception occured while executing ServiceRequestsCleanup: "+e, this.getClass());
		}
	}
	
	protected void moveServiceRequest(Parameters archiveParams) throws Exception{
		String save_point = "moveServiceRequest"; // for transaction rollback
		String record_id = archiveParams.get("orig_record_id");

		// add/delete needs to be done as one transaction
		if (Functions.doesSavePointExist(save_point)){
			Functions.removeSavePoint(save_point);
		}
		Functions.addSavePoint(save_point);
	
		try{
			// 1. create a record in service request archive
			com.platform.api.Logger.info("SDRequestsArchive - add new record: "+archiveParams, this.getClass());
			Result addResult = Functions.addRecord("SDRequestsArchive", archiveParams);
			
			int resultCode = addResult.getCode();
			if (resultCode < 0){
			     String msg = "Service request could not be archived. Orig record ID="+record_id;
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + addResult.getMessage(), this.getClass());  // Log details
			     throw new Exception(addResult.getMessage());
			}
			
			// 2. delete the record from service requests
			com.platform.api.Logger.info("SDRequests - delete record with ID: "+record_id, this.getClass());
			Result deleteResult = Functions.deleteRecord("SDRequests", record_id);
			resultCode = deleteResult.getCode();
			if (resultCode < 0){
			     String msg = "Service request could not be deleted. Orig record ID="+record_id;
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + deleteResult.getMessage(), this.getClass());  // Log details
			     throw new Exception(deleteResult.getMessage());
			}
		}
		catch(Exception e){
			Functions.rollbackToSavePoint(save_point); // roll back
			throw e;
		}
	}
}