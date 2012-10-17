package com.platform.cMobilityEnablersToolkit.locaid;

import java.util.*;

import com.platform.api.*;

/**
 * Class that implements misc helper methods
 * 
 * @author Marina Bashtovaya
 *
 */
public class LocAidHelper {
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
		Result searchResult = Functions.searchRecords("LocAid_Setup", 
				"record_id, registration_service_url, location_service_url, address_service_url, geofencing_service_url, "+
				"login, password, class_id", "");

		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		    String msg = "Configuration information could not be retrieved";
		    com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidHelper.class);
		    throw new Exception(msg+". See log for more details.");
		}
		else if (resultCode == 0){
			// No records found. 
		    String msg = "Configure account in the LocAid Setup object first";
		    com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidHelper.class);
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
			com.platform.api.Logger.trace("getRecord: "+p.toString(), LocAidHelper.class);
		}
		
		return recordInfo;
	}
	
	/**
	 * get phone numbers for record ids selected
	 * 
	 * @param ids comma-separated list of record ids
	 * @return list of cell phones
	 * @throws Exception
	 */
	public static List<String> getPhonesForIds(String ids) throws Exception{
		List<String> phones = null;
		
		String[] id_list = ids.split(",");
		String clause = null;
		for (String id: id_list){
			if (clause == null){
				clause = "record_id ="+id;
			}
			else{
				clause += " or record_id="+id;
			}
		}
		
		Result searchResult = Functions.searchRecords("LocAidOverview", "phone", clause);
		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		     String msg = "LocAidOverview information could not be retrieved";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidHelper.class);
		      throw new Exception(searchResult.getMessage());
		}
		else if (resultCode == 0){
			// No records found. 
			return null;
		}

		//Records retrieved successfully
		ParametersIterator iterator = searchResult.getIterator();

		while(iterator.hasNext()){
			Parameters p = iterator.next();
			com.platform.api.Logger.trace("getRecord: "+p.toString(), LocAidHelper.class);
			
			if (phones == null){
				phones = new ArrayList<String>(); 
			}
			phones.add(p.get("phone"));
		}
		
		return phones;
	}

	
}