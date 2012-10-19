package com.platform.c2125126633.alerts;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.Result;
import com.platform.c2125126633.gsms.GSMSManager;
import com.platform.c2125126633.locaid.LocAidManager;

/**
 * Class that implements data policies used by Alerts object.
 * 
 * @author Magdalena Biala
 * 
 */
public class AlertsDataPolicy {
  public void processAlert(Parameters requestParams) {
    Logger.info("New failure alert received!", this.getClass());
    try {
      LocAidManager locAidManager = new LocAidManager();
      String lat = (String) requestParams.getObject("latitude");
      String lng = (String) requestParams.getObject("longitude");
      Logger.debug("requestParams: " + requestParams, AlertsDataPolicy.class);
      String msisdn = locAidManager.locateEngineer(lat, lng);
      Logger.debug("Chosen msisdn: " + msisdn, AlertsDataPolicy.class);
      Result result = Functions.searchRecords("Engineers", "id", "msisdn=" + msisdn);
      Logger.debug("Result: " + result.getIterator().next().toString(), AlertsDataPolicy.class);
      String engineerId = result.getIterator().next().get("id");
      Parameters params = new Parameters();
      params.add("related_to_engineers", engineerId);
      params.add("status", "ASSIGNED");
      String alertId = (String) requestParams.getObject("id");
      Functions.updateRecord("Alerts", alertId, params);
      GSMSManager gsmsManager = new GSMSManager();
      gsmsManager.sendSms(msisdn, (String) requestParams.getObject("device_id"), Double.valueOf(lat), Double.valueOf(lng));
    } catch (Exception e) {
      Logger.error(e.getMessage(), AlertsDataPolicy.class);
    }
  }

}