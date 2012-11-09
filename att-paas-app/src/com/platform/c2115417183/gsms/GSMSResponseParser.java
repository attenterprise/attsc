package com.platform.c2115417183.gsms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

public class GSMSResponseParser {
  
  public static final String SUCCESS = "1000";
  public static final String CONTACT_ALREDY_EXISTS = "2000";
  
  private static final String API_RESPONSE_OBJECT = "ApiResponse";
  
  private static final String DATA_VALUE = "Data";
  private static final String RECORD_ID_VALUE = "RecordId";
  private static final String STATUS_CODE_VALUE = "StatusCode";

  private static final Pattern ID_PATTERN = Pattern.compile("id=([0-9]+)");
  
  private final JSONObject response;
  
  public GSMSResponseParser(String gsmsResponse) throws GSMSException {
    try {
      JSONObject object = new JSONObject(gsmsResponse);
      response = object.getJSONObject(API_RESPONSE_OBJECT);
    } catch (JSONException e) {
      throw new GSMSException("Invalid GSMS response: " + gsmsResponse);
    }
  }

  public String findContactId() throws JSONException {        
    if (CONTACT_ALREDY_EXISTS.equals(getStatusCode())) {
       String data = response.getString(DATA_VALUE);
       Matcher matcher = ID_PATTERN.matcher(data);
       
       if (matcher.find()) {
         return matcher.group(1);
       }
    }
    
    return response.getString(RECORD_ID_VALUE);
  }

  public String getStatusCode() throws JSONException {
    return response.getString(STATUS_CODE_VALUE);
  }
  
  public boolean isStatusCodeValid() throws JSONException {
    final String status = getStatusCode();
    
    return SUCCESS.equals(status) || CONTACT_ALREDY_EXISTS.equals(status);
  }

}