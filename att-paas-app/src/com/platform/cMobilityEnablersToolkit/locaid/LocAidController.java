package com.platform.cMobilityEnablersToolkit.locaid;

import java.util.*;

import com.att.enablers.locaid.*;
import com.att.enablers.locaid.request.*;
import com.att.enablers.locaid.response.*;
import com.platform.api.Controller;
import com.platform.api.ControllerResponse;

/**
 * Controller class that communicates with the AT&T LIS.
 * Action is a parameter that determines the outcome:<br>
 * <ul>
 * <li>"register" to subscribe selected phones with the configured class ID</li>
 * <li>"registerAll" to unsubscribe selected phones from all class IDs for the service account</li>
 * <li>"status" to retrieve the subscription statuses of selected phones</li>
 * <li>"latlongSingle" to retrieve the location of a selected phone</li>
 * <li>"latlongMultiple" to request the locations of selected phones</li>
 * <li>"latlongAnswer" to retrieve the locations of selected phones that were requested asynchronously</li>
 * <li>"addressProfile" to create address profile</li>
 * <li>"address" to request the address of selected phones using the profile</li>
 * <li>"addressAnswer" to retrieve the addresses of selected phones that were requested asynchronously</li>
 * <li>"zipCodeCity" to request the zip-code and city of selected phones</li>
 * <li>"zipCodeCityAnswer" to retrieve the zip-code and city of selected phones that were requested asynchronously</li>
 * <li>"zipCode" to request the zip-code of selected phones</li>
 * <li>"zipCodeAnswer" to retrieve the zip-codes of selected phones that were requested asynchronously</li>
 * <li>"setGeoFence" to set geo-fencing around the selected phone</li>
 * <li>"getGeoFenceAnswer" to retrieve the status of the geo-fence request</li>
 * </ul>
 * 
 * @author Marina Bashtovaya
 * 
 */
public class LocAidController implements Controller
{
	protected static final String RECORD_ID = "id";
	protected static final String RECORD_ID_LIST = "idlist";
	protected static final String ACTION = "action";

	@SuppressWarnings({ "rawtypes"})
	public ControllerResponse execute(HashMap params) 
	throws Exception {
		LocAidHelper.printAllInputParams(params);
		
		ControllerResponse cr = null;
		
		// --------------------------------------------------------------------
		// find LocAid settings
		String[] account = LocAidHelper.getLocAidConfiguration();

		// --------------------------------------------------------------------
		String action = (String) params.get(ACTION);
		if ("register".equalsIgnoreCase(action)){
			cr = register(params, account);
		}
		else if ("registerAll".equalsIgnoreCase(action)){
			cr = registerAll(params, account);
		}
		else if ("status".equalsIgnoreCase(action)){
			cr = status(params, account); 
		}
		else if ("latlongSingle".equalsIgnoreCase(action)){
			cr = latlongSingle(params, account);
		}
		else if ("latlongMultiple".equalsIgnoreCase(action)){
			cr = latlongMultiple(params, account);
		}
		else if ("latlongAnswer".equalsIgnoreCase(action)){
			cr = latlongAnswer(params, account);
		}
		else if ("addressProfile".equalsIgnoreCase(action)){
			cr = addressProfile(params, account);
		}
		else if ("address".equalsIgnoreCase(action)){
			cr = address(params, account);
		}
		else if ("addressAnswer".equalsIgnoreCase(action)){
			cr = addressAnswer(params, account);
		}
		else if ("zipCodeCity".equalsIgnoreCase(action)){
			cr = zipCodeCity(params, account);
		}
		else if ("zipCodeCityAnswer".equalsIgnoreCase(action)){
			cr = zipCodeCityAnswer(params, account);
		}
		else if ("zipCode".equalsIgnoreCase(action)){
			cr = zipCode(params, account);
		}
		else if ("zipCodeAnswer".equalsIgnoreCase(action)){
			cr = zipCodeAnswer(params, account);
		}
		else if ("setGeoFence".equalsIgnoreCase(action)){
			cr = setGeoFence(params, account);
		}
		else if ("getGeoFenceAnswer".equalsIgnoreCase(action)){
			cr = getGeoFenceAnswer(params, account);
		}
		else{
			throw new Exception("Unknown action: "+action);
		}
		
		return cr;
	}
	
	@SuppressWarnings("rawtypes")
	protected List<String> findSelectedPhones(HashMap params) throws Exception{
		
		// check single record id first
		String record_id = (String)params.get(RECORD_ID);
		if (record_id != null && record_id.trim().length() > 0 && record_id.matches("[0-9]+")){
			return LocAidHelper.getPhonesForIds(record_id);
		}
		else{
			String record_id_list = (String)params.get(RECORD_ID_LIST);
			if (record_id_list != null && record_id_list.trim().length() > 0){
				return LocAidHelper.getPhonesForIds(record_id_list);
			}
		}
		
		return null;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse register(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones to register");
		}

		String command = (String) params.get("command");
		if (command == null || command != null && command.isEmpty()){
			throw new Exception("Internal error - command is undefined for action register");
		}

		List<ClassIDList> classIdListObj = new ArrayList<ClassIDList>();
		ClassIDList classIdList = new ClassIDList();
		classIdList.setClassId(account[7]);
		List<String> msisdnList = classIdList.getMsisdnList();
		for (String phone: selectedPhones){
			msisdnList.add(phone);
		}
		classIdListObj.add(classIdList);

		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		SubscribePhoneResponseBean subscribeResponse = service.subscribePhone(command, classIdListObj);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", subscribeResponse);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayRegistrationStatus.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse registerAll(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones to register");
		}

		String command = (String) params.get("command");
		if (command == null || command != null && command.isEmpty()){
			throw new Exception("Internal error - command is undefined for action register");
		}

		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		SubscribePhoneAllResponseBean subscribeResponse = service.subscribePhoneAll(command, selectedPhones);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", subscribeResponse);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayRegistrationAllStatus.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse status(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		PhoneStatusListResponseBean statusResponseObj = service.getPhoneStatus(selectedPhones);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", statusResponseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayGetPhoneStatus.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse latlongSingle(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}

		String coorType = (String) params.get("coorType");
		if (coorType == null || coorType != null && coorType.isEmpty()){
			throw new Exception("Internal error - coordinate type is undefined for action latlong");
		}
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action latlong");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action latlong");
		}

		// selectedPhones - should be just one
		String phone = selectedPhones.get(0);
		
		LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
		LocationResponseBean locationResponseObj = service.getLocation(phone, coorType, locationMethod, 
				Integer.valueOf(overage));
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", locationResponseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayLatLong.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse latlongMultiple(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		String coorType = (String) params.get("coorType");
		if (coorType == null || coorType != null && coorType.isEmpty()){
			throw new Exception("Internal error - coordinate type is undefined for action latlong");
		}
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action latlong");
		}
		String syncType = (String) params.get("syncType");
		if (syncType == null || syncType != null && syncType.isEmpty()){
			throw new Exception("Internal error - synchronization type is undefined for action latlong");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action latlong");
		}

		LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
		LocationAnswerResponseBean locationResponseObj = service.getLocationsX(selectedPhones, coorType, locationMethod, 
				syncType, Integer.valueOf(overage));
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", locationResponseObj);
		// save for answer retrieval later
		if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && locationResponseObj != null){
			params.put("origTransactionId", String.valueOf(locationResponseObj.getTransactionId()));
		} 
		cr.setData(params);
		cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse latlongAnswer(HashMap params, String[] account) throws Exception{
		String transactionId = (String) params.get("origTransactionId");
		if (transactionId == null || transactionId != null && transactionId.isEmpty()){
			throw new Exception("Internal error - transaction ID is undefined for action latlong");
		}

		// retrieve answer for the asynchronized request of geo-coordinates
		LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
		LocationAnswerResponseBean locationResponseObj = service.getLocationsAnswer(Long.parseLong(transactionId));
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", locationResponseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse addressProfile(HashMap params, String[] account) throws Exception{
		String addressProfile = (String) params.get("addressProfile");
		if (addressProfile == null || addressProfile != null && addressProfile.isEmpty()){
			throw new Exception("Internal error - address profile is undefined for action address profile");
		}
		
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		AddressProfileResponseBean addressResponseObj = service.createAddressProfileId(addressProfile);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", addressResponseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayAddressProfile.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse address(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action address");
		}
		String syncType = (String) params.get("syncType");
		if (syncType == null || syncType != null && syncType.isEmpty()){
			throw new Exception("Internal error - synchronization type is undefined for action address");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action address");
		}
		String profileId = (String) params.get("profileId");
		if (profileId == null || profileId != null && profileId.isEmpty()){
			throw new Exception("Internal error - address profile is undefined for action address");
		}
		
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		AddressListResponseBean addressResponseObj = service.getAddressByPhoneNumber(
				locationMethod, syncType, profileId, Integer.valueOf(overage), selectedPhones);

		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", addressResponseObj);
		// save for answer retrieval later
		if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && addressResponseObj != null){
			params.put("origTransactionId", String.valueOf(addressResponseObj.getTransactionId()));
		} 
		cr.setData(params);
		cr.setTargetPage("locAidDisplayAddress.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse addressAnswer(HashMap params, String[] account) throws Exception{
		String transactionId = (String) params.get("origTransactionId");
		if (transactionId == null || transactionId != null && transactionId.isEmpty()){
			throw new Exception("Internal error - transaction ID is undefined for action address");
		}

		// retrieve answer for the asynchronized request of address
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		AddressListResponseBean addressResponseObj = service.getAddressByPhoneNumberAnswer(transactionId);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", addressResponseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayAddress.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse zipCodeCity(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action zipCodeCity");
		}
		String syncType = (String) params.get("syncType");
		if (syncType == null || syncType != null && syncType.isEmpty()){
			throw new Exception("Internal error - synchronization type is undefined for action zipCodeCity");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action zipCodeCity");
		}
		
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		ZipCodeCityListResponseBean responseObj = service.getZipCodeCityByPhoneNumber(
				locationMethod, syncType, Integer.valueOf(overage), selectedPhones);

		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		// save for answer retrieval later
		if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && responseObj != null){
			params.put("origTransactionId", String.valueOf(responseObj.getTransactionId()));
		} 
		cr.setData(params);
		cr.setTargetPage("locAidDisplayZipCodeCity.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse zipCodeCityAnswer(HashMap params, String[] account) throws Exception{
		String transactionId = (String) params.get("origTransactionId");
		if (transactionId == null || transactionId != null && transactionId.isEmpty()){
			throw new Exception("Internal error - transaction ID is undefined for action zipCodeCity");
		}

		// retrieve answer for the asynchronized request of address
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		ZipCodeCityListResponseBean responseObj = service.getZipCodeCityByPhoneNumberAnswer(transactionId);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayZipCodeCity.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse zipCode(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action zipCode");
		}
		String syncType = (String) params.get("syncType");
		if (syncType == null || syncType != null && syncType.isEmpty()){
			throw new Exception("Internal error - synchronization type is undefined for action zipCode");
		}
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action zipCode");
		}
		
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		ZipCodeListResponseBean responseObj = service.getZipCodeByPhoneNumber(
				locationMethod, syncType, Integer.valueOf(overage), selectedPhones);

		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		// save for answer retrieval later
		if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && responseObj != null){
			params.put("origTransactionId", String.valueOf(responseObj.getTransactionId()));
		} 
		cr.setData(params);
		cr.setTargetPage("locAidDisplayZipCode.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse zipCodeAnswer(HashMap params, String[] account) throws Exception{
		String transactionId = (String) params.get("origTransactionId");
		if (transactionId == null || transactionId != null && transactionId.isEmpty()){
			throw new Exception("Internal error - transaction ID is undefined for action zipCode");
		}

		// retrieve answer for the asynchronized request of address
		AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
		ZipCodeListResponseBean responseObj = service.getZipCodeByPhoneNumberAnswer(transactionId);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayZipCode.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse setGeoFence(HashMap params, String[] account) throws Exception{
		// find phone numbers of the records that were selected
		List<String> selectedPhones = findSelectedPhones(params);
		if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0){
			throw new Exception("Select one or more phones");
		}
		String emailResponse = (String) params.get("emailResponse");
		if (emailResponse == null || emailResponse != null && emailResponse.isEmpty()){
			throw new Exception("Internal error - email response is undefined for action setGeoFence");
		}
		String geoName = (String) params.get("geoName");
		if (geoName == null || geoName != null && geoName.isEmpty()){
			throw new Exception("Internal error - geo name is undefined for action setGeoFence");
		}
		List<MsisdnList> msisdnList = new ArrayList<MsisdnList>();
		for (String phone: selectedPhones){
			MsisdnList msisdn = new MsisdnList();
			msisdnList.add(msisdn);
			msisdn.setEmailResponse(emailResponse);
			msisdn.setGeoName(geoName);
			msisdn.setMsisdn(phone);
		}		
		String locationMethod = (String) params.get("locationMethod");
		if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()){
			throw new Exception("Internal error - location method is undefined for action setGeoFence");
		}
		
		String overage = (String) params.get("overage");
		if (overage == null || overage != null && overage.isEmpty()){
			throw new Exception("Internal error - overage is undefined for action setGeoFence");
		}
		
		String language = (String) params.get("language");
		if (language == null || language != null && language.isEmpty()){
			throw new Exception("Internal error - language is undefined for action setGeoFence");
		}
		
		String beginDate = (String) params.get("beginDate");
		if (beginDate == null || beginDate != null && beginDate.isEmpty()){
			throw new Exception("Internal error - begin date is undefined for action setGeoFence");
		}
		
		String endDate = (String) params.get("endDate");
		if (endDate == null || endDate != null && endDate.isEmpty()){
			throw new Exception("Internal error - end date is undefined for action setGeoFence");
		}
		
		String interval = (String) params.get("interval");
		if (interval == null || interval != null && interval.isEmpty()){
			throw new Exception("Internal error - interval is undefined for action setGeoFence");
		}
		
		String format = (String) params.get("format");
		if (format == null || format != null && format.isEmpty()){
			throw new Exception("Internal error - format is undefined for action setGeoFence");
		}
		
		String x = (String) params.get("x");
		if (x == null || x != null && x.isEmpty()){
			throw new Exception("Internal error - x is undefined for action setGeoFence");
		}
		
		String y = (String) params.get("y");
		if (y == null || y != null && y.isEmpty()){
			throw new Exception("Internal error - y is undefined for action setGeoFence");
		}
		
		CoordinateGeoList coordinateGeoList = new CoordinateGeoList();
		coordinateGeoList.setFormat(format);
		List<Coordinate> coordinates = coordinateGeoList.getCoordinate();
		Coordinate coor = new Coordinate();
		coor.setX(x);
		coor.setY(y);
		coordinates.add(coor);
		
		String lengthMeasure = (String) params.get("lengthMeasure");
		if (lengthMeasure == null || lengthMeasure != null && lengthMeasure.isEmpty()){
			throw new Exception("Internal error - lengthMeasure is undefined for action setGeoFence");
		}
		
		String radius = (String) params.get("radius");
		if (radius == null || radius != null && radius.isEmpty()){
			throw new Exception("Internal error - radius is undefined for action setGeoFence");
		}
		
		String speedMeasure = (String) params.get("speedMeasure");
		if (speedMeasure == null || speedMeasure != null && speedMeasure.isEmpty()){
			throw new Exception("Internal error - speedMeasure is undefined for action setGeoFence");
		}
		
		String speedLimited = (String) params.get("speedLimited");
		if (speedLimited == null || speedLimited != null && speedLimited.isEmpty()){
			throw new Exception("Internal error - speedLimited is undefined for action setGeoFence");
		}
		
		String violationType = (String) params.get("violationType");
		if (violationType == null || violationType != null && violationType.isEmpty()){
			throw new Exception("Internal error - violationType is undefined for action setGeoFence");
		}
		
		String violationWarning = (String) params.get("violationWarning");
		if (violationWarning == null || violationWarning != null && violationWarning.isEmpty()){
			throw new Exception("Internal error - violationWarning is undefined for action setGeoFence");
		}

		GeofencingServices service = new GeofencingServices(account[4], account[5], account[6], account[7]); 
		
		BaseTransactionResponseBean responseObj = service.setGeofencing(msisdnList, locationMethod, Integer.parseInt(overage),
				language, beginDate, endDate, interval, coordinateGeoList,
				lengthMeasure, Double.parseDouble(radius), speedMeasure, Integer.parseInt(speedLimited), violationType, violationWarning);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		// save for answer retrieval later
		if (responseObj != null){
			params.put("origTransactionId", String.valueOf(responseObj.getTransactionId()));
		} 
		cr.setData(params);
		cr.setTargetPage("locAidDisplayGeoFencing.jsp");
		
		return cr;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected ControllerResponse getGeoFenceAnswer(HashMap params, String[] account) throws Exception{
		String transactionId = (String) params.get("origTransactionId");
		if (transactionId == null || transactionId != null && transactionId.isEmpty()){
			throw new Exception("Internal error - transaction ID is undefined for action getGeoFenceAnswer");
		}

		GeofencingServices service = new GeofencingServices(account[4], account[5], account[6], account[7]); 
		GeofencingAnswerResponseBean responseObj = service.getGeofencingAnswer(transactionId);
		
		ControllerResponse cr = new ControllerResponse();
		params.put("locaid_response", responseObj);
		cr.setData(params);
		cr.setTargetPage("locAidDisplayGeoFencingResult.jsp");
			
		return cr;
	}
}