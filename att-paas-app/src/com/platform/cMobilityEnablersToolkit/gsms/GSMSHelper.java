package com.platform.cMobilityEnablersToolkit.gsms;

import java.util.*;

import com.platform.api.*;

/**
 * Class that implements misc helper methods
 * 
 * @author Marina Bashtovaya
 *
 */
public class GSMSHelper {
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
	 * get SMS configuration
	 * 
	 * @return [0] - record id, [1] - URL, [2] - user, [3] - password, [4] - short code, [5] - SMS receiver site URL
	 * @throws Exception
	 */
	public static String[] getSMSConfiguration() throws Exception{
		Result searchResult = Functions.searchRecords("GSMS_Setup", "record_id, url, field_user, password, shortcode, receiversite", "");

		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		     String msg = "Configuration information could not be retrieved";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), GSMSHelper.class);
		      throw new Exception(msg+". See log for more details.");
		}
		else if (resultCode == 0){
			// No records found. 
		     String msg = "Configure the account in GSMS Setup object first";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), GSMSHelper.class);
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
			com.platform.api.Logger.trace("getRecord: "+p.toString(), GSMSHelper.class);
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
		
		Result searchResult = Functions.searchRecords("GSMSOverview", "phone", clause);
		int resultCode = searchResult.getCode();
		if (resultCode < 0){
		     String msg = "GSMSOverview information could not be retrieved";
		     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), GSMSHelper.class);
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
			com.platform.api.Logger.trace("getRecord: "+p.toString(), GSMSHelper.class);
			
			if (phones == null){
				phones = new ArrayList<String>(); 
			}
			phones.add(p.get("phone"));
		}
		
		return phones;
	}
	
	/**
	 * check if the phone is a registered in the platform
	 * 
	 * @param phone cell phone to check
	 * @return true, if the phone is known, false otherwise
	 * @throws Exception
	 */
	public static boolean isValidSender(String phone) throws Exception{
		com.platform.api.Logger.trace("isValidSender: phone to check '"+phone+"'", GSMSHelper.class);
		if (phone != null){
			Result searchResult = Functions.searchRecords("GSMSOverview", "record_id", "phone = '"+phone.trim()+"'");
			int resultCode = searchResult.getCode();
			if (resultCode < 0){
			     String msg = "GSMSOverview information could not be retrieved";
			     com.platform.api.Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), GSMSHelper.class);
			      throw new Exception(searchResult.getMessage());
			}
			else if (resultCode == 0){
				// No records found. 
			     com.platform.api.Logger.trace("No record found:(" + resultCode + ")" + searchResult.getMessage(), GSMSHelper.class);
				return false;
			}
			
			return true;
		}
		
		return false;
	}
	

}