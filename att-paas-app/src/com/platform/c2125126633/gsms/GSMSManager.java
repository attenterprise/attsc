package com.platform.c2125126633.gsms;

import java.net.URLEncoder;

import com.platform.api.CONSTANTS;
import com.platform.api.HttpConnection;
import com.platform.api.Logger;

public class GSMSManager {

  private static final String SERVICE_URL = "https://na1.smartmessagingsuite.com/cgphttp/servlet/sendmsg";
  private static final String USERNAME = "tim.young@sentaca.com";
  private static final String PASSWORD = "=tTEoI9DfV";

  public String sendSms(String msisdn, String assetId, double lat, double lng) {
    try {
      String text = String.format("Asset '%s' (%f, %f) is damaged", assetId, lat, lng);
      
      String params = "?username=" + USERNAME + "&password=" + PASSWORD + "&text=" + URLEncoder.encode(text) + "&destination=" + msisdn;
      
      Logger.debug("SMS params: " + params, GSMSManager.class);
      
      HttpConnection conn = new HttpConnection(CONSTANTS.HTTP.METHOD.GET, SERVICE_URL + params);

      conn.execute();

      return conn.getResponse();
    } catch (Exception e1) {
      return e1.getMessage();
    }
  }

}