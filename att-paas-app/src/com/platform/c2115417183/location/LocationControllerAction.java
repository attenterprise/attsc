package com.platform.c2115417183.location;

import java.util.HashMap;
import java.util.Map;

import com.platform.api.ControllerResponse;

public abstract class LocationControllerAction {
  
  public abstract ControllerResponse execute(LocationService locationService, LocationControllerParameters params) throws Exception;
  
  protected ControllerResponse successMessage(String message, String details) {
    Map<String, String> result = new HashMap<String, String>();
    result.put("message", message);
    result.put("details", details);
    
    ControllerResponse response = new ControllerResponse();
    response.setData(result);
    response.setTargetPage("locationControllerStatus.jsp");
    
    return response;
  }
  
}