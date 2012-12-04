package com.platform.c2115417183.engineers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.platform.api.Logger;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationService;
import com.platform.c2115417183.location.LocationServiceFactory;
import com.platform.c2115417183.location.LocationServiceSetup;
import com.platform.c2115417183.location.LocationUtils;

public class EngineersService {

  private EngineersDao engineersDao = new EngineersDao();
  private LocationServiceFactory lisFactory = new LocationServiceFactory();

  public List<SearchResult> locateClosestEngineer(LocationServiceSetup setup, String lat, String lng) throws Exception {
    Logger.info("Locating engineers", EngineersService.class);

    if (setup.isCorrectlyConfigured()) {
      return locateClosesEngineer(setup, lat, lng);
    } else {
      Logger.error("Default Location Service hasn't been selected.", EngineersService.class);

      return new ArrayList<SearchResult>();
    }
  }

  private List<SearchResult> locateClosesEngineer(LocationServiceSetup setup, String lat, String lng) throws Exception {
    List<SearchResult> results = new ArrayList<SearchResult>();
    LocationService locationService = lisFactory.getNewLocationService(setup);

    double deviceLatitude = Double.valueOf(lat);
    double deviceLongitude = Double.valueOf(lng);

    List<String> msisdnList = engineersDao.searchEngineers("lis_subscription_status='SUBSCRIBED' AND gsms_subscription_status='SUBSCRIBED'", "msisdn");

    Map<String, Coordinates> searchResult = locationService.locateMsisdns(msisdnList);

    for (String msisdn : searchResult.keySet()) {
      Coordinates location = searchResult.get(msisdn);
      double distance = LocationUtils.distanceFrom(deviceLatitude, deviceLongitude, location.getLatitude(), location.getLongitude());

      Logger.info(msisdn + " distance: " + distance + " km", EngineersService.class);
      
      results.add(new SearchResult(msisdn, distance));
    }
    
    Collections.sort(results);

    return results;
  }

  void setEngineersDao(EngineersDao engineersDao) {
    this.engineersDao = engineersDao;
  }

  void setLisFactory(LocationServiceFactory lisFactory) {
    this.lisFactory = lisFactory;
  }

}