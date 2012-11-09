package com.platform.c2115417183.gsms;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import com.platform.api.Controller;
import com.platform.api.ControllerResponse;
import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class GSMSController implements Controller {

  private static final String ACTION_PARAMETER = "action";
  private static final String ID_PARAMETER = "id";
    
  private static final String SEND_INVITATION_ACTION = "sendInvitation";
  private static final String SUBSCRIBE_ACTION = "subscribe";

  @SuppressWarnings("rawtypes")
  @Override
  public ControllerResponse execute(HashMap params) throws Exception {
    String action = (String) params.get(ACTION_PARAMETER);
    String id = (String) params.get(ID_PARAMETER);
    
    Logger.info("action: " + action, GSMSController.class);
    Logger.info("id: " + id, GSMSController.class);

    if (SUBSCRIBE_ACTION.equals(action)) {
      return subscribeEngineerWithID(id);
    } else if (SEND_INVITATION_ACTION.equals(action)) {
      return sendInvitationSms(id);
    } else {
      return reportError("Not supported action " + action);
    }
  }

  private ControllerResponse subscribeEngineerWithID(String id) {
    try {
      Result searchResult = Functions.searchRecords("Engineers", "msisdn,first_name,last_name", "id=" + id);
      ParametersIterator paramsIter = searchResult.getIterator();

      if (paramsIter.hasNext()) {
        Parameters searchParams = paramsIter.next();
        
        return subscribeEngineer(searchParams.get("first_name"), searchParams.get("last_name"), searchParams.get("msisdn"));
      }
      
      return reportError("Invalid engineer's ID: " + id);
    } catch (NoSuchElementException e) {
      return reportError("Cannot subscribe enginner: " + e.getMessage());
    } catch (Exception e) {
      return reportError("Cannot subscribe enginner: " + e.getMessage());
    }
  }
  
  private ControllerResponse subscribeEngineer(String firstName, String lastName, String msisdn) {
    try {
      GSMSManager gsmsManager = new GSMSManager();
      gsmsManager.subscribe(firstName, lastName, msisdn);
      
      return reportSuccess(firstName + " " + lastName + " was subscribed to GSMS");
    } catch (GSMSException e) {
      Logger.error("Unable to subscribe engineer: " + e.getMessage(), GSMSController.class);
      Logger.error(e, GSMSController.class);
      
      return reportError("Unable to subscribe engineer: " + e.getMessage());
    }
  }
  
  private ControllerResponse sendInvitationSms(String id) {
    try {
      Result searchResult = Functions.searchRecords("Engineers", "msisdn,first_name", "id=" + id);
      ParametersIterator paramsIter = searchResult.getIterator();

      if (paramsIter.hasNext()) {
        Parameters searchParams = paramsIter.next();
        String firstName = searchParams.get("first_name");
        String msisdn = searchParams.get("msisdn");
        
        GSMSManager gsmsManager = new GSMSManager();
        gsmsManager.sendSms(msisdn, String.format("Hi %s! Welcome to AT&T Showcase Applicaiton.", firstName));
        
        return reportSuccess("Invitation SMS was sent to: " + searchParams.get("msisdn"));
      }
      
      return reportError("Unknown ID: " + id);
    } catch (NoSuchElementException e) {
      return reportError(e.getMessage());
    } catch (Exception e) {
      return reportError(e.getMessage());
    }
  }

  private ControllerResponse reportError(String errorMessage) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("error_message", errorMessage);

    ControllerResponse errorResponse = new ControllerResponse();
    errorResponse.setData(params);
    errorResponse.setTargetPage("locationControllerStatus.jsp");

    return errorResponse;
  }
  
  private ControllerResponse reportSuccess(String message) {
    Map<String, String> params = new HashMap<String, String>();
    params.put("message", message);

    ControllerResponse errorResponse = new ControllerResponse();
    errorResponse.setData(params);
    errorResponse.setTargetPage("locationControllerStatus.jsp");

    return errorResponse;
  }
}