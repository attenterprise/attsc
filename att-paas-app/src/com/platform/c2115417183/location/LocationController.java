package com.platform.c2115417183.location;

import java.util.HashMap;

import com.platform.api.Controller;
import com.platform.api.ControllerResponse;
import com.platform.c2115417183.location.actions.GetLocationAction;
import com.platform.c2115417183.location.actions.SubscribeAction;
import com.platform.c2115417183.location.actions.UnsubscribeAction;
import com.platform.c2115417183.pages.DefaultResponseFactory;

public class LocationController implements Controller {

  public static final String ACTION_PARAMETER = "action";
  public static final String IDS_PARAMETER = "ids";
  public static final String ID_PARAMETER = "id";

  private static final String GET_LOCATION_ACTION = "get_location";
  private static final String SUBSCRIBE_ACTION = "subscribe";
  private static final String UNSUBSCRIBE_ACTION = "unsubscribe";
  
  private LocationServiceFactory lisFactory = new LocationServiceFactory();

  @SuppressWarnings("rawtypes")
  public ControllerResponse execute(HashMap requestParams) throws Exception {
    final LocationServiceSetup setup = LocationServiceSetup.getInstance();

    if (setup.isCorrectlyConfigured()) {
      final LocationService locationService = lisFactory.getNewLocationService(setup);

      return executeAction(locationService, requestParams);
    } else {
      return DefaultResponseFactory.reportError("Location Service hasn't been selected in 'Location Service Setup'");
    }
  }

  @SuppressWarnings("rawtypes")
  private ControllerResponse executeAction(LocationService locationService, HashMap requestParams) throws Exception {
    final String actionName = (String) requestParams.get(ACTION_PARAMETER);
    final LocationControllerParameters params = LocationControllerParameters.getInstance(requestParams);
    final LocationControllerAction action;

    if (GET_LOCATION_ACTION.equals(actionName)) {
      action = new GetLocationAction();
    } else if (UNSUBSCRIBE_ACTION.equals(actionName)) {
      action = new UnsubscribeAction();
    } else if (SUBSCRIBE_ACTION.equals(actionName)) {
      action = new SubscribeAction();
    } else {
      return DefaultResponseFactory.reportError("Not supported action: " + actionName);
    }

    return action.execute(locationService, params);
  }
}