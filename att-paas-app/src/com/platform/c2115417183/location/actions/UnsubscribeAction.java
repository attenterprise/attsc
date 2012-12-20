/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.location.actions;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;
import com.platform.c2115417183.pages.DefaultResponseFactory;

public class UnsubscribeAction implements LocationControllerAction {

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) {
    Logger.info("Unsubscribe action", UnsubscribeAction.class);
    
    try {
      locationService.unsubscribe(params.getMSISDN());
      return DefaultResponseFactory.successMessage("Subscription canceled", "For number: " + params.getMSISDN());
    } catch (Exception e) {
      return DefaultResponseFactory.reportError("Unable to cancel subscription");
    }
  }
}