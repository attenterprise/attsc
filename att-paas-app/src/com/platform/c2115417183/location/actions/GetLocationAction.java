/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.location.actions;

import java.util.Map;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;
import com.platform.c2115417183.pages.DefaultResponseFactory;

public class GetLocationAction implements LocationControllerAction {

  private static final String ERROR = "Unable to locate engineer";

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) {
    Logger.info("Get Location Action", GetLocationAction.class);

    try {
      Map<String, Coordinates> locations = locationService.locateMsisdns(params.getAllMSISDNs());
      Coordinates location = locations.get(params.getAllMSISDNs().get(0));

      String details = location != null ? String.format("Latitude: %f Longitude: %f", location.getLatitude(), location.getLongitude()) : ERROR;

      return DefaultResponseFactory.successMessage("Engineers location", details);
    } catch (Exception e) {
      return DefaultResponseFactory.reportError("Getting information about the location wasn't possible");
    }
  }

}