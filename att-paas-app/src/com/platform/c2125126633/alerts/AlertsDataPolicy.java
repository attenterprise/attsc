package com.platform.c2125126633.alerts;

import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.c2125126633.gsms.GSMSManager;
import com.platform.c2125126633.locaid.LocAidManager;

/**
 * Class that implements data policies used by Alerts object.
 * 
 * @author Magdalena Biala
 * 
 */
public class AlertsDataPolicy {

  private LocationManager locationManager = new LocAidManager();

  /**
   * Method invoked after inserting new record to Alerts.
   * 
   * @param requestParams
   */
  public void processAlert(Parameters requestParams) {
    Logger.info("New failure alert received!", this.getClass());
    try {
      String lat = (String) requestParams.getObject("latitude");
      String lng = (String) requestParams.getObject("longitude");
      String msisdn = locationManager.locateClosestEngineer(lat, lng);
      String alertId = (String) requestParams.getObject("id");
      locationManager.assignEngineer(msisdn, alertId);
      GSMSManager gsmsManager = new GSMSManager();
      gsmsManager.sendSms(msisdn, (String) requestParams.getObject("device_id"), Double.valueOf(lat), Double.valueOf(lng));
    } catch (Exception e) {
      Logger.error(e.getMessage(), AlertsDataPolicy.class);
    }
  }

}