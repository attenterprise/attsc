package com.platform.c2115417183.alerts;

import java.util.List;

import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.c2115417183.engineers.EngineersDao;
import com.platform.c2115417183.engineers.EngineersService;
import com.platform.c2115417183.engineers.SearchResult;
import com.platform.c2115417183.gsms.GSMSManager;
import com.platform.c2115417183.location.LocationServiceSetup;

/**
 * Class that implements data policies used by Alerts object.
 * 
 * @author Magdalena Biala
 * 
 */
public class AlertsDataPolicy {

  private EngineersService engineersService = new EngineersService();
  private EngineersDao engineersDao = new EngineersDao();
  private GSMSManager gsmsManager = new GSMSManager();

  /**
   * Method invoked after inserting new record to Alerts.
   * 
   * @param requestParams
   */
  public void processAlert(Parameters requestParams) {
    try {
      String lat = (String) requestParams.getObject("latitude");
      String lng = (String) requestParams.getObject("longitude");
      String alertId = (String) requestParams.getObject("id");
      String deviceId = (String) requestParams.getObject("device_id");
      String details = (String) requestParams.getObject("description");

      String message = String.format("New alert[device: %s]: %s", deviceId, details);
      Logger.info(message, this.getClass());

      LocationServiceSetup lisSetup = LocationServiceSetup.getInstance();

      List<SearchResult> results = engineersService.locateClosestEngineer(lisSetup, lat, lng);

      for (SearchResult result : results) {
        String msisdn = result.getMsisdn();

        if (gsmsManager.sendSms(msisdn, deviceId, lat, lng)) {
          engineersDao.assignEngineer(msisdn, alertId);
          List<String> selectedEngineer = engineersDao.searchEngineers("msisdn=" + msisdn, "first_name");

          Logger.info("Selected engineer: " + selectedEngineer.get(0), AlertsDataPolicy.class);
          
          break;
        } else {
          Logger.error("Unable to send SMS to: " + msisdn, AlertsDataPolicy.class);
        }
      }
    } catch (Exception e) {
      Logger.error(e.getMessage(), AlertsDataPolicy.class);
    }
  }

  void setEngineersService(EngineersService engineersService) {
    this.engineersService = engineersService;
  }

  void setEngineersDao(EngineersDao engineersDao) {
    this.engineersDao = engineersDao;
  }

  void setGsmsManager(GSMSManager gsmsManager) {
    this.gsmsManager = gsmsManager;
  }
}