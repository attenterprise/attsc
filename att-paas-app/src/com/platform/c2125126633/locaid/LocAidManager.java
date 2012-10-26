package com.platform.c2125126633.locaid;

import java.util.ArrayList;
import java.util.List;

import com.att.enablers.locaid.LatitudeLongitudeServices;
import com.att.enablers.locaid.RegistrationServices;
import com.att.enablers.locaid.request.ClassIDList;
import com.att.enablers.locaid.response.BaseResponseBean;
import com.att.enablers.locaid.response.ComplexClassIDResponseBean;
import com.att.enablers.locaid.response.LocationAnswerResponseBean;
import com.att.enablers.locaid.response.LocationResponseBean;
import com.att.enablers.locaid.response.MsisdnStatusResponseBean;
import com.att.enablers.locaid.response.PhoneStatusListResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneAllResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneResponseBean;
import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;
import com.platform.c2125126633.alerts.LocationManager;

/**
 * Encapsulates methods for dealing with LocAid API.
 * 
 * @author Magdalena Biala
 * 
 */
public class LocAidManager extends LocationManager {

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

  protected SubscribePhoneResponseBean register(String command, List<String> selectedPhones) throws Exception {
    LocAidSetup setup = getLocAidSetup();

    Logger.info("Selected phones: " + selectedPhones, LocAidManager.class);
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

    // Update subscription status
    if (subscribeResponse.getError() == null) {
      List<ComplexClassIDResponseBean> classIdListResponse = subscribeResponse.getClassIdList();
      if (classIdListResponse != null) {
        for (ComplexClassIDResponseBean classIdResponse : classIdListResponse) {
          if (classIdResponse.getError() == null) {
            List<MsisdnStatusResponseBean> msisdnStatusList = classIdResponse.getMsisdnList();
            if (msisdnStatusList != null) {
              for (MsisdnStatusResponseBean msisdnStatus : msisdnStatusList) {
                String msisdnReturned = msisdnStatus.getMsisdn();
                if (msisdnStatus.getError() == null) {
                  Result result = Functions.searchRecords("Engineers", "id", "msisdn=" + msisdnReturned);
                  int code = result.getCode();
                  if (code > 0) {
                    String id = result.getIterator().next().get("id");
                    Logger.info("id: " + id, LocAidManager.class);
                    String subscriptionStatus = null;
                    if (msisdnStatus.getStatus().equals("OK")) {
                      if (command.equals("OPTIN")) {
                        subscriptionStatus = "OPTIN_PENDING";
                      } else if (command.equals("YES")) {
                        subscriptionStatus = "OPTIN_COMPLETE";
                      } else if (command.equals("CANCEL")) {
                        subscriptionStatus = "CANCELLED";
                      }
                    }
                    Logger.info("subscriptionStatus: " + subscriptionStatus, LocAidManager.class);
                    Parameters params = new Parameters();
                    params.add("lis_subscription_status_2125126633", subscriptionStatus);
                    Functions.updateRecord("Engineers", id, params);
                  }
                }
              }
            }
          }
        }
      }
    }

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
  protected LocationAnswerResponseBean latlongMultiple(List<String> selectedPhones, String coorType, String locationMethod, String syncType, String overage)
      throws Exception {
    LocAidSetup setup = getLocAidSetup();

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationAnswerResponseBean locationResponseObj = service.getLocationsX(selectedPhones, coorType, locationMethod, syncType, Integer.valueOf(overage));

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

  /**
   * Method for locate the closest engineer.
   * 
   * @param lat
   *          latitude of broken asset
   * @param lng
   *          longitude of broken asset
   * @return msisdn of closest engineer.
   * @throws Exception
   */
  @Override
  public String locateClosestEngineer(String lat, String lng) throws Exception {
    String msisdn = null;
    List<String> msisdnList = searchEngineers("lis_subscription_status_2125126633='OPTIN_COMPLETE'", "msisdn");
    LocationAnswerResponseBean response = latlongMultiple(msisdnList, "DECIMAL", "LEAST_EXPENSIVE", "syn", "1");
    Logger.debug("status: " + response.getStatus(), LocAidManager.class);
    if (response.getError() != null) {
      Logger.error(response.getError().getErrorMessage(), LocAidManager.class);
    } else {
      List<LocationResponseBean> list = response.getLocationResponse();
      double distance = Double.MAX_VALUE;
      for (LocationResponseBean bean : list) {
        if (bean.getError() != null) {
          Logger.error(bean.getError().getErrorMessage(), LocAidManager.class);
        } else {
          Logger.debug("status: " + bean.getStatus(), LocAidManager.class);
          if (bean.getStatus().equals("FOUND")) {
            double actualDistance = distanceFrom(Double.valueOf(lat), Double.valueOf(lng), Double.valueOf(bean.getCoordinateGeo().getY()),
                Double.valueOf(bean.getCoordinateGeo().getX()));
            if (actualDistance < distance) {
              distance = actualDistance;
              msisdn = bean.getNumber();
            }
          }
        }
      }
    }
    return msisdn;
  }

}