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

  public String locateEngineer(String lat, String lng) throws Exception {
    String msisdn = null;
    Result result = Functions.searchRecords("Engineers", "id, msisdn", "lis_subscription_status_2125126633='OPTIN_COMPLETE'");
    int resultCode = result.getCode();
    Logger.info("Result Code: " + resultCode, LocAidManager.class);
    if (resultCode < 0) {
      // Some error happened.
      String msg = "Engineers could not be retrieved";
      Functions.debug(msg + ":\n" + result.getMessage()); // Log details
      Functions.throwError(msg + "."); // Error dialog
    } else if (resultCode == 0) {
      // No records found. Take action according to your business logic
    } else {
      // Records retrieved successfully
      List<String> msisdnList = new ArrayList<String>();
      ParametersIterator iterator = result.getIterator();
      while (iterator.hasNext()) {
        Parameters params = iterator.next();
        msisdnList.add(params.get("msisdn"));
      }
      Logger.debug(msisdnList, LocAidManager.class);
      LocationAnswerResponseBean response = latlongMultiple(msisdnList, "DECIMAL", "LEAST_EXPENSIVE", "syn", "1");
      List<LocationResponseBean> list = response.getLocationResponse();
      double distance = Double.MAX_VALUE;
      Logger.debug("list.size(): " + list.size(), LocAidManager.class);
      for (LocationResponseBean bean : list) {
        if (bean.getStatus().equals("FOUND")) {
          Logger.debug("Status:" + bean.getStatus(), LocAidManager.class);
          double actualDistance = countDistance(Double.valueOf(lat), Double.valueOf(lng), Double.valueOf(bean.getCoordinateGeo().getY()),
              Double.valueOf(bean.getCoordinateGeo().getX()));
          Logger.debug("Msisdn: " + bean.getNumber(), LocAidManager.class);
          Logger.debug("Coordinates: " + bean.getCoordinateGeo().getY() + ", " + bean.getCoordinateGeo().getX(), LocAidManager.class);
          Logger.debug("Distance: " + actualDistance, LocAidManager.class);
          if (actualDistance < distance) {
            distance = actualDistance;
            msisdn = bean.getNumber();
          }
        }
      }
    }
    return msisdn;
  }

  public static double countDistance(double lat1, double long1, double lat2, double long2) {
    // var R = 6371; // km
    double R = 6371;
    // var dLat = (lat2 - lat1).toRad();
    double dLat = Math.toRadians(lat2 - lat1);
    // var dLon = (lon2 - lon1).toRad();
    double dLon = Math.toRadians(long2 - long1);

    // var a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.sin(dLon / 2) *
    double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0) + Math.sin(dLon / 2.0) *
    // Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        Math.sin(dLon / 2.0) * Math.cos(lat1) * Math.cos(lat2);

    // var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    // var d = R * c;
    double d = R * c;
    // return d;

    return d;
  }

}