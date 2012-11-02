package com.platform.c2115417183.location.actions;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;

public class ConfirmSubscriptionAction extends LocationControllerAction {

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) throws Exception {
    Logger.info("Confirm Subscription Action", ConfirmSubscriptionAction.class);
    
    locationService.confirmSubscription(params.getAllMSISDNs());

    return successMessage("Subsciptions were confirmed", "For numbers: " + params.getAllMSISDNs());
  }

}