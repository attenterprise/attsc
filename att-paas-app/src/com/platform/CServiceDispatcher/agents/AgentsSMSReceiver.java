package com.platform.CServiceDispatcher.agents;

import java.util.HashMap;

import com.att.enablers.gsms.GSMSAttributes;
import com.platform.api.*;

/**
 * Controller class responsible for processing SMS messages received on the platform.
 * The following commands are accepted:<br>
 * <ul>
 * <li>ACCEPT <Request ID> to modify the status of the request to “Accepted by Agent”</li>
 * <li>REJECT <Request ID> to modify the status of the request to “Waiting for Agent Assignment” and remove the request from the agents queue.</li>
 * <li>START <Request ID> to modify the status of the request to “Work in progress”.</li>
 * <li>DONE <Request ID> to modify the status of the request to “Completed”.<li>
 * <li>NOTE <Request ID> any note – to add the provided note to the service request Activity History. The status of the request remains unchanged.</li>
 * </ul>
 * <Request ID> is a service request ID that the agent wants to update. 
 * The request ID is sent to the agent along with the request details upon assignment.
 * 
 * @author Marina Bashtovaya
 *
 */
public class AgentsSMSReceiver implements Controller{
	
	@SuppressWarnings("rawtypes")
	public ControllerResponse execute(HashMap params) 
	throws Exception {
		
		AgentsHelper.printAllInputParams(params);

		// extra security - can only get replies to the messages sent
		String source = (String)params.get(GSMSAttributes.SendMessageAttributes.SOURCE);
		com.platform.api.Logger.debug("AgentsSMSReceiver - source="+source, this.getClass());

		String recordId = AgentsHelper.findAgentByPhone(source);
		if (recordId != null){
			// retrieve parameters of the message
			String destination = (String)params.get(GSMSAttributes.SendMessageAttributes.DESTINATION);	
			String text = (String)params.get(GSMSAttributes.SendMessageAttributes.TEXT);
			String clientMessageId = (String)params.get(GSMSAttributes.SendMessageAttributes.CLIENT_MESSAGE_ID);
			String costCentre = (String)params.get(GSMSAttributes.SendMessageAttributes.COST_CENTRE);

			// in case if the message is a multi-part message
			String segmentCount = (String)params.get(GSMSAttributes.ReceiverAttributes.SEGMENT_COUNT);
			String segmentNumber = (String)params.get(GSMSAttributes.ReceiverAttributes.SEGMENT_NUMBER);
			String referenceNumber = (String)params.get(GSMSAttributes.ReceiverAttributes.REFERENCE_NUMBER);
			
			com.platform.api.Logger.debug("destination="+destination, this.getClass());
			com.platform.api.Logger.debug("text="+text, this.getClass());
			com.platform.api.Logger.debug("clientMessageId="+clientMessageId, this.getClass());
			com.platform.api.Logger.debug("costCentre="+costCentre, this.getClass());
			com.platform.api.Logger.debug("Multi-part: segmentCount="+segmentCount+"; segmentNumber="+segmentNumber+"; referenceNumber="+referenceNumber, this.getClass());

			// ----------------------------------------------------
			// Parse the SMS, find the 			
			if (text != null){
				String[] words = text.split(" ");
				if (words.length >= 2){
					String command = words[0].trim();
					String request_id = words[1].trim();
					String status = null;
					Parameters recordValues = Functions.getParametersInstance();
					
					if ("accept".equalsIgnoreCase(command)){
						status = "Accepted by Agent";
					}
					else if ("start".equalsIgnoreCase(command)){
						status = "Work in progress";
					}
					else if ("done".equalsIgnoreCase(command)){
						status = "Completed";
					}
					else if ("reject".equalsIgnoreCase(command)){
						status = "Waiting for Agent Assignment";
						recordValues.add("related_to_sdagents", ""); 
					}
					
					if (status != null){
						// update service request status
						com.platform.api.Logger.debug("Update service request status to '"+status+"' for record ID="+request_id, this.getClass());
						recordValues.add("request_status", status); 
						Result updateResult = Functions.updateRecord("SDRequests", request_id, recordValues);
						int retCode = updateResult.getCode();
						String retMessage = updateResult.getMessage();
						 
						if (retCode < 0){
							com.platform.api.Logger.error("Functions.updateRecord return code="+retCode+". Message="+retMessage, this.getClass());
						}
						else{
							com.platform.api.Logger.debug("Functions.updateRecord return code="+retCode+". Message="+retMessage, this.getClass());
						}
					}
					else{
						if ("note".equalsIgnoreCase(command)){						
							// update service request - add note
							com.platform.api.Logger.debug("Add activity log to service request for record ID="+request_id, this.getClass());

							Parameters activityParams = Functions.getParametersInstance();
							String user_id = Functions.getEnv(ENV.USER.ID);
							String note = source+" -";
							for (int i = 2; i < words.length; i ++){
								note += " "+words[i];
							}
						    activityParams.add("sales_notes", note);
						    activityParams.add("action_type", "Phone");
						    Result result = Functions.logActivity("Status Change", "SDRequests", request_id, user_id, activityParams);

						    //Add a debug message to the Debug Log to get result of logActivity
						    com.platform.api.Logger.debug("Result from logActivity:" + result.getMessage(), this);
						}
						else{
							com.platform.api.Logger.debug("Unknown command. SMS Received: "+text, this.getClass());
						}
					}
				}
				else{
					com.platform.api.Logger.debug("Incorrect command format. SMS Received: "+text, this.getClass());
				}
			}
			else{
				com.platform.api.Logger.debug("Command is empty. SMS Received: "+text, this.getClass());
			}
		}
		else{
			com.platform.api.Logger.error("AgentsSMSReceiver - rejected because of unknown senders phone: "+source, this.getClass());
		}
		
		// ----------------------------------------------------
		// page that will be returned as a reply to GSMS
		String successPage = (String)params.get("successPage");
		if (successPage == null){
			throw new Exception("AgentsSMSReceiver - Success page is not defined for the GSMS reply");
		}
		
		ControllerResponse cr = new ControllerResponse();
		cr.setData(params);
		cr.setTargetPage(successPage);
		
		return cr;
	}
	
}