package com.platform.c2115417183.locationsmart;

import java.util.List;
import java.util.Map;

import com.platform.api.CONSTANTS;
import com.platform.api.HttpConnection;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationService;

public class LocationSmartService implements LocationService {

  @Override
  public void subscribe(List<String> msisdns) throws Exception {
    String user = "tim.young@sentaca.com";
    String passwd = "n6ahvoF3";
    String url = "https://tlp.technocom-wireless.com/api/sentaca/demo/statusreq/?tn=7604385115&carrierReq=True";
    HttpConnection httpConnection = new HttpConnection(CONSTANTS.HTTP.METHOD.GET, url);
    

  }

  @Override
  public void confirmSubscription(List<String> msisdns) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public void unsubscribe(List<String> msisdns) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public Map<String, Coordinates> locateMsisdns(List<String> msisdns) throws Exception {
    // TODO Auto-generated method stub
    return null;
  }

}