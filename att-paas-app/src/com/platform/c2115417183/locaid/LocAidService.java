package com.platform.c2115417183.locaid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.att.enablers.locaid.response.LocationAnswerResponseBean;
import com.att.enablers.locaid.response.LocationResponseBean;
import com.platform.api.Logger;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationService;

public class LocAidService implements LocationService {

  private LocAidManager manager = new LocAidManager();

  @Override
  public void subscribe(List<String> msisdns) throws Exception {
    manager.register("OPTIN", msisdns);
  }

  @Override
  public void confirmSubscription(List<String> msisdns) throws Exception {
    manager.register("YES", msisdns);
  }

  @Override
  public void unsubscribe(List<String> msisdns) throws Exception {
    manager.register("CANCEL", msisdns);
  }

  @Override
  public Map<String, Coordinates> locateMsisdns(List<String> msisdnList) throws Exception {
    Map<String, Coordinates> result = new HashMap<String, Coordinates>();
        
    LocationAnswerResponseBean response = manager.latlongMultiple(msisdnList, "DECIMAL", "LEAST_EXPENSIVE", "syn", "1");
    Logger.debug("status: " + response.getStatus(), LocAidManager.class);
    
    if (response.getError() != null) {
      Logger.error(response.getError().getErrorMessage(), LocAidManager.class);
    } else {
      List<LocationResponseBean> list = response.getLocationResponse();
      
      for (LocationResponseBean bean : list) {
        if (bean.getError() != null) {
          Logger.error(bean.getError().getErrorMessage(), LocAidManager.class);
        } else {
          Logger.debug(bean.getNumber() + " status: " + bean.getStatus(), LocAidManager.class);
          
          if (bean.getStatus().equals("FOUND")) {            
            String msisdn = bean.getNumber();
            double latitude = Double.valueOf(bean.getCoordinateGeo().getY());
            double longitude = Double.valueOf(bean.getCoordinateGeo().getX());
            
            Logger.info(String.format("%s location: %f, %f",  msisdn, latitude, longitude), LocAidManager.class);
            
            result.put(msisdn, new Coordinates(latitude, longitude));
          }
        }
      }
    }
    
    return result;
  }

}