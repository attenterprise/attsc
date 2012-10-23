package com.platform.c2125126633.alerts;

import java.util.ArrayList;
import java.util.List;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;
import com.platform.c2125126633.locaid.LocAidManager;

/**
 * Abstract class for location services.
 * 
 * @author Magdalena Biala
 * 
 */
public abstract class LocationManager {

  /**
   * Methods returns list of field's values from Engineers object
   * 
   * @param clause
   *          criteria to specify record to set
   * @param field
   *          field from which value will be returned
   * @return list of field's values
   * @throws Exception
   */
  public List<String> searchEngineers(String clause, String field) throws Exception {
    List<String> msisdnList = new ArrayList<String>();
    Logger.debug("clause: " + clause + ", field: " + field, LocationManager.class);
    Result result = Functions.searchRecords("Engineers", field, clause);
    int resultCode = result.getCode();
    Logger.debug("resultCode: " + resultCode, LocationManager.class);
    if (resultCode < 0) {
      // Some error happened.
      String msg = "Engineers could not be retrieved";
      Logger.error(msg + "Engineers could not be retrieved:\n" + result.getMessage(), LocationManager.class);
      Functions.throwError(msg + "."); // Error dialog
    } else if (resultCode == 0) {
      // No records found.
      Logger.info("No records of Engineers found.", LocAidManager.class);
    } else {
      // Records retrieved successfully
      ParametersIterator iterator = result.getIterator();
      while (iterator.hasNext()) {
        Parameters params = iterator.next();
        msisdnList.add(params.get(field));
      }
    }
    return msisdnList;
  }

  /**
   * Method calculating distance.
   * 
   * @param lat1
   * @param long1
   * @param lat2
   * @param long2
   * @return distance between (lat1, long1) and (lat1, long2)
   */
  protected static double distanceFrom(double lat1, double long1, double lat2, double long2) {
    double R = 6371; // km
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(long2 - long1);
    double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0) + Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0) * Math.cos(lat1) * Math.cos(lat2);
    double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = R * c;
    return d;
  }

  /**
   * Method search for closest engineers to given location parameters.
   * 
   * @param lat
   * @param lng
   * @return msisdn of closest engineer
   * @throws Exception
   */
  public abstract String locateClosestEngineer(String lat, String lng) throws Exception;

  public void assignEngineer(String msisdn, String alertId) throws Exception {
    List<String> engineersIdsList = searchEngineers("msisdn=" + msisdn, "id");
    if (engineersIdsList.size() == 1) {
      String engineerId = engineersIdsList.get(0);
      Parameters params = new Parameters();
      params.add("related_to_engineers", engineerId);
      Functions.updateRecord("Alerts", alertId, params);
    } else {
      Logger.error("Cannot find enginners with msisdn: " + msisdn, LocationManager.class);
    }
  }

}
