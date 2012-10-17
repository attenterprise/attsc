package com.platform.c2125126633.locaid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.att.enablers.locaid.LatitudeLongitudeServices;
import com.att.enablers.locaid.LocAidAttributes;
import com.att.enablers.locaid.RegistrationServices;
import com.att.enablers.locaid.request.ClassIDList;
import com.att.enablers.locaid.response.LocationAnswerResponseBean;
import com.att.enablers.locaid.response.LocationResponseBean;
import com.att.enablers.locaid.response.PhoneStatusListResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneAllResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneResponseBean;
import com.platform.api.ControllerResponse;
import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

/**
 * @author Magdalena Biała
 * 
 */
public class LocAidManager {

  protected static final String RECORD_ID = "id";
  protected static final String RECORD_ID_LIST = "idlist";
  protected static final String ACTION = "action";

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected ControllerResponse register(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();

    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones to register");
    }

    String command = (String) params.get("command");
    if (command == null || command != null && command.isEmpty()) {
      throw new Exception("Internal error - command is undefined for action register");
    }

    List<ClassIDList> classIdListObj = new ArrayList<ClassIDList>();
    ClassIDList classIdList = new ClassIDList();
    classIdList.setClassId(setup.getClassId());
    List<String> msisdnList = classIdList.getMsisdnList();
    for (String phone : selectedPhones) {
      msisdnList.add(phone);
    }
    classIdListObj.add(classIdList);

    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    SubscribePhoneResponseBean subscribeResponse = service.subscribePhone(command, classIdListObj);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", subscribeResponse);
    cr.setData(params);
    cr.setTargetPage("locAidDisplayRegistrationStatus.jsp");

    return cr;
  }

  public static LocAidSetup getLocAidSetup() throws Exception {
    Result searchResult = Functions.searchRecords("LocAid_Setup", "*", "");
    // "record_id, registration_service_url, location_service_url, address_service_url, geofencing_service_url, login, password, class_id",
    // "");

    int resultCode = searchResult.getCode();
    if (resultCode < 0) {
      String msg = "Configuration information for LocAid could not be retrieved";
      Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidManager.class);
      throw new Exception(msg + ". See log for more details.");
    } else if (resultCode == 0) {
      // No records found.
      String msg = "Please configure account in the LocAid Setup object first";
      Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidManager.class);
      throw new Exception(msg);
    }

    // Records retrieved successfully
    LocAidSetup setup = new LocAidSetup();
    ParametersIterator iterator = searchResult.getIterator();

    while (iterator.hasNext()) {
      Parameters p = iterator.next();
      setup.setRecordId(p.get("record_id"));
      setup.setRegistrationServiceUrl(p.get("registration_service_url"));
      setup.setLocationServiceUrl(p.get("location_service_url"));
      setup.setAddressServiceUrl(p.get("address_service_url"));
      setup.setGeofencingServiceUrl(p.get("geofencing_service_url"));
      setup.setLogin(p.get("login"));
      setup.setPassword(p.get("password"));
      setup.setClassId(p.get("class_id"));
    }

    return setup;
  }

  @SuppressWarnings("rawtypes")
  protected List<String> findSelectedPhones(HashMap params) throws Exception {

    // check single record id first
    String record_id = (String) params.get(RECORD_ID);
    if (record_id != null && record_id.trim().length() > 0 && record_id.matches("[0-9]+")) {
      return getMsisdnList(record_id);
    } else {
      String record_id_list = (String) params.get(RECORD_ID_LIST);
      if (record_id_list != null && record_id_list.trim().length() > 0) {
        return getMsisdnList(record_id_list);
      }
    }

    return null;
  }

  private List<String> getMsisdnList(String ids) throws Exception {
    List<String> msisdnList = null;

    String[] id_list = ids.split(",");
    String clause = "";
    for (int i = 0; i < id_list.length; i++) {
      if (i > 0) {
        clause += " or ";
      }
      clause += "record_id =" + id_list[i];
    }

    Result result = Functions.searchRecords("Engineers", "msisdn", clause);
    int resultCode = result.getCode();
    if (resultCode < 0) {
      // Some error happened.
      String msg = "Engineers information could not be retrieved";
      Logger.error(msg + ":(" + resultCode + ")" + result.getMessage(), LocAidManager.class);

      Functions.throwError(msg + "."); // Error dialog
    } else if (resultCode == 0) {
      // No records found.
    } else {
      // Records retrieved successfully
      msisdnList = new ArrayList<String>();
      ParametersIterator iterator = result.getIterator();
      while (iterator.hasNext()) {
        Parameters params = iterator.next();
        String msisdn = params.get("msisdn");
        msisdnList.add(msisdn);
      }
    }

    return msisdnList;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected ControllerResponse registerAll(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones to register");
    }

    String command = (String) params.get("command");
    if (command == null || command != null && command.isEmpty()) {
      throw new Exception("Internal error - command is undefined for action register");
    }

    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    SubscribePhoneAllResponseBean subscribeResponse = service.subscribePhoneAll(command, selectedPhones);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", subscribeResponse);
    cr.setData(params);
    cr.setTargetPage("locAidDisplayRegistrationAllStatus.jsp");

    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected ControllerResponse status(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones");
    }
    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    PhoneStatusListResponseBean statusResponseObj = service.getPhoneStatus(selectedPhones);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", statusResponseObj);
    cr.setData(params);
    cr.setTargetPage("locAidDisplayGetPhoneStatus.jsp");

    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected ControllerResponse latlongSingle(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones");
    }

    String coorType = (String) params.get("coorType");
    if (coorType == null || coorType != null && coorType.isEmpty()) {
      throw new Exception("Internal error - coordinate type is undefined for action latlong");
    }
    String locationMethod = (String) params.get("locationMethod");
    if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()) {
      throw new Exception("Internal error - location method is undefined for action latlong");
    }
    String overage = (String) params.get("overage");
    if (overage == null || overage != null && overage.isEmpty()) {
      throw new Exception("Internal error - overage is undefined for action latlong");
    }

    // selectedPhones - should be just one
    String phone = selectedPhones.get(0);

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationResponseBean locationResponseObj = service.getLocation(phone, coorType, locationMethod, Integer.valueOf(overage));

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", locationResponseObj);
    cr.setData(params);
    cr.setTargetPage("locAidDisplayLatLong.jsp");

    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected ControllerResponse latlongMultiple(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones");
    }
    String coorType = (String) params.get("coorType");
    if (coorType == null || coorType != null && coorType.isEmpty()) {
      throw new Exception("Internal error - coordinate type is undefined for action latlong");
    }
    String locationMethod = (String) params.get("locationMethod");
    if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()) {
      throw new Exception("Internal error - location method is undefined for action latlong");
    }
    String syncType = (String) params.get("syncType");
    if (syncType == null || syncType != null && syncType.isEmpty()) {
      throw new Exception("Internal error - synchronization type is undefined for action latlong");
    }
    String overage = (String) params.get("overage");
    if (overage == null || overage != null && overage.isEmpty()) {
      throw new Exception("Internal error - overage is undefined for action latlong");
    }

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationAnswerResponseBean locationResponseObj = service.getLocationsX(selectedPhones, coorType, locationMethod, syncType, Integer.valueOf(overage));

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", locationResponseObj);
    // save for answer retrieval later
    if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && locationResponseObj != null) {
      params.put("origTransactionId", String.valueOf(locationResponseObj.getTransactionId()));
    }
    cr.setData(params);
    cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");

    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected ControllerResponse latlongAnswer(HashMap params) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    String transactionId = (String) params.get("origTransactionId");
    if (transactionId == null || transactionId != null && transactionId.isEmpty()) {
      throw new Exception("Internal error - transaction ID is undefined for action latlong");
    }

    // retrieve answer for the asynchronized request of geo-coordinates
    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationAnswerResponseBean locationResponseObj = service.getLocationsAnswer(Long.parseLong(transactionId));

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", locationResponseObj);
    cr.setData(params);
    cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");

    return cr;
  }

}