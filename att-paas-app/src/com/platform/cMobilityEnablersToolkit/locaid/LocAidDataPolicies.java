package com.platform.cMobilityEnablersToolkit.locaid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.att.enablers.locaid.*;
import com.att.enablers.locaid.request.*;
import com.att.enablers.locaid.response.*;
import com.platform.api.Parameters;

/**
 * Class that implements data policies used by LocAid Overview object.
 * 
 * @author Marina Bashtovaya
 *
 */
public class LocAidDataPolicies {
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
	 * subscribe the phone with LIS
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void subscribe(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String subscribe = (String) params_map.get("subscribe");
		if (phone != null && "1".equalsIgnoreCase(subscribe)){
			sendSubscriptionCommand(phone, LocAidAttributes.Command.OPTIN);
			params_map.put("subscribe", "0"); // reset the checkbox 
		}
	}

	/**
	 * confirm the subscription of the phone with LIS
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void confirmSubscription(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String confirm = (String) params_map.get("confirm");
		if (phone != null && "1".equalsIgnoreCase(confirm)){
			sendSubscriptionCommand(phone, LocAidAttributes.Command.YES);
			params_map.put("confirm", "0"); // reset the checkbox 
		}
	}

	/**
	 * unsubscribe the phone from LIS
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void unsubscribe(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String unsubscribe = (String) params_map.get("unsubscribe");
		if (phone != null && "1".equalsIgnoreCase(unsubscribe)){
			sendSubscriptionCommand(phone, LocAidAttributes.Command.CANCEL);
			params_map.put("unsubscribe", "0"); // reset the checkbox 
		}
	}

	protected void sendSubscriptionCommand(String phone, String command)
	throws Exception{
		// find LocAid settings
		String[] account = LocAidHelper.getLocAidConfiguration();
	
		List<ClassIDList> classIdListObj = new ArrayList<ClassIDList>();
		ClassIDList classIdList = new ClassIDList();
		classIdList.setClassId(account[7]);
		List<String> msisdnList = classIdList.getMsisdnList();
		msisdnList.add(phone);
		classIdListObj.add(classIdList);

		RegistrationServices service = new RegistrationServices(account[1], account[5], account[6]); 
		SubscribePhoneResponseBean subscribeResponseObj = service.subscribePhone(command, classIdListObj);
		
		if (subscribeResponseObj != null){
			com.platform.api.Logger.debug("Transaction ID=" + subscribeResponseObj.getTransactionId(), this.getClass());
			
			BaseErrorResponseBean error = subscribeResponseObj.getError();
			if (error != null) {
				com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
				com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
			} 
			
			List<ComplexClassIDResponseBean> classIdListResponse = subscribeResponseObj.getClassIdList();
			if (classIdListResponse != null){
				for (ComplexClassIDResponseBean classIdResponse : classIdListResponse) {
					com.platform.api.Logger.debug("classId=" + classIdResponse.getClassId(), this.getClass());
					
					error = classIdResponse.getError();
					if (error != null) {
						com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
						com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
					} 
					
					List<MsisdnStatusResponseBean> msisdnStatusList = classIdResponse.getMsisdnList();
					if (msisdnStatusList != null){
						for (MsisdnStatusResponseBean msisdnStatus : msisdnStatusList) {
							com.platform.api.Logger.debug("Msisdn=" + msisdnStatus.getMsisdn(), this.getClass());
							com.platform.api.Logger.debug("Status=" + msisdnStatus.getStatus(), this.getClass());

							error = msisdnStatus.getError();
							if (error != null) {
								com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
								com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
							}
						}
					}
				}
			}
		}            
		else{
            throw new Exception("Internal error! SubscribePhoneResponseBean is NULL.");
        }
	}
	
	/**
	 * get LIS subscription status of the phone 
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void getStatus(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String status = (String) params_map.get("get_status");
		if (phone != null && "1".equalsIgnoreCase(status)){
			params_map.put("get_status", "0"); // reset the checkbox 

			List<String> msisdnList = new ArrayList<String>();
			msisdnList.add(phone);
			
			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

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
						com.platform.api.Logger.debug("msisdn=" +msisdnResponseObj.getMsisdn(), this.getClass());
	
						error = msisdnResponseObj.getError();
						if (error != null) {
							com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
							com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
						} 
						
						List<ClassIDStatusResponseBean> classIdStatusList = msisdnResponseObj.getClassIdList();
						if (classIdStatusList != null){
							for (ClassIDStatusResponseBean classIdStatusObj : classIdStatusList) {
								com.platform.api.Logger.debug("ClassId=" + classIdStatusObj.getClassId(), this.getClass());
								
								error = classIdStatusObj.getError();
								if (error != null) {
									com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
									com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
								} 
								
								com.platform.api.Logger.debug("Status=" + classIdStatusObj.getStatus(), this.getClass());
							}
						}
					}
				}            
	        } 
			else {
	            throw new Exception("Internal error! GetPhoneStatusResponseBean is NULL.");
	        }
		}
	}

	/**
	 * get the current location of the phone 
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void locationLookup(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String latlong = (String) params_map.get("latlong");
		if (phone != null && "1".equalsIgnoreCase(latlong)){
			params_map.put("latlong", "0"); // reset the checkbox 

			List<String> msisdnList = new ArrayList<String>();
			msisdnList.add(phone);
			
			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

			LatitudeLongitudeServices service = new LatitudeLongitudeServices(account[2], account[5], account[6], account[7]); 
			LocationResponseBean locationResponseObj = service.getLocation(
					phone, LocAidAttributes.CoorType.DECIMAL, LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.Overage.WITHOUT_LIMIT);

			if (locationResponseObj != null){
				com.platform.api.Logger.debug("number=" + locationResponseObj.getNumber(), this.getClass());
				com.platform.api.Logger.debug("status=" + locationResponseObj.getStatus(), this.getClass());
				com.platform.api.Logger.debug("direction=" + locationResponseObj.getDirection(), this.getClass());
				com.platform.api.Logger.debug("speed=" + locationResponseObj.getSpeed(), this.getClass());
				com.platform.api.Logger.debug("technology=" + locationResponseObj.getTechnology(), this.getClass());

				BaseErrorResponseBean error = locationResponseObj.getError();
				if (error != null) {
					com.platform.api.Logger.debug("locationResponse-Error Code=" + error.getErrorCode(), this.getClass());
					com.platform.api.Logger.debug("locationResponse-Error Msg=" + error.getErrorMessage(), this.getClass());
				}
				CoordinateGeo coor = locationResponseObj.getCoordinateGeo();
				if (coor != null){
					com.platform.api.Logger.debug("Coor-format=" + coor.getFormat(), this.getClass());
					com.platform.api.Logger.debug("Coor-y=" + coor.getY(), this.getClass());
					com.platform.api.Logger.debug("Coor-x=" + coor.getX(), this.getClass());
				}
				Geometry geometry = locationResponseObj.getGeometry();
				if (geometry != null){
					com.platform.api.Logger.debug("Geometry-inRadius=" + geometry.getInRadius(), this.getClass());
					com.platform.api.Logger.debug("Geometry-outRadius=" + geometry.getOutRadius(), this.getClass());
					com.platform.api.Logger.debug("Geometry-radius=" + geometry.getRadius(), this.getClass());
					com.platform.api.Logger.debug("Geometry-startAngle=" + geometry.getStartAngle(), this.getClass());
					com.platform.api.Logger.debug("Geometry-stopAngle=" + geometry.getStopAngle(), this.getClass());
					com.platform.api.Logger.debug("Geometry-type=" + geometry.getType(), this.getClass());
				}
				LocationTime locationTime = locationResponseObj.getLocationTime();
				if (locationTime != null){
					com.platform.api.Logger.debug("LocationTime-time=" + locationTime.getTime(), this.getClass());
					com.platform.api.Logger.debug("LocationTime-utc=" + locationTime.getUtc(), this.getClass());
				}
			}
			else {
	            throw new Exception("Internal error! LocationResponseBean is NULL.");
	        }
		}
	}

	/**
	 * create a profile and get address of the current phone's location
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addressLookup(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String address = (String) params_map.get("address");
		if (phone != null && "1".equalsIgnoreCase(address)){
			String addressProfile = (String) params_map.get("profile");
			if (addressProfile == null || addressProfile != null && addressProfile.isEmpty()){
				throw new Exception("Address profile needs to be defined in order to get the address");
			}
			
			params_map.put("address", "0"); // reset the checkbox 

			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

			AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
			String profileId = null;
			AddressProfileResponseBean profileResponseObj = service.createAddressProfileId(addressProfile);
			if (profileResponseObj != null){
				profileId = profileResponseObj.getAddressProfileId();

				com.platform.api.Logger.debug("Transaction ID=" + profileResponseObj.getTransactionId(), this.getClass());
				
				BaseErrorResponseBean error = profileResponseObj.getError();
				if (error != null) {
					com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
					com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
				} 
				else{
					List<String> selectedPhones = new ArrayList<String>();
					selectedPhones.add(phone);
					AddressListResponseBean addressResponseObj = service.getAddressByPhoneNumber(
							LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.SyncType.SYNC,
							String.valueOf(profileId), LocAidAttributes.Overage.WITHOUT_LIMIT, selectedPhones);
					if (addressResponseObj != null){
						com.platform.api.Logger.debug("Transaction ID=" + addressResponseObj.getTransactionId(), this.getClass());
						com.platform.api.Logger.debug("Status=" + addressResponseObj.getStatus(), this.getClass());

						error = profileResponseObj.getError();
						if (error != null) {
							com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
							com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
						} 

						List<MsisdnErrorResponseBean> msisdn_errors = addressResponseObj.getMsisdnErrorList();
						if (msisdn_errors != null){
							for (MsisdnErrorResponseBean msisdn_error: msisdn_errors){
								com.platform.api.Logger.debug("MSISDN=" + msisdn_error.getMsisdn(), this.getClass());
								com.platform.api.Logger.debug("Error Code=" + msisdn_error.getErrorCode(), this.getClass());
								com.platform.api.Logger.debug("Error Msg=" + msisdn_error.getErrorMessage(), this.getClass());
							}
						}
						List<AddressByPhoneResponseBean> addressList = addressResponseObj.getAddressResponseList();
						if (addressList != null){
							for (AddressByPhoneResponseBean addressObj: addressList){
								com.platform.api.Logger.debug("MSISDN=" + addressObj.getMsisdn(), this.getClass());
								com.platform.api.Logger.debug("Profile=" + addressObj.getProfile(), this.getClass());
								com.platform.api.Logger.debug("Status=" + addressObj.getStatus(), this.getClass());
								com.platform.api.Logger.debug("Street number=" + addressObj.getStreetNumber(), this.getClass());
								com.platform.api.Logger.debug("Street name=" + addressObj.getStreetName(), this.getClass());
								com.platform.api.Logger.debug("City=" + addressObj.getCity(), this.getClass());
								com.platform.api.Logger.debug("State=" + addressObj.getStateName(), this.getClass());
								com.platform.api.Logger.debug("Zipcode=" + addressObj.getZipCode(), this.getClass());
								com.platform.api.Logger.debug("County=" + addressObj.getCounty(), this.getClass());
								com.platform.api.Logger.debug("Country=" + addressObj.getCountry(), this.getClass());
								com.platform.api.Logger.debug("Map=" + addressObj.getMap(), this.getClass());
							}
						}
					}
				}
				
			}
			else {
	            throw new Exception("Internal error! AddressProfileResponseBean is NULL.");
	        }
		}
	}

	/**
	 * get a zip-code of the current phone's location
	 * 
	 * @param params
	 * @throws Exception
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void zipCodeLookup(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String zipcode = (String) params_map.get("zipcode");
		if (phone != null && "1".equalsIgnoreCase(zipcode)){
			params_map.put("zipcode", "0"); // reset the checkbox 

			List<String> selectedPhones = new ArrayList<String>();
			selectedPhones.add(phone);

			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

			AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
			ZipCodeListResponseBean addressResponseObj = service.getZipCodeByPhoneNumber(
					LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.SyncType.SYNC, 
					LocAidAttributes.Overage.WITHOUT_LIMIT, selectedPhones);
			if (addressResponseObj != null){
				com.platform.api.Logger.debug("Transaction ID=" + addressResponseObj.getTransactionId(), this.getClass());
				com.platform.api.Logger.debug("Status=" + addressResponseObj.getStatus(), this.getClass());
				
				BaseErrorResponseBean error = addressResponseObj.getError();
				if (error != null) {
					com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
					com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
				} 

				List<MsisdnErrorResponseBean> msisdn_errors = addressResponseObj.getMsisdnErrorList();
				if (msisdn_errors != null){
					for (MsisdnErrorResponseBean msisdn_error: msisdn_errors){
						com.platform.api.Logger.debug("MSISDN=" + msisdn_error.getMsisdn(), this.getClass());
						com.platform.api.Logger.debug("Error Code=" + msisdn_error.getErrorCode(), this.getClass());
						com.platform.api.Logger.debug("Error Msg=" + msisdn_error.getErrorMessage(), this.getClass());
					}
				}

				List<ZipCodeResponseBean> addressList = addressResponseObj.getZipCodeList();
				if (addressList != null){
					for (ZipCodeResponseBean addressObj: addressList){
						com.platform.api.Logger.debug("MSISDN=" + addressObj.getMsisdn(), this.getClass());
						com.platform.api.Logger.debug("Status=" + addressObj.getStatus(), this.getClass());
						com.platform.api.Logger.debug("Zipcode=" + addressObj.getZipCode(), this.getClass());
					}
				}
			}
			else {
	            throw new Exception("Internal error! ZipCodeListResponseBean is NULL.");
	        }
		}
	}

	/**
	 * get a zip-code and city of the current phone's location
	 * 
	 * @param params
	 * @throws Exception
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void zipCodeCityLookup(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String zipcodecity = (String) params_map.get("zipcodecity");
		if (phone != null && "1".equalsIgnoreCase(zipcodecity)){
			params_map.put("zipcodecity", "0"); // reset the checkbox 

			List<String> selectedPhones = new ArrayList<String>();
			selectedPhones.add(phone);

			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

			AddressServices service = new AddressServices(account[3], account[5], account[6], account[7]); 
			ZipCodeCityListResponseBean addressResponseObj = service.getZipCodeCityByPhoneNumber(
					LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.SyncType.SYNC, 
					LocAidAttributes.Overage.WITHOUT_LIMIT, selectedPhones);
			if (addressResponseObj != null){
				com.platform.api.Logger.debug("Transaction ID=" + addressResponseObj.getTransactionId(), this.getClass());
				com.platform.api.Logger.debug("Status=" + addressResponseObj.getStatus(), this.getClass());
				
				BaseErrorResponseBean error = addressResponseObj.getError();
				if (error != null) {
					com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
					com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
				} 

				List<MsisdnErrorResponseBean> msisdn_errors = addressResponseObj.getMsisdnErrorList();
				if (msisdn_errors != null){
					for (MsisdnErrorResponseBean msisdn_error: msisdn_errors){
						com.platform.api.Logger.debug("MSISDN=" + msisdn_error.getMsisdn(), this.getClass());
						com.platform.api.Logger.debug("Error Code=" + msisdn_error.getErrorCode(), this.getClass());
						com.platform.api.Logger.debug("Error Msg=" + msisdn_error.getErrorMessage(), this.getClass());
					}
				}

				List<ZipCodeCityResponseBean> addressList = addressResponseObj.getZipCodeCityList();
				if (addressList != null){
					for (ZipCodeCityResponseBean addressObj: addressList){
						com.platform.api.Logger.debug("MSISDN=" + addressObj.getMsisdn(), this.getClass());
						com.platform.api.Logger.debug("Status=" + addressObj.getStatus(), this.getClass());
						com.platform.api.Logger.debug("Zipcode=" + addressObj.getZipCode(), this.getClass());
						com.platform.api.Logger.debug("City=" + addressObj.getCity(), this.getClass());
					}
				}
			}
			else {
	            throw new Exception("Internal error! ZipCodeCityListResponseBean is NULL.");
	        }
		}
	}

	/**
	 * set a geo-fence for the phone and get the result back once
	 * 
	 * @param params
	 * @throws Exception
	 */	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setGeofence(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		//LocAidHelper.printAllInputParams(params_map);
		
		String phone = (String) params_map.get("phone");
		String geofence = (String) params_map.get("geofence");
		if (phone != null && "1".equalsIgnoreCase(geofence)){
			String emailResponse = (String) params.get("emailResponse");
			if (emailResponse == null || emailResponse != null && emailResponse.isEmpty()){
				throw new Exception("Internal error - email response is undefined for action setGeoFence");
			}
			String geoName = (String) params.get("geoName");
			if (geoName == null || geoName != null && geoName.isEmpty()){
				throw new Exception("Internal error - geo name is undefined for action setGeoFence");
			}
			String beginDate = (String) params.get("beginDate");
			if (beginDate == null || beginDate != null && beginDate.isEmpty()){
				throw new Exception("Internal error - begin date is undefined for action setGeoFence");
			}
			
			String endDate = (String) params.get("endDate");
			if (endDate == null || endDate != null && endDate.isEmpty()){
				throw new Exception("Internal error - end date is undefined for action setGeoFence");
			}
			
			String interval = (String) params.get("field_interval");
			if (interval == null || interval != null && interval.isEmpty()){
				throw new Exception("Internal error - interval is undefined for action setGeoFence");
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
			coordinateGeoList.setFormat(LocAidAttributes.CoorType.DECIMAL);
			List<Coordinate> coordinates = coordinateGeoList.getCoordinate();
			Coordinate coor = new Coordinate();
			coor.setX(x);
			coor.setY(y);
			coordinates.add(coor);
			
			String radius = (String) params.get("radius");
			if (radius == null || radius != null && radius.isEmpty()){
				throw new Exception("Internal error - radius is undefined for action setGeoFence");
			}
			
			String speedLimited = (String) params.get("speedLimited");
			if (speedLimited == null || speedLimited != null && speedLimited.isEmpty()){
				throw new Exception("Internal error - speedLimited is undefined for action setGeoFence");
			}
			
			String violationType = (String) params.get("violationType");
			if (violationType == null || violationType != null && violationType.isEmpty()){
				throw new Exception("Internal error - violationType is undefined for action setGeoFence");
			}
			
			List<MsisdnList> msisdnList = new ArrayList<MsisdnList>();
			MsisdnList msisdn = new MsisdnList();
			msisdnList.add(msisdn);
			msisdn.setEmailResponse(emailResponse);
			msisdn.setGeoName(geoName);
			msisdn.setMsisdn(phone);
			
			params_map.put("geofence", "0"); // reset the checkbox 

			// find LocAid settings
			String[] account = LocAidHelper.getLocAidConfiguration();

			GeofencingServices service = new GeofencingServices(account[4], account[5], account[6], account[7]); 

			BaseTransactionResponseBean responseObj = service.setGeofencing(msisdnList, 
					LocAidAttributes.LocationMethod.LEAST_EXPENSIVE, LocAidAttributes.Overage.WITHOUT_LIMIT,
					LocAidAttributes.Language.ENGLISH, beginDate, endDate, 
					interval, coordinateGeoList,
					LocAidAttributes.LengthMeasure.MILES, Double.parseDouble(radius), 
					LocAidAttributes.SpeedMeasure.MI_H, Integer.parseInt(speedLimited), 
					violationType, LocAidAttributes.ViolationWarning.ONCE);

			if (responseObj != null){
				Long transactionId = responseObj.getTransactionId();

				BaseErrorResponseBean error = responseObj.getError();
				if (error != null) {
					com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
					com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
				} 
				else{
					GeofencingAnswerResponseBean answerResponse = service.getGeofencingAnswer(String.valueOf(transactionId));
					if (answerResponse != null){
						com.platform.api.Logger.debug("Transaction ID=" + answerResponse.getTransactionId(), this.getClass());
						//com.platform.api.Logger.debug("Status=" + answerResponse.getStatus(), this.getClass());
						
						error = answerResponse.getError();
						if (error != null) {
							com.platform.api.Logger.debug("Error Code=" + error.getErrorCode(), this.getClass());
							com.platform.api.Logger.debug("Error Msg=" + error.getErrorMessage(), this.getClass());
						} 

						List<MsisdnErrorResponseBean> msisdn_errors = answerResponse.getMsisdnError();
						if (msisdn_errors != null){
							for (MsisdnErrorResponseBean msisdn_error: msisdn_errors){
								com.platform.api.Logger.debug("MSISDN=" + msisdn_error.getMsisdn(), this.getClass());
								com.platform.api.Logger.debug("Error Code=" + msisdn_error.getErrorCode(), this.getClass());
								com.platform.api.Logger.debug("Error Msg=" + msisdn_error.getErrorMessage(), this.getClass());
							}
						}
						List<GeofencingMsisdnBean> geofencingMsisdnList = answerResponse.getMsisdnLocation();
						if (geofencingMsisdnList != null){
							for (GeofencingMsisdnBean geofencingMsisdn: geofencingMsisdnList){
								com.platform.api.Logger.debug("MSISDN=" + geofencingMsisdn.getMsisdn(), this.getClass());
								com.platform.api.Logger.debug("Error Code=" + geofencingMsisdn.getErrorCode(), this.getClass());
								com.platform.api.Logger.debug("Error Msg=" + geofencingMsisdn.getErrorMessage(), this.getClass());

								List<GeofencingLocationResponseBean> locationList = geofencingMsisdn.getGeofencingLocationResponse();
								if (locationList != null){
									for (GeofencingLocationResponseBean location: locationList){
										com.platform.api.Logger.debug("number=" + location.getNumber(), this.getClass());
										com.platform.api.Logger.debug("status=" + location.getStatus(), this.getClass());
										com.platform.api.Logger.debug("schedule time=" + location.getScheduleTime(), this.getClass());
										com.platform.api.Logger.debug("violation type=" + location.getViolationType(), this.getClass());
										com.platform.api.Logger.debug("technology=" + location.getTechnology(), this.getClass());
										com.platform.api.Logger.debug("direction=" + location.getDirection(), this.getClass());
										com.platform.api.Logger.debug("speed=" + location.getSpeed(), this.getClass());
										
										error = location.getError();
										if (error != null) {
											com.platform.api.Logger.debug("locationResponse-Error Code=" + error.getErrorCode(), this.getClass());
											com.platform.api.Logger.debug("locationResponse-Error Msg=" + error.getErrorMessage(), this.getClass());
										}
										CoordinateGeo coorGeo = location.getCoordinateGeo();
										if (coorGeo != null){
											com.platform.api.Logger.debug("Coor-format=" + coorGeo.getFormat(), this.getClass());
											com.platform.api.Logger.debug("Coor-y=" + coorGeo.getY(), this.getClass());
											com.platform.api.Logger.debug("Coor-x=" + coorGeo.getX(), this.getClass());
										}
										Geometry geometry = location.getGeometry();
										if (geometry != null){
											com.platform.api.Logger.debug("Geometry-inRadius=" + geometry.getInRadius(), this.getClass());
											com.platform.api.Logger.debug("Geometry-outRadius=" + geometry.getOutRadius(), this.getClass());
											com.platform.api.Logger.debug("Geometry-radius=" + geometry.getRadius(), this.getClass());
											com.platform.api.Logger.debug("Geometry-startAngle=" + geometry.getStartAngle(), this.getClass());
											com.platform.api.Logger.debug("Geometry-stopAngle=" + geometry.getStopAngle(), this.getClass());
											com.platform.api.Logger.debug("Geometry-type=" + geometry.getType(), this.getClass());
										}
										LocationTime locationTime = location.getLocationTime();
										if (locationTime != null){
											com.platform.api.Logger.debug("LocationTime-time=" + locationTime.getTime(), this.getClass());
											com.platform.api.Logger.debug("LocationTime-utc=" + locationTime.getUtc(), this.getClass());
										}
									}
								}
							}
						}
					}
					else {
			            throw new Exception("Internal error! GeofencingAnswerResponseBean is NULL.");
			        }
				}				
			}
			else {
	            throw new Exception("Internal error! BaseTransactionResponseBean is NULL.");
	        }			
		}
	}
}