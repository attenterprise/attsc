package com.platform.c2115417183.locaid;

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
import com.platform.api.Result;

/**
 * Encapsulates methods for dealing with LocAid API.
 * 
 * @author Magdalena Biala
 * 
 */
public class LocAidManager {
  
  public SubscribePhoneResponseBean register(String command, List<String> selectedPhones) throws Exception {
    try {
      LocAidSetup setup = LocAidSetup.getInstance();
  
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
  
      Logger.info("Subscription error: " + (subscribeResponse.getError() != null ? subscribeResponse.getError().getErrorMessage() : "no error"), LocAidManager.class);
      if (subscribeResponse.getError() == null) {
        List<ComplexClassIDResponseBean> classIdListResponse = subscribeResponse.getClassIdList();
        if (classIdListResponse != null) {
          for (ComplexClassIDResponseBean classIdResponse : classIdListResponse) {
            Logger.info("ClassID response error: " + (classIdResponse.getError() != null ? classIdResponse.getError().getErrorMessage() : "no error"), LocAidManager.class);
            
            if (classIdResponse.getError() == null) {              
              List<MsisdnStatusResponseBean> msisdnStatusList = classIdResponse.getMsisdnList();              
              if (msisdnStatusList != null) {
                for (MsisdnStatusResponseBean msisdnStatus : msisdnStatusList) {
                  String msisdnReturned = msisdnStatus.getMsisdn();
                  Logger.info(msisdnReturned + " error: " + (msisdnStatus.getError() != null ? msisdnStatus.getError().getErrorMessage() : "no error"), LocAidManager.class);
                  
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
                      params.add("lis_subscription_status", subscriptionStatus);
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
    catch (Exception e) {
      e.printStackTrace();
      
      Logger.error(e, LocAidManager.class);
      throw e;
    }
  }

  /**
   * @param command
   * @param msisdnList
   * @return
   * @throws Exception
   */
  public SubscribePhoneAllResponseBean registerAll(String command, List<String> msisdnList) throws Exception {
    LocAidSetup setup = LocAidSetup.getInstance();

    RegistrationServices service = new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getLogin(), setup.getPassword());
    SubscribePhoneAllResponseBean subscribeResponse = service.subscribePhoneAll(command, msisdnList);

    return subscribeResponse;
  }

  /**
   * @param msisdnList
   * @return
   * @throws Exception
   */
  public BaseResponseBean status(List<String> msisdnList) throws Exception {
    LocAidSetup setup = LocAidSetup.getInstance();

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
  public BaseResponseBean latlongSingle(String phone, String coorType, String locationMethod, String overage) throws Exception {
    LocAidSetup setup = LocAidSetup.getInstance();

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
  public LocationAnswerResponseBean latlongMultiple(List<String> selectedPhones, String coorType, String locationMethod, String syncType, String overage)
      throws Exception {
    LocAidSetup setup = LocAidSetup.getInstance();

    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    LocationAnswerResponseBean locationResponseObj = service.getLocationsX(selectedPhones, coorType, locationMethod, syncType, Integer.valueOf(overage));

    return locationResponseObj;
  }

  /**
   * @param transactionId
   * @return
   * @throws Exception
   */
  public BaseResponseBean latlongAnswer(String transactionId) throws Exception {
    LocAidSetup setup = LocAidSetup.getInstance();
    // retrieve answer for the asynchronized request of geo-coordinates
    LatitudeLongitudeServices service = new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getLogin(), setup.getPassword(), setup.getClassId());
    BaseResponseBean locationResponseObj = service.getLocationsAnswer(Long.parseLong(transactionId));

    return locationResponseObj;
  }

}