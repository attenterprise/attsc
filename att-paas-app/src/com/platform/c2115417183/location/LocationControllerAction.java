package com.platform.c2115417183.location;

import com.platform.api.ControllerResponse;

public interface LocationControllerAction {

  ControllerResponse execute(LocationService locationService, LocationControllerParameters params);

}