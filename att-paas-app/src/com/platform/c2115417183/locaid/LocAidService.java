package com.platform.c2115417183.locaid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.enablers.locaid.LatitudeLongitudeServices;
import com.att.enablers.locaid.RegistrationServices;
import com.att.enablers.locaid.request.ClassIDList;
import com.att.enablers.locaid.response.BaseErrorResponseBean;
import com.att.enablers.locaid.response.ComplexClassIDResponseBean;
import com.att.enablers.locaid.response.LocationAnswerResponseBean;
import com.att.enablers.locaid.response.LocationResponseBean;
import com.att.enablers.locaid.response.MsisdnErrorResponseBean;
import com.att.enablers.locaid.response.MsisdnStatusResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneResponseBean;
import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.c2115417183.engineers.EngineersDao;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationService;

public class LocAidService implements LocationService {

  private static final String ALREADY_SUBSCRIBED = "00013";
  
  private EngineersDao engineersDao = new EngineersDao();
  private final LocAidSetup setup;

  public LocAidService(LocAidSetup setup) {
    this.setup = setup;
  }

  @Override
  public void subscribe(String msisdn) throws Exception {
    manageSubscription(msisdn, "OPTIN", "SUBSCRIBED");
  }

  @Override
  public void unsubscribe(String msisdn) throws Exception {
    manageSubscription(msisdn, "CANCEL", "NOT SUBSCRIBED");
  }

  private void manageSubscription(String msisdn, String command, String status) throws Exception {
    List<ClassIDList> classIdListObj = new ArrayList<ClassIDList>();
    ClassIDList classIdList = new ClassIDList();
    classIdList.setClassId(setup.getClassId());
    List<String> msisdnList = classIdList.getMsisdnList();
    msisdnList.add(msisdn);
    classIdListObj.add(classIdList);
    
    RegistrationServices service = createRegistrationServices();    
    SubscribePhoneResponseBean subscribeResponse = service.subscribePhone(command, classIdListObj);
    
    if (subscribeResponse.getError() != null) {
      Logger.error("Subscription error: " + subscribeResponse.getError().getErrorCode() + ". " + subscribeResponse.getError().getErrorMessage(),
          LocAidService.class);
      throw new Exception(subscribeResponse.getError().getErrorMessage());
    } else {
      List<ComplexClassIDResponseBean> classIdListResponse = subscribeResponse.getClassIdList();
      if (classIdListResponse != null) {
        for (ComplexClassIDResponseBean classIdResponse : classIdListResponse) {
          if (classIdResponse.getError() != null) {
            logError("ClassID response", classIdResponse.getError());
            
            throw new Exception(classIdResponse.getError().getErrorMessage());
          } else {
            List<MsisdnStatusResponseBean> msisdnStatusList = classIdResponse.getMsisdnList();
            if (msisdnStatusList != null) {
              for (MsisdnStatusResponseBean msisdnStatus : msisdnStatusList) {
                String msisdnReturned = msisdnStatus.getMsisdn();
                if (msisdnStatus.getError() != null && !ALREADY_SUBSCRIBED.equals(msisdnStatus.getError().getErrorCode())) {
                  logError(msisdnReturned, msisdnStatus.getError());
                  throw new Exception(msisdnStatus.getError().getErrorMessage());
                } else {
                  Logger.info("Status for msisdn " + msisdnReturned + ": " + msisdnStatus.getStatus(), LocAidService.class);
                  List<String> result = engineersDao.searchEngineers("msisdn=" + msisdnReturned, "id");
                  if (result.size() == 1) {
                    String id = result.get(0);
                    Parameters params = new Parameters();
                    params.add("lis_subscription_status", status);
                    Functions.updateRecord("Engineers", id, params);
                  } else {
                    Logger.error("Cannot update engineer's status.", LocAidService.class);
                  }
                }
              }
            }
          }
        }
      }
    }
  }

  @Override
  public Map<String, Coordinates> locateMsisdns(List<String> msisdnList) throws Exception {
    Map<String, Coordinates> result = new HashMap<String, Coordinates>();

    LatitudeLongitudeServices service = createLatitudeLongitudeServices();
    LocationAnswerResponseBean response = service.getLocationsX(msisdnList, "DECIMAL", setup.getTechnology(), "syn", Integer.valueOf("1"));

    Logger.info("Technology: " + setup.getTechnology(), LocAidService.class);
    Logger.info("TransactionId: " + response.getTransactionId(), LocAidService.class);
    Logger.info("Status: " + response.getStatus(), LocAidService.class);
    if (response.getError() != null) {
      String errorMesssage = "Error: " + response.getError().getErrorCode() + ". " + response.getError().getErrorMessage();
      Logger.error(errorMesssage, LocAidService.class);
            
      throw new Exception(errorMesssage);
    } else {
      if (response.getMsisdnError() != null) {
        for (MsisdnErrorResponseBean msisdnErrorResponseBean : response.getMsisdnError()) {
          logError("Cannot locate number: " + msisdnErrorResponseBean.getMsisdn() , msisdnErrorResponseBean);
        }
      }
      List<LocationResponseBean> list = response.getLocationResponse();
      for (LocationResponseBean bean : list) {
        Logger.info("Msisdn: " + bean.getNumber(), LocAidService.class);
        Logger.info(bean.getNumber() + " status: " + bean.getStatus(), LocAidService.class);
        if (bean.getError() != null) {
          logError("Cannot locate number: " + bean.getNumber(), bean.getError());
        } else {
          if (bean.getStatus().equals("FOUND")) {
            Logger.info("Location time: " + bean.getLocationTime(), LocAidService.class);
            Logger.info("Technology : " + bean.getTechnology(), LocAidService.class);
            String msisdn = bean.getNumber();
            double latitude = Double.valueOf(bean.getCoordinateGeo().getY());
            double longitude = Double.valueOf(bean.getCoordinateGeo().getX());

            Logger.info(String.format("%s location: %f, %f", msisdn, latitude, longitude), LocAidService.class);

            result.put(msisdn, new Coordinates(latitude, longitude));
          }
        }
      }
    }
    return result;
  }
  
  private void logError(String message, BaseErrorResponseBean error) {
    Logger.error(message + " Error: " + error.getErrorCode() + ". " + error.getErrorMessage(), LocAidService.class);
  }

  protected RegistrationServices createRegistrationServices() {
    return new RegistrationServices(setup.getRegistrationServiceUrl(), setup.getUsername(), setup.getPassword());
  }

  protected LatitudeLongitudeServices createLatitudeLongitudeServices() {
    return new LatitudeLongitudeServices(setup.getLocationServiceUrl(), setup.getUsername(), setup.getPassword(), setup.getClassId());
  }

  void setEngineersDao(EngineersDao engineersDao) {
    this.engineersDao = engineersDao;
  }
}