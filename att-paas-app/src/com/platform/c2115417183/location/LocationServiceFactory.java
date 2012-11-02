package com.platform.c2115417183.location;

import com.platform.c2115417183.locaid.LocAidService;

public class LocationServiceFactory {

  public static LocationService getNewLocationService(LocationServiceSetup setup) throws Exception {
    if (setup.isLocAidDefault()) {
      return new LocAidService();
    }
    
    throw new Exception("Not supported location service.");
  }

}