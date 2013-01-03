/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.location;

import com.platform.c2115417183.locaid.LocAidService;
import com.platform.c2115417183.locaid.LocAidSetup;
import com.platform.c2115417183.locationsmart.LocationSmartService;

public class LocationServiceFactory {

  public LocationService getNewLocationService(LocationServiceSetup setup) throws Exception {
    if (setup.isLocAidDefault()) {
      return new LocAidService(LocAidSetup.getInstance());
    }
    if (setup.isLocationSmartDefault()) {
      return new LocationSmartService();
    }
    throw new Exception("Not supported location service.");
  }

}