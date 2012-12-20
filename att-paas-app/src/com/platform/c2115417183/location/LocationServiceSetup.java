/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.location;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class LocationServiceSetup {

  private enum LocationService {
    NONE,
    LOC_AID,
    LOCATION_SMART
  }
  
  private final LocationService locationService;

  public LocationServiceSetup(LocationService locationService) {
    this.locationService = locationService;
  }
  
  public boolean isLocAidDefault() {
    return LocationService.LOC_AID.equals(locationService);
  }
  
  public boolean isLocationSmartDefault() {
    return LocationService.LOCATION_SMART.equals(locationService);
  }
  
  public boolean isCorrectlyConfigured() {
    return !LocationService.NONE.equals(locationService);
  }
  
  public static LocationServiceSetup getInstance() {
    LocationService service = LocationService.NONE;
    
    try {
      final Result searchResult = Functions.searchRecords("LIS_Setup", "*", "");
      final ParametersIterator searchResultIterator = searchResult.getIterator();
      
      if (searchResultIterator.hasNext()) {
        final Parameters parameters = searchResultIterator.next();
        final String serviceName = parameters.get("service");
        
        if ("LocAid".equals(serviceName)) {
          service = LocationService.LOC_AID;
        } else if ("LocationSmart".equals(serviceName)) {
          service = LocationService.LOCATION_SMART;
        }
      }
    } catch (Exception e) {
      Logger.error(e.getMessage(), LocationServiceSetup.class);
    }
    
    return new LocationServiceSetup(service);
  }
}