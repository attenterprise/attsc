package com.platform.c2125126633.gsms;

import java.util.HashMap;

import com.platform.api.Controller;
import com.platform.api.ControllerResponse;

public class GSMSController implements Controller {

  @Override
  public ControllerResponse execute(@SuppressWarnings("rawtypes") HashMap parameters) throws Exception {
    GSMSManager manager = new GSMSManager();
    String resp = manager.sendSms("4154906173 ", "test 2", 34.4, 23.3);
    
    HashMap returnParams = new HashMap();
    returnParams.put("message", resp);
    
    ControllerResponse cr = new ControllerResponse();
    cr.setData(returnParams);
    cr.setTargetPage("gsmsTest.jsp");
    
    return cr;
  }

}