package com.platform.c2115417183.location.actions;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;

public class SubscribeAction extends LocationControllerAction {

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) throws Exception {
    Logger.info("Subscribe Action", SubscribeAction.class);
    
    locationService.subscribe(params.getAllMSISDNs());
    
    return successMessage("Engineers were subscribed to LIS", "For numbers: " + params.getAllMSISDNs());
  }

}