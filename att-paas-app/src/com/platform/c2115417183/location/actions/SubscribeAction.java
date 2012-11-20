package com.platform.c2115417183.location.actions;

import com.platform.api.ControllerResponse;
import com.platform.api.Logger;
import com.platform.c2115417183.location.LocationControllerAction;
import com.platform.c2115417183.location.LocationControllerParameters;
import com.platform.c2115417183.location.LocationService;
import com.platform.c2115417183.pages.DefaultResponseFactory;

public class SubscribeAction implements LocationControllerAction {

  @Override
  public ControllerResponse execute(LocationService locationService, LocationControllerParameters params) {
    Logger.info("Subscribe action", SubscribeAction.class);

    try {
      locationService.subscribe(params.getMSISDN());
      return DefaultResponseFactory.successMessage("Engineer subscribed to LIS", "MSISDN: " + params.getMSISDN());
    } catch (Exception e) {
      return DefaultResponseFactory.reportError("Subscription creation was not possible, please try again");
    }
  }

}