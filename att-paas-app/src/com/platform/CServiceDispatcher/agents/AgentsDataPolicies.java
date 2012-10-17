package com.platform.CServiceDispatcher.agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.att.enablers.locaid.LatitudeLongitudeServices;
import com.att.enablers.locaid.LocAidAttributes;
import com.att.enablers.locaid.RegistrationServices;
import com.att.enablers.locaid.response.*;
import com.platform.api.*;

/**
 * Class that contains the data policy methods for object Agents.
 * 
 * @author Marina Bashtovaya
 *
 */
public class AgentsDataPolicies {

	/**
	 * Validate that entered phone starts with 1 (country code) and is a 10 digits number and
	 * normalize it by removing spaces, dashes and parenthesis.<br>
	 * It gets triggered on Add/Update action for the object.<br>
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void normalizePhone(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		String phone = (String) params_map.get("phone");
		
		if (phone != null){
			com.platform.api.Logger.debug("Entered phone="+phone, this);
			
			if (!phone.matches("([ ()0-9]|-)+")){
				throw new Exception("Invalid cell phone format.");
			}
			
			phone = phone.replaceAll("-", "").replaceAll("[ ()]", "");
			
			if (phone.length() == 10){
				phone = "1" + phone;	// auto-add country code
			}
			else if (phone.length() == 11){
				if (!phone.startsWith("1")){
					throw new Exception("Invalid cell phone. Number should start with the country code 1");
				}
			}
			else{
				throw new Exception("Invalid cell phone. 11 digits expected (including the country code 1)");
			}
			
			com.platform.api.Logger.debug("Normalized phone="+phone, this);
			params_map.put("phone", phone);
		}
	}

	/**
	 * Retrieve LIS subscription status of a phone
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void recurringStatusUpdate(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		String recordId = (String) params_map.get("id");
		String phone = (String) params_map.get("phone");
		com.platform.api.Logger.debug("Recurring LIS Subscription Status Update for "+phone, this.getClass());

		//ServiceDispatcherHelper.printAllInputParams(params_map);

		// find LocAid settings
		String[] account = AgentsHelper.getLocAidConfiguration();

		List<String> msisdnList = new ArrayList<String>();
		msisdnList.add(phone);
		
		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		PhoneStatusListResponseBean statusResponseObj = service.getPhoneStatus(msisdnList);

		if (statusResponseObj != null){
			com.platform.api.Logger.debug("Transaction ID=" + statusResponseObj.getTransactionId(), this.getClass());
			
			BaseErrorResponseBean error = statusResponseObj.getError();
			if (error != null) {
				com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
				com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
			} 
			
			List<ComplexMsisdnResponseBean> msisdnResponseList = statusResponseObj.getMsisdnList();
			if (msisdnResponseList != null){
				for (ComplexMsisdnResponseBean msisdnResponseObj : msisdnResponseList) {
					error = msisdnResponseObj.getError();
					if (error != null) {
						com.platform.api.Logger.debug("msisdn=" +msisdnResponseObj.getMsisdn()+
								". Error Code=" + error.getErrorCode() + 
								". Error Msg=" + error.getErrorMessage(), this.getClass());
						// some error occurred - reset the current one
						AgentsHelper.updateAgentStatus(recordId, "");
					} 
					
					List<ClassIDStatusResponseBean> classIdStatusList = msisdnResponseObj.getClassIdList();
					if (classIdStatusList != null){
						for (ClassIDStatusResponseBean classIdStatusObj : classIdStatusList) {
							if (account[7].equalsIgnoreCase(classIdStatusObj.getClassId())){
								error = classIdStatusObj.getError();
								if (error != null) {
									com.platform.api.Logger.debug("msisdn=" +msisdnResponseObj.getMsisdn()+
											". Error Code=" + error.getErrorCode() + 
											". Error Msg=" + error.getErrorMessage(), this.getClass());
									// some error occurred - reset the current one
									AgentsHelper.updateAgentStatus(recordId, "");
								} 
								else{
									com.platform.api.Logger.debug("msisdn=" +msisdnResponseObj.getMsisdn()+". Status=" + classIdStatusObj.getStatus(), this.getClass());
									AgentsHelper.updateAgentStatus(recordId, classIdStatusObj.getStatus());
								}
							}
						}
					}
				}
			}            
        } 
		else {
            throw new Exception("Internal error! GetPhoneStatusResponseBean is NULL.");
        }
	}
	
	/**
	 * Retrieve location of a phone
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	public void recurringLocationUpdate(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();
		//ServiceDispatcherHelper.printAllInputParams(params_map);

		String recordId = (String) params_map.get("id");
		String phone = (String) params_map.get("phone");
		com.platform.api.Logger.debug("Recurring LIS Location Update for "+phone, this.getClass());
		
		// find LocAid settings
		String[] account = AgentsHelper.getLocAidConfiguration();

		LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
		LocationResponseBean locationResponseObj = service.getLocation(phone, LocAidAttributes.CoorType.DECIMAL, 
				LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.Overage.WITH_LIMIT);
		
		if (locationResponseObj != null){
			BaseErrorResponseBean error = locationResponseObj.getError();
			if (error != null) {
				com.platform.api.Logger.debug("msisdn=" +locationResponseObj.getNumber()+
						". Error Code=" + error.getErrorCode() + 
						". Error Msg=" + error.getErrorMessage(), this.getClass());
			} 
			
			CoordinateGeo coor = locationResponseObj.getCoordinateGeo();
			if (coor != null){
				com.platform.api.Logger.debug("msisdn=" +locationResponseObj.getNumber()+
						". Latitude=" + coor.getY() + 
						". Longitude=" + coor.getX() +
						". Technology used=" + locationResponseObj.getTechnology(), this.getClass());
				AgentsHelper.updateAgentLocation(recordId, coor.getY(), coor.getX(), locationResponseObj.getTechnology());
			}
			else{
				com.platform.api.Logger.debug("msisdn=" +locationResponseObj.getNumber()+
						". Location is reset.", this.getClass());
				// some error occurred - reset location
				AgentsHelper.updateAgentLocation(recordId, "", "", "");
			}
		}
		else {
            throw new Exception("Internal error! LocationResponseBean is NULL.");
        }
	}
}