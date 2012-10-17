package com.platform.CServiceDispatcher.agents;

import java.util.*;

import com.att.enablers.gsms.*;
import com.att.enablers.locaid.*;
import com.att.enablers.locaid.request.ClassIDList;
import com.att.enablers.locaid.response.*;
import com.platform.api.*;

/**
 * Controller Class for object Agents.
 * Action is a parameter that determines the outcome:<br>
 * <ul>
 * <li>"register" to execute one of the AT&T LIS subscription commands (OPTIN,YES,CANCEL)</li>
 * <li>"status" to get the AT&T LIS subscription status</li>
 * <li>"latlongMultiple" to retrieve the locations of the phones</li>
 * <li>"sendSMS" to send sms message to the agent using AT&T GSMS</li>
 * </ul>
 * 
 * @author Marina Bashtovaya
 *
 */
public class AgentsController implements Controller
{

	@SuppressWarnings({ "rawtypes"})
	public ControllerResponse execute(HashMap params) 
	throws Exception {
		AgentsHelper.printAllInputParams(params);
		
		ControllerResponse cr = null;
		
		// --------------------------------------------------------------------
		String action = (String) params.get("action");
		if ("register".equalsIgnoreCase(action)){
			cr = register(params);
		}
		else if ("status".equalsIgnoreCase(action)){
			cr = status(params); 
		}
		else if ("latlongMultiple".equalsIgnoreCase(action)){
			cr = latlongMultiple(params);
		}
		else if ("sendSMS".equalsIgnoreCase(action)){
			cr = sendSMS(params); 
		}
		
		if (cr == null){
			throw new Exception("Unknown action: "+action);
		}
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse register(HashMap params) throws Exception{
		// find LocAid settings
		String[] account = AgentsHelper.getLocAidConfiguration();

		String command = (String) params.get("command");
		if (command == null || command != null && command.isEmpty()){
			throw new Exception("Internal error - command is undefined for action register");
		}

		// find phone numbers of the records that were selected
		Map<String, String> agents = findSelectedAgents(params);
		
		List<ClassIDList> classIdListObj = new ArrayList<ClassIDList>();
		ClassIDList classIdList = new ClassIDList();
		classIdList.setClassId(account[7]);
		List<String> msisdnList = classIdList.getMsisdnList();
		for (String phone: agents.keySet()){
			msisdnList.add(phone);
		}
		classIdListObj.add(classIdList);

		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		SubscribePhoneResponseBean subscribeResponseObj = service.subscribePhone(command, classIdListObj);
		
		// request status update
		status(params); 
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", subscribeResponseObj);
		cr.setData(params);
		cr.setTargetPage("sdAgentsSubscription.jsp");
		
		return cr;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse status(HashMap params) throws Exception{
		// find LocAid settings
		String[] account = AgentsHelper.getLocAidConfiguration();

		// find phone numbers of the records that were selected
		Map<String, String> agents = findSelectedAgents(params);
		
		List<String> phones = new ArrayList<String>();
		for (String phone: agents.keySet()){
			phones.add(phone);
		}
		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		PhoneStatusListResponseBean statusResponseObj = service.getPhoneStatus(phones);
		
		if (statusResponseObj != null){
			boolean statusUpdated = false;
			List<ComplexMsisdnResponseBean> msisdnResponseList = statusResponseObj.getMsisdnList();
			if (msisdnResponseList != null){
				for (ComplexMsisdnResponseBean msisdnResponseObj : msisdnResponseList) {
					String phoneReturned = msisdnResponseObj.getMsisdn();
					
					String recordId = agents.get(phoneReturned);
					if (recordId != null){
						List<ClassIDStatusResponseBean> classIdStatusList = msisdnResponseObj.getClassIdList();
						if (classIdStatusList != null){
							for (ClassIDStatusResponseBean classIdStatusObj : classIdStatusList) {
								String classIdReturned = classIdStatusObj.getClassId();
								if (account[7].equalsIgnoreCase(classIdReturned)){
									com.platform.api.Logger.debug("msisdn=" +phoneReturned+". Status=" + classIdStatusObj.getStatus(), this.getClass());
									AgentsHelper.updateAgentStatus(recordId, classIdStatusObj.getStatus());
									statusUpdated = true;
								}
							}
						}
						if (!statusUpdated){
							// some error occurred - reset the current one
							AgentsHelper.updateAgentStatus(recordId, "");
						}
					}
				}
			}
		}
		else{
			throw new Exception("Unexpected error occrured - PhoneStatusListResponseBean object is NULL.");
		}
		
		ControllerResponse cr = new ControllerResponse();
		params.put("class_id", account[7]);
		params.put("locaid_response", statusResponseObj);
		cr.setData(params);
		cr.setTargetPage("sdAgentsSubscriptionStatus.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse latlongMultiple(HashMap params) throws Exception{
		// find LocAid settings
		String[] account = AgentsHelper.getLocAidConfiguration();

		// find phone numbers of the records that were selected
		Map<String, String> agents = findSelectedAgents(params);
		
		List<String> phones = new ArrayList<String>();
		for (String phone: agents.keySet()){
			phones.add(phone);
		}

		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action latlongMultiple");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action latlongMultiple");
		}

		LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
		LocationAnswerResponseBean locationAnswerResponseObj = service.getLocationsX(phones, LocAidAttributes.CoorType.DECIMAL, locationMethod, 
				LocAidAttributes.SyncType.SYNC, Integer.valueOf(overage));
		
		if (locationAnswerResponseObj != null){
			List<LocationResponseBean> responseList = locationAnswerResponseObj.getLocationResponse();
			
			if (responseList != null){
				for (LocationResponseBean locationResponseObj: responseList){
					
					String phoneReturned = locationResponseObj.getNumber();
					
					String recordId = agents.get(phoneReturned);
					if (recordId != null){
						CoordinateGeo coor = locationResponseObj.getCoordinateGeo();
						if (coor != null){
							com.platform.api.Logger.debug("msisdn=" +phoneReturned+
									". Latitude=" + coor.getY() + 
									". Longitude=" + coor.getX() +
									". Technology used=" + locationResponseObj.getTechnology(), this.getClass());
							AgentsHelper.updateAgentLocation(recordId, coor.getY(), coor.getX(), locationResponseObj.getTechnology());
						}
						else{
							// some error occurred - reset location
							AgentsHelper.updateAgentLocation(recordId, "", "", "");
						}
					}
				}
			}			

			List<MsisdnErrorResponseBean> errorList = locationAnswerResponseObj.getMsisdnError();
			if (errorList != null){
				for (MsisdnErrorResponseBean error: errorList){
					// some error occurred - reset location
					String recordId = agents.get(error.getMsisdn());
					if (recordId != null){
						AgentsHelper.updateAgentLocation(recordId, "", "", "");
					}
				}
			}
		}
		else {
            throw new Exception("Internal error! LocationAnswerResponseBean is NULL.");
        }
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", locationAnswerResponseObj);
		cr.setData(params);
		cr.setTargetPage("sdAgentsLatLongMultiple.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse sendSMS(HashMap params)
	throws Exception{
		// --------------------------------------------------------------------
		String phone = (String) params.get("phone");
		if (phone == null){
			throw new Exception("Internal error - phone is not provided");
		}			
		com.platform.api.Logger.debug("Phone="+phone, this.getClass());
		
		String text = (String) params.get("message");
		if (text == null || text != null && text.trim().isEmpty()){
			throw new Exception("Internal error - message is not provided");
		}			
		com.platform.api.Logger.debug("Message="+text, this.getClass());
		
		GSMSResponse response = AgentsHelper.sendSMS(phone, text);
	
		ControllerResponse cr = new ControllerResponse();
		// --------------------------------------------------------
		params.put("gsms_response", response);
		cr.setData(params);
		cr.setTargetPage("sdAgentsSMSStatus.jsp");
		
		return cr;
	}
	
	/**
	 * get selected agents
	 * 
	 * @param params
	 * @return List of String[], where [0] - record ID, [1] - agent phone number
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	protected Map<String,String> findSelectedAgents(HashMap params) 
	throws Exception{
		
		Map<String,String> selectedAgents = null;
		
		// check single record id first
		String record_id = (String)params.get("id");
		if (record_id != null && record_id.trim().length() > 0 && record_id.matches("[0-9]+")){
			selectedAgents = new HashMap<String,String>();
			selectedAgents.put((String)params.get("phone"), record_id);
		}
		else{
			// muti-select IDs and phones
			String record_id_list = (String)params.get("idlist");
			if (record_id_list != null && record_id_list.trim().length() > 0){
				String[] id_list = record_id_list.split(",");
				String clause = null;
				for (String id: id_list){
					if (clause == null){
						clause = "record_id ="+id;
					}
					else{
						clause += " or record_id="+id;
					}
				}
				
				Result searchResult = Functions.searchRecords("SDAgents", "record_id, phone", clause);
				int resultCode = searchResult.getCode();
				if (resultCode < 0){
				     String msg = "Agents information could not be retrieved";
				     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), this.getClass());
				      throw new Exception(msg);
				}
				else if (resultCode == 0){
					// No records found. 
				     String msg = "Agents information is not found";
				     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), this.getClass());
				      throw new Exception(msg);
				}

				//Records retrieved successfully
				ParametersIterator iterator = searchResult.getIterator();
				selectedAgents = new HashMap<String,String>();

				while(iterator.hasNext()){
					Parameters p = iterator.next();
					com.platform.api.Logger.trace("getRecord: "+p.toString(), this.getClass());
					selectedAgents.put(p.get("phone"), p.get("record_id"));
				}
			}
		}

		if (selectedAgents == null || selectedAgents != null && selectedAgents.size() == 0){
			throw new Exception("Internal error occured while trying to retrieve agent's information");
		}
		
		return selectedAgents;
	}
	
}