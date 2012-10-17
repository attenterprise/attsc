package com.platform.CServiceDispatcher.agents;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.att.enablers.gsms.GSMSAttributes;
import com.att.enablers.gsms.GSMSResponse;
import com.att.enablers.gsms.GSMSServices;
import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

/**
 * Class that implements misc helper methods
 * 
 * @author Marina Bashtovaya
 *
 */
public class AgentsHelper {
	/**
	 * print the request parameters and their values
	 * 
	 * @param all_params
	 */
	@SuppressWarnings("rawtypes")
	public static void printAllInputParams(HashMap all_params){
		for (Iterator a_param = all_params.keySet().iterator(); a_param.hasNext(); ){
			Object key = a_param.next();
			com.platform.api.Logger.trace("printAllInputParams: "+key+"="+all_params.get(key), java.lang.String.class);
		}
	}	
	
	/**
	 * get LocAid configuration
	 * 
	 * @return [0] - record id, [1] - registration service URL, [2] - location service URL, 
	 * [3] - address service URL, [4] - geofencing service URL, [5] - login, [6] - password, [7] - class ID
	 * @throws Exception
	 */
	public static String[] getLocAidConfiguration() throws Exception{
		Result searchResult = Functions.searchRecords("SDLocAidSetup", 
				"record_id, registration_service_url, location_service_url, address_service_url, geofencing_service_url, "+
				"login, password, class_id", "");

		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		     String msg = "Configuration information could not be retrieved";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
		      throw new Exception(msg+". See log for more details.");
		}
		else if (resultCode == 0){
			// No records found. 
		    String msg = "Configure account in the LocAid Setup object first";
		    com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
		      throw new Exception(msg);
		}

		//Records retrieved successfully
		String[] recordInfo = new String[8];
		ParametersIterator iterator = searchResult.getIterator();
		while(iterator.hasNext()){
			Parameters p = iterator.next();
			recordInfo[0] = p.get("record_id");
			recordInfo[1] = p.get("registration_service_url");
			recordInfo[2] = p.get("location_service_url");
			recordInfo[3] = p.get("address_service_url");
			recordInfo[4] = p.get("geofencing_service_url");
			recordInfo[5] = p.get("login");
			recordInfo[6] = p.get("password");
			recordInfo[7] = p.get("class_id");
			com.platform.api.Logger.trace("getRecord: "+p.toString(), AgentsHelper.class);
		}
		
		return recordInfo;
	}
	
	/**
	 * get SMS configuration
	 * 
	 * @return [0] - record id, [1] - URL, [2] - user, [3] - password, [4] - short code, [5] - SMS receiver site URL
	 * @throws Exception
	 */
	public static String[] getSMSConfiguration() throws Exception{
		Result searchResult = Functions.searchRecords("SDGSMSSetup", "record_id, url, field_user, password, shortcode, receiversite", "");

		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		     String msg = "Configuration information could not be retrieved";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
		      throw new Exception(msg+". See log for more details.");
		}
		else if (resultCode == 0){
			// No records found. 
		     String msg = "Configure the account in GSMS Setup object first";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
		      throw new Exception(msg);
		}

		//Records retrieved successfully
		String[] recordInfo = new String[6];
		ParametersIterator iterator = searchResult.getIterator();
		while(iterator.hasNext()){
			Parameters p = iterator.next();
			recordInfo[0] = p.get("record_id");
			recordInfo[1] = p.get("url");
			recordInfo[2] = p.get("field_user");
			recordInfo[3] = p.get("password");
			recordInfo[4] = p.get("shortcode");
			recordInfo[5] = p.get("receiversite");
			com.platform.api.Logger.trace("getRecord: "+p.toString(), AgentsHelper.class);
		}
		
		return recordInfo;
	}

	/**
	 * update the agent's phone AT&T LIS subscription status 
	 * 
	 * @param recordId
	 * @param status
	 * @throws Exception
	 */
	public static void updateAgentStatus(String recordId, String status) throws Exception{	
		com.platform.api.Logger.debug("Update agent's status with record ID="+recordId, AgentsHelper.class);

		// update the status
		Parameters recordValues = Functions.getParametersInstance();
		recordValues.add("lis_subscription_status", status);
		if (!"OPTIN_COMPLETE".equalsIgnoreCase(status)){
			// reset location
			recordValues.add("latitude", ""); 
			recordValues.add("longitude", ""); 
			recordValues.add("technology_used", ""); 
		}
		Result updateResult = Functions.updateRecord("SDAgents", recordId, recordValues);
		int retCode = updateResult.getCode();
		String retMessage = updateResult.getMessage();
		
		com.platform.api.Logger.debug("Functions.updateRecord return code="+retCode+". Message="+retMessage, AgentsHelper.class);
		
		if (retCode < 0){
			throw new Exception("Internal error - cannot update status of the agent: "+retMessage);
		}
	}
	
	/**
	 * update the agent's location 
	 * 
	 * @param recordId
	 * @param status
	 * @throws Exception
	 */
	public static void updateAgentLocation(String recordId, String latitude, String longitude, String technology) 
	throws Exception{	
		com.platform.api.Logger.debug("Update agent's location with record ID="+recordId, AgentsHelper.class);

		// update the status
		Parameters recordValues = Functions.getParametersInstance();
		recordValues.add("latitude", latitude); 
		recordValues.add("longitude", longitude); 
		recordValues.add("technology_used", (technology != null ? technology : "")); 
		Result updateResult = Functions.updateRecord("SDAgents", recordId, recordValues);
		int retCode = updateResult.getCode();
		String retMessage = updateResult.getMessage();
		
		com.platform.api.Logger.debug("Functions.updateRecord return code="+retCode+". Message="+retMessage, AgentsHelper.class);
		
		if (retCode < 0){
			throw new Exception("Internal error - cannot update location of the agent: "+retMessage);
		}

	}
	
	/**
	 * find the agent's record ID using phone
	 * 
	 * @param phone
	 * @return record ID
	 * @throws Exception
	 */
	public static String findAgentByPhone(String phone) throws Exception{
		com.platform.api.Logger.trace("findAgent: phone to check '"+phone+"'", AgentsHelper.class);
		if (phone != null){
			Result searchResult = Functions.searchRecords("SDAgents", "record_id", "phone = '"+phone.trim()+"'");
			int resultCode = searchResult.getCode();
			if (resultCode < 0){
			     String msg = "SDAgents information could not be retrieved";
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
			      throw new Exception(searchResult.getMessage());
			}
			else if (resultCode == 0){
				// No records found. 
			     com.platform.api.Logger.trace("No record found:(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
				return null;
			}
			
			String recordId = null;
			ParametersIterator iterator = searchResult.getIterator();
			while(iterator.hasNext()){
				Parameters p = iterator.next();
				recordId = p.get("record_id");
			    com.platform.api.Logger.trace("Record found: record ID="+recordId, AgentsHelper.class);
			}
			return recordId;
		}
		
		return null;
	}
	
	/**
	 * find the agent's phone using record ID
	 * 
	 * @param agent_id
	 * @return phone
	 * @throws Exception
	 */
	public static String findAgentById(String agent_id) throws Exception{
		if (agent_id != null){
			Result searchResult = Functions.searchRecords("SDAgents", "phone", "id = '"+agent_id.trim()+"'");
			int resultCode = searchResult.getCode();
			if (resultCode < 0){
			     String msg = "SDAgents information could not be retrieved";
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
			      throw new Exception(searchResult.getMessage());
			}
			else if (resultCode == 0){
				// No records found. 
			     com.platform.api.Logger.trace("No record found:(" + resultCode + ")" + searchResult.getMessage(), AgentsHelper.class);
				return null;
			}
			
			String phone = null;
			ParametersIterator iterator = searchResult.getIterator();
			while(iterator.hasNext()){
				Parameters p = iterator.next();
				phone = p.get("phone");
			    com.platform.api.Logger.trace("Record found: phone="+phone, AgentsHelper.class);
			}
			return phone;
		}
		
		return null;
	}

	/**
	 * send SMS using AT&T GSMS
	 * 
	 * @param phone
	 * @param text
	 * @return response object
	 * @throws Exception
	 */
	public static GSMSResponse sendSMS(String phone, String text) throws Exception{
		// --------------------------------------------------------------------
		// retrieve GSMS account settings
		String[] account = AgentsHelper.getSMSConfiguration();

		Map<String,Object> attributes = new HashMap<String, Object>();
		attributes.put(GSMSAttributes.SendMessageAttributes.DESTINATION, phone); 
		attributes.put(GSMSAttributes.SendMessageAttributes.TEXT, text);
		attributes.put(GSMSAttributes.SendMessageAttributes.RESPONSE_TYPE, GSMSAttributes.ResponseType.XML);
		
		if (account[5] != null){
			// Warning: source must be excluded when the reply to is URL
			attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO_TON, GSMSAttributes.ReplyTo.TON_URL);
			attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO, account[5]);
		}
		
		com.platform.api.Logger.debug("SMS attributes: "+attributes, AgentsHelper.class);

		GSMSServices gsms = new GSMSServices(account[1], account[2], account[3]);
		int status_code = gsms.sendMessage(attributes);
		
		com.platform.api.Logger.debug("SMS sent - status code = "+status_code, AgentsHelper.class);
		
		GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
		if (response == null){
			throw new Exception("Internal error - unknown response of the sendSMS command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}
	
}