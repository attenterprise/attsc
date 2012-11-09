package com.platform.c2115417183.alerts;

import java.util.List;

import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.c2115417183.engineers.EngineersService;
import com.platform.c2115417183.gsms.GSMSManager;

/**
 * Class that implements data policies used by Alerts object.
 * 
 * @author Magdalena Biala
 * 
 */
public class AlertsDataPolicy {

  private EngineersService engineersService = new EngineersService();
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
      
      String msisdn = engineersService.locateClosestEngineer(lat, lng);
      
      if (msisdn != null) {
        engineersService.assignEngineer(msisdn, alertId);
        List<String> selectedEngineer = engineersService.searchEngineers("msisdn=" + msisdn, "first_name");
        
        Logger.info("Selected engineer: " + selectedEngineer.get(0), AlertsDataPolicy.class);
        
        gsmsManager.sendSms(msisdn, deviceId, lat, lng);
      } else {
        Logger.error("Engineer wasn't selected", AlertsDataPolicy.class);
      }
      
    } catch (Exception e) {
      Logger.error(e.getMessage(), AlertsDataPolicy.class);
    }
  }

}