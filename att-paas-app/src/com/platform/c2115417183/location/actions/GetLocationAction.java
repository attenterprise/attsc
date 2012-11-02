package com.platform.c2115417183.location.actions;

import java.util.Map;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;

public class GetLocationAction extends LocationControllerAction {

  private static final String ERROR = "Unable to locate engineer";

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) throws Exception {
    Logger.info("Get Location Action", GetLocationAction.class);

    Map<String, Coordinates> locations = locationService.locateMsisdns(params.getAllMSISDNs());
    Coordinates location = locations.get(params.getAllMSISDNs().get(0));

    String details = location != null ? String.format("Latitude: %f Longitude: %f", location.getLatitude(), location.getLongitude()) : ERROR;

    return successMessage("Engineers location", details);
  }

}