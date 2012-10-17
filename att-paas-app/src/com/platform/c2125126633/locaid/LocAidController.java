package com.platform.c2125126633.locaid;

import java.util.HashMap;
import java.util.Iterator;

import com.platform.api.Controller;
import com.platform.api.ControllerResponse;

/**
 * @author Magdalena Biała
 * 
 */
public class LocAidController implements Controller {

  private static final String ACTION = "action";

  @SuppressWarnings("rawtypes")
  @Override
  public ControllerResponse execute(HashMap params) throws Exception {
    LocAidManager manager = new LocAidManager();
    printAllInputParams(params);

    ControllerResponse cr = null;

    // --------------------------------------------------------------------
    String action = (String) params.get(ACTION);
    if ("register".equalsIgnoreCase(action)) {
      cr = manager.register(params);
    } else if ("registerAll".equalsIgnoreCase(action)) {
      cr = manager.registerAll(params);
    } else if ("status".equalsIgnoreCase(action)) {
      cr = manager.status(params);
    } else if ("latlongSingle".equalsIgnoreCase(action)) {
      cr = manager.latlongSingle(params);
    } else if ("latlongMultiple".equalsIgnoreCase(action)) {
      cr = manager.latlongMultiple(params);
    } else if ("latlongAnswer".equalsIgnoreCase(action)) {
      cr = manager.latlongAnswer(params);
    } else {
      throw new Exception("Unknown action: " + action);
    }
    return cr;
  }

  @SuppressWarnings("rawtypes")
  public static void printAllInputParams(HashMap all_params) {
    for (Iterator a_param = all_params.keySet().iterator(); a_param.hasNext();) {
      Object key = a_param.next();
      com.platform.api.Logger.debug("printAllInputParams: " + key + "=" + all_params.get(key), java.lang.String.class);
    }
  }

}