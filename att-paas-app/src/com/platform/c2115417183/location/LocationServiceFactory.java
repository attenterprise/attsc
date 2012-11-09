package com.platform.c2115417183.location;

import com.platform.c2115417183.locaid.LocAidService;
import com.platform.c2115417183.locationsmart.LocationSmartService;

public class LocationServiceFactory {

  public static LocationService getNewLocationService(LocationServiceSetup setup) throws Exception {
    if (setup.isLocAidDefault()) {
      return new LocAidService();
    }
    if (setup.isLocationSmartDefault()) {
      return new LocationSmartService();
    }
    throw new Exception("Not supported location service.");
  }

}