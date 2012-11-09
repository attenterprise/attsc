package com.platform.c2115417183.location.actions;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;

public class UnsubscribeAction extends LocationControllerAction {

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) throws Exception {
    Logger.info("Unsubscribe Action", UnsubscribeAction.class);

    locationService.unsubscribe(params.getMSISDN());

    return successMessage("Subscriptions were canceled", "For number: " + params.getMSISDN());
  }

}