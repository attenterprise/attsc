package com.platform.CServiceDispatcher.requests;

import java.util.*;
import java.text.*;

import com.platform.CServiceDispatcher.agents.AgentsHelper;
import com.platform.api.*;

/**
 * Controller Class for object Service Requests.
 * Action is a parameter that determines the outcome:<br>
 * <ul>
 * <li>"getAgents" to retrieve all agents info for service request assignment. The distance between the service request and each agent is calculated</li>
 * <li>"assignAgent" to assign the selected agent to the service request. SMS message with the request details will be sent to the agent's phone</li>
 * </ul>
 * 
 * @author Marina Bashtovaya
 *
 */
public class ServiceRequestsController implements Controller{
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ControllerResponse execute(HashMap params) throws Exception
	{
		String action = (String) params.get("action");
		com.platform.api.Logger.debug("action="+action, this.getClass());
		AgentsHelper.printAllInputParams(params);
		
		ControllerResponse cr = new ControllerResponse();
		
		List<Parameters> sortedAgents = null;
		if ("getAgents".equalsIgnoreCase(action)){
			
			Result searchResult = Functions.searchRecords("SDAgents", 
					"record_id, first_name, last_name, phone, latitude, longitude", 
					"lis_subscription_status='OPTIN_COMPLETE' AND is_active=1");
			
			int resultCode = searchResult.getCode();
			if (resultCode < 0){
			     String msg = "Agents could not be retrieved";
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), this.getClass());  // Log details
			     throw new Exception(searchResult.getMessage());
			}
			else if (resultCode == 0){
				// No records found. Take action according to your business logic
				throw new Exception("Please configure your agents first");
			}
			else{
				//Records retrieved successfully
				String requestLatitude = (String) params.get("latitude");
				String requestLongitude = (String) params.get("longitude");
				Double latD = Double.parseDouble(requestLatitude);
				Double longD = Double.parseDouble(requestLongitude);
				
				DecimalFormat format = new DecimalFormat("#.##");
				sortedAgents = new ArrayList<Parameters>();
				ParametersIterator iterator = searchResult.getIterator();
				while(iterator.hasNext()){
					Parameters p = iterator.next();
					HashMap p_map = p.getParams();

					try{
						String latStr = p.get("latitude");
						String longStr = p.get("longitude");
						
						if (latStr != null && !latStr.isEmpty() && longStr != null && !longStr.isEmpty()){
							double latitude = Double.parseDouble(latStr);
							double longitude = Double.parseDouble(longStr);
							
							double distance = distFrom(latD, longD, latitude, longitude);	
							p_map.put("distance", format.format(distance));
							
							boolean added = false;
							for (int i = 0; i < sortedAgents.size(); i ++){
								Parameters a_p = sortedAgents.get(i);
								double a_d = Double.parseDouble(a_p.get("distance"));
								if (a_d > distance){
									added = true;
									sortedAgents.add(i, p);
									break;
								}
							}
							if (!added){
								sortedAgents.add(p); // add to the end of the list
							}
						}
					}
					catch(Exception e){
						com.platform.api.Logger.error("Cannot calculate distance. "+e, this.getClass());
					}
				}
			} 
			params.put("searchResultObj", sortedAgents);
			cr.setTargetPage("sdServiceRequestsAssignment.jsp");
		}
		else if ("assignAgent".equalsIgnoreCase(action)){
			String record_id = (String)params.get("request_id");
			if (record_id == null){
				throw new Exception("Service request needs to be selected to do the agent assignment");
			}
			String agent_id = (String)params.get("agent_selection");
			if (agent_id == null){
				throw new Exception("Agent needs to be selected to do the service request assignment");
			}
			String agent_phone = (String)params.get("agent_phone");
			if (agent_phone == null){
				throw new Exception("Agent phone needs to be defined to do the service request assignment");
			}
			String customer_name = (String)params.get("customer_name");
			if (customer_name == null){
				throw new Exception("Customer name needs to be defined");
			}
			String customer_address = (String)params.get("customer_address");
			if (customer_address == null){
				throw new Exception("Customer address needs to be defined");
			}
			String customer_description = (String)params.get("customer_description");
			if (customer_description == null){
				throw new Exception("Service request description needs to be defined");
			}
			
			// update the agent information
			Parameters recordValues = Functions.getParametersInstance();
			recordValues.add("request_status", "Waiting for Agent Acceptance"); 
			recordValues.add("related_to_sdagents", agent_id); 
					
			com.platform.api.Logger.debug("Update service request with record ID="+record_id, this.getClass());
			
			Result updateResult = Functions.updateRecord("SDRequests", record_id, recordValues);
			int retCode = updateResult.getCode();
			String retMessage = updateResult.getMessage();
			 
			com.platform.api.Logger.debug("Functions.updateRecord return code="+retCode+". Message="+retMessage, this.getClass());
			
			if (retCode < 0){
				throw new Exception("Cannot update service request: "+retMessage);
			}
			
			// send SMS to the agent
			//String msg = ""+customer_name+","+customer_address+":"+customer_description+". Reply ACCEPT/REJECT/START/DONE "+record_id+". Or reply NOTE "+record_id+" any text.";
			//AgentsHelper.sendSMS(agent_phone, msg);
			
			cr.setTargetPage("sdServiceRequestsAssignmentResult.jsp");		
		}
		else{
			throw new Exception("Unknown action: "+action);
		}
		
		// --------------------------------------------------------
		// Pass the parameters to the target page
		cr.setData(params);
		
		return cr;
	}
	
	  public static double distFrom(double lat1, double lng1, double lat2, double lng2) {     
		  double earthRadius = 3958.75;     
		  double dLat = Math.toRadians(lat2-lat1);     
		  double dLng = Math.toRadians(lng2-lng1);     
		  double a = Math.sin(dLat/2) * Math.sin(dLat/2) +               
				  Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *                
				  Math.sin(dLng/2) * Math.sin(dLng/2);     
		  double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));     
		  double dist = earthRadius * c;      
		  return dist;     
	  } 	
}