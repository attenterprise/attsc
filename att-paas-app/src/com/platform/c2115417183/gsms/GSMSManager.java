package com.platform.c2115417183.gsms;

import java.net.URLEncoder;

import com.platform.api.CONSTANTS;
import com.platform.api.HttpConnection;
import com.platform.api.Logger;

public class GSMSManager {

  @SuppressWarnings("deprecation")
  public String sendSms(String msisdn, String assetId, double lat, double lng) {
    try {
      final GSMSSetup setup = GSMSSetup.getInstance();
      String message = String.format("Asset '%s' (%f, %f) is damaged", assetId, lat, lng);
      
      Logger.info("Sending SMS", GSMSManager.class);
      Logger.info("Recipient: " + msisdn, GSMSManager.class);
      Logger.info("Message: " + message, GSMSManager.class);
      
      message = URLEncoder.encode(message);
      String params = "?username=" + setup.getUsername() + "&password=" + setup.getPassword() + "&text=" + message  + "&destination=" + msisdn;
            
      HttpConnection conn = new HttpConnection(CONSTANTS.HTTP.METHOD.GET, setup.getServiceUrl() + params);
      conn.execute();

      return conn.getResponse();
    } catch (Exception e) {
      return e.getMessage();
    }
  }

}