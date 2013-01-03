/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.pages;

import java.util.HashMap;
import java.util.Map;

import com.platform.api.ControllerResponse;

public class DefaultResponseFactory {

  public static ControllerResponse successMessage(String message, String details) {
    Map<String, String> result = new HashMap<String, String>();
    result.put("message", message);
    result.put("details", details);

    ControllerResponse response = new ControllerResponse();
    response.setData(result);
    response.setTargetPage("locationControllerStatus.jsp");

    return response;
  }

  public static ControllerResponse reportError(String errorMessage) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("error_message", errorMessage);

    ControllerResponse errorResponse = new ControllerResponse();
    errorResponse.setData(params);
    errorResponse.setTargetPage("locationControllerStatus.jsp");

    return errorResponse;
  }

}