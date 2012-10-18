package com.platform.c2125126633.locaid;

import java.util.ArrayList;
import java.util.List;

import com.att.enablers.locaid.LatitudeLongitudeServices;
import com.att.enablers.locaid.RegistrationServices;
import com.att.enablers.locaid.request.ClassIDList;
import com.att.enablers.locaid.response.BaseResponseBean;
import com.att.enablers.locaid.response.BaseTransactionResponseBean;
import com.att.enablers.locaid.response.LocationResponseBean;
import com.att.enablers.locaid.response.PhoneStatusListResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneAllResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneResponseBean;
import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

/**
 * Encapsulates methods for dealing with LocAid API.
 * 
 * @author Magdalena Biała
 * 
 */
public class LocAidManager {

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

  public List<String> getMsisdnList(String ids) throws Exception {
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

  /**
   * @param command
   * @param selectedPhones
   * @return
   * @throws Exception
   */
  protected SubscribePhoneResponseBean register(String command, List<String> selectedPhones) throws Exception {
    LocAidSetup setup = getLocAidSetup();

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

    return subscribeResponse;
  }

  /**
   * @param command
   * @param msisdnList
   * @return
   * @throws Exception
   */
  protected SubscribePhoneAllResponseBean registerAll(String command, List<String> msisdnList) throws Exception {
    LocAidSetup setup = getLocAidSetup();

    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    SubscribePhoneAllResponseBean subscribeResponse = service.subscribePhoneAll(command, msisdnList);

    return subscribeResponse;
  }

  /**
   * @param msisdnList
   * @return
   * @throws Exception
   */
  protected BaseResponseBean status(List<String> msisdnList) throws Exception {
    LocAidSetup setup = getLocAidSetup();

    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    PhoneStatusListResponseBean response = service.getPhoneStatus(msisdnList);

    return response;
  }

  /**
   * @param phone
   * @param coorType
   * @param locationMethod
   * @param overage
   * @return
   * @throws Exception
   */
  protected BaseResponseBean latlongSingle(String phone, String coorType, String locationMethod, String overage) throws Exception {
    LocAidSetup setup = getLocAidSetup();

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationResponseBean locationResponseObj = service.getLocation(phone, coorType, locationMethod, Integer.valueOf(overage));

    return locationResponseObj;
  }

  /**
   * @param selectedPhones
   * @param coorType
   * @param locationMethod
   * @param syncType
   * @param overage
   * @return
   * @throws Exception
   */
  protected BaseTransactionResponseBean latlongMultiple(List<String> selectedPhones, String coorType, String locationMethod, String syncType, String overage)
      throws Exception {
    LocAidSetup setup = getLocAidSetup();

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    BaseTransactionResponseBean locationResponseObj = service.getLocationsX(selectedPhones, coorType, locationMethod, syncType, Integer.valueOf(overage));

    return locationResponseObj;
  }

  /**
   * @param transactionId
   * @return
   * @throws Exception
   */
  protected BaseResponseBean latlongAnswer(String transactionId) throws Exception {
    LocAidSetup setup = getLocAidSetup();
    // retrieve answer for the asynchronized request of geo-coordinates
    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    BaseResponseBean locationResponseObj = service.getLocationsAnswer(Long.parseLong(transactionId));

    return locationResponseObj;
  }

}