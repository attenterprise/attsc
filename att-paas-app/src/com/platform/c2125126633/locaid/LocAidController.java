package com.platform.c2125126633.locaid;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.att.enablers.locaid.LocAidAttributes;
import com.att.enablers.locaid.response.BaseResponseBean;
import com.att.enablers.locaid.response.BaseTransactionResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneAllResponseBean;
import com.att.enablers.locaid.response.SubscribePhoneResponseBean;
import com.platform.api.Controller;
import com.platform.api.ControllerResponse;
import com.platform.api.Logger;

/**
 * Controller responsible for dispatch requests to appropriate methods from
 * LocAid API.
 * 
 * @author Magdalena Biała
 * 
 */
public class LocAidController implements Controller {

  protected static final String RECORD_ID = "id";
  protected static final String RECORD_ID_LIST = "idlist";
  private static final String ACTION = "action";
  private LocAidManager manager = new LocAidManager();

  @SuppressWarnings("rawtypes")
  @Override
  public ControllerResponse execute(HashMap params) throws Exception {

    printAllInputParams(params);

    ControllerResponse cr = null;

    String param = (String) params.get(ACTION);
    switch (Action.valueOfParam(param)) {
    case REGISTER:
      cr = executeRegister(params);
      break;
    case REGISTER_ALL:
      cr = executeRegisterAll(params);
      break;
    case STATUS:
      cr = executeStatus(params);
      break;
    case LAT_LONG_SINGLE:
      cr = executeLatLongSingle(params);
      break;
    case LAT_LONG_MULTIPLE:
      cr = executeLatLongMultiple(params);
      break;
    case LAT_LONG_ANSWER:
      cr = executeLatLongAnswer(params);
      break;
    default:
      throw new Exception("Unknown action: " + param);
    }
    cr.setData(params);
    return cr;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ControllerResponse executeRegister(HashMap params) throws Exception {
    ControllerResponse cr = new ControllerResponse();
    // find phone numbers of the records that were selected
    List<String> msisdnList = findSelectedPhones(params);
    if (msisdnList == null || msisdnList != null && msisdnList.size() == 0) {
      throw new Exception("Select one or more phones to register");
    }

    String command = (String) params.get("command");
    if (command == null || command != null && command.isEmpty()) {
      throw new Exception("Internal error - command is undefined for action register");
    }
    SubscribePhoneResponseBean subscribeResponse = manager.register(command, msisdnList);
    params.put("locaid_response", subscribeResponse);
    cr.setTargetPage("locAidDisplayRegistrationStatus.jsp");
    return cr;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ControllerResponse executeRegisterAll(HashMap params) throws Exception {
    // find phone numbers of the records that were selected
    List<String> msisdnList = findSelectedPhones(params);
    if (msisdnList == null || msisdnList != null && msisdnList.size() == 0) {
      throw new Exception("Select one or more phones to register");
    }

    String command = (String) params.get("command");
    if (command == null || command != null && command.isEmpty()) {
      throw new Exception("Internal error - command is undefined for action register");
    }
    SubscribePhoneAllResponseBean responseBean = manager.registerAll(command, msisdnList);
    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", responseBean);
    cr.setTargetPage("locAidDisplayRegistrationAllStatus.jsp");
    return cr;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ControllerResponse executeStatus(HashMap params) throws Exception {
    // find phone numbers of the records that were selected
    List<String> msisdnList = findSelectedPhones(params);
    if (msisdnList == null || msisdnList != null && msisdnList.size() == 0) {
      throw new Exception("Select one or more phones");
    }
    BaseResponseBean response = manager.status(msisdnList);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", response);
    cr.setTargetPage("locAidDisplayGetPhoneStatus.jsp");
    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ControllerResponse executeLatLongSingle(HashMap params) throws Exception {
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones");
    }

    String coorType = (String) params.get("coorType");
    if (coorType == null || coorType != null && coorType.isEmpty()) {
      throw new Exception("Internal error - coordinate type is undefined for action latlong");
    }
    String locationMethod = (String) params.get("locationMethod");
    if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()) {
      throw new Exception("Internal error - location method is undefined for action latlong");
    }
    String overage = (String) params.get("overage");
    if (overage == null || overage != null && overage.isEmpty()) {
      throw new Exception("Internal error - overage is undefined for action latlong");
    }

    // selectedPhones - should be just one
    String phone = selectedPhones.get(0);

    BaseResponseBean response = manager.latlongSingle(phone, coorType, locationMethod, overage);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", response);
    cr.setTargetPage("locAidDisplayLatLong.jsp");

    return cr;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private ControllerResponse executeLatLongMultiple(HashMap params) throws Exception {
    // find phone numbers of the records that were selected
    List<String> selectedPhones = findSelectedPhones(params);
    if (selectedPhones == null || selectedPhones != null && selectedPhones.size() == 0) {
      throw new Exception("Select one or more phones");
    }
    String coorType = (String) params.get("coorType");
    if (coorType == null || coorType != null && coorType.isEmpty()) {
      throw new Exception("Internal error - coordinate type is undefined for action latlong");
    }
    String locationMethod = (String) params.get("locationMethod");
    if (locationMethod == null || locationMethod != null && locationMethod.isEmpty()) {
      throw new Exception("Internal error - location method is undefined for action latlong");
    }
    String syncType = (String) params.get("syncType");
    if (syncType == null || syncType != null && syncType.isEmpty()) {
      throw new Exception("Internal error - synchronization type is undefined for action latlong");
    }
    String overage = (String) params.get("overage");
    if (overage == null || overage != null && overage.isEmpty()) {
      throw new Exception("Internal error - overage is undefined for action latlong");
    }

    BaseTransactionResponseBean response = manager.latlongMultiple(selectedPhones, coorType, locationMethod, syncType, overage);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", response);
    // save for answer retrieval later
    if (LocAidAttributes.SyncType.ASYNC.equalsIgnoreCase(syncType) && response != null) {
      params.put("origTransactionId", String.valueOf(response.getTransactionId()));
    }
    cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");
    return cr;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private ControllerResponse executeLatLongAnswer(HashMap params) throws Exception {
    String transactionId = (String) params.get("origTransactionId");
    if (transactionId == null || transactionId != null && transactionId.isEmpty()) {
      throw new Exception("Internal error - transaction ID is undefined for action latlong");
    }

    BaseResponseBean responseBean = manager.latlongAnswer(transactionId);

    ControllerResponse cr = new ControllerResponse();
    params.put("locaid_response", responseBean);
    cr.setTargetPage("locAidDisplayLatLongMultiple.jsp");

    return cr;
  }

  @SuppressWarnings("rawtypes")
  public static void printAllInputParams(HashMap allParams) {
    StringBuffer buffer = new StringBuffer("Input parameters: /n");
    for (Iterator param = allParams.keySet().iterator(); param.hasNext();) {
      Object key = param.next();
      buffer.append(key + "=" + allParams.get(key)).append("/n");
    }
    Logger.debug(buffer.toString(), String.class);
  }

  @SuppressWarnings("rawtypes")
  protected List<String> findSelectedPhones(HashMap params) throws Exception {

    // check single record id first
    String record_id = (String) params.get(RECORD_ID);
    if (record_id != null && record_id.trim().length() > 0 && record_id.matches("[0-9]+")) {
      return manager.getMsisdnList(record_id);
    } else {
      String record_id_list = (String) params.get(RECORD_ID_LIST);
      if (record_id_list != null && record_id_list.trim().length() > 0) {
        return manager.getMsisdnList(record_id_list);
      }
    }

    return null;
  }

  private enum Action {

    REGISTER("register"), REGISTER_ALL("registerAll"), STATUS("status"), LAT_LONG_SINGLE("latlongSingle"), LAT_LONG_MULTIPLE("latlongMultiple"), LAT_LONG_ANSWER(
        "latlongAnswer");

    private String param;

    Action(String param) {
      this.param = param;
    }

    public String getParam() {
      return param;
    }

    static Action valueOfParam(String param) {
      for (Action action : Action.values()) {
        if (action.getParam().equals(param)) {
          return action;
        }
      }
      return null;
    }
  }

}