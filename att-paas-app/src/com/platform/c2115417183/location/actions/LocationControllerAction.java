package com.platform.c2115417183.location.actions;

import java.util.HashMap;
import java.util.Map;

import com.platform.api.ControllerResponse;

public abstract class LocationControllerAction {

  @SuppressWarnings("rawtypes")
  public abstract ControllerResponse execute(HashMap params) throws Exception;
  
  protected ControllerResponse successMessage(String message) {
    Map<String, String> result = new HashMap<String, String>();
    result.put("message", message);
    
    ControllerResponse response = new ControllerResponse();
    response.setData(result);
    response.setTargetPage("locationControllerStatus.jsp");
    
    return response;
  }
  
}