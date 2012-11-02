package com.platform.c2115417183.gsms;

import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class GSMSSetup {
  
  private static final String UNKNOWN = "unknown";

  private final String serviceUrl;
  private final String username;
  private final String password;
  
  private GSMSSetup(String serviceUrl, String username, String password) {
    this.serviceUrl = serviceUrl;
    this.username = username;
    this.password = password;
  }
  
  public static GSMSSetup getInstance() throws Exception {
    String serviceUrl = UNKNOWN;
    String username = UNKNOWN;
    String password = UNKNOWN;
    
    Result searchResult = Functions.searchRecords("GSMS_Setup", "*", "");
    ParametersIterator resultIterator = searchResult.getIterator();
    
    if (resultIterator.hasNext()) {
      Parameters parameters = resultIterator.next();
      
      serviceUrl = parameters.get("service_url");
      username = parameters.get("username");
      password = parameters.get("password");
    }
    
    return new GSMSSetup(serviceUrl, username, password);
  }

  public String getServiceUrl() {
    return serviceUrl;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }
}