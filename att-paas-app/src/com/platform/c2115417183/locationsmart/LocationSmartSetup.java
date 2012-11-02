package com.platform.c2115417183.locationsmart;

import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class LocationSmartSetup {
  
  private static final String UNKNOWN = "unknown";

  private final String serviceUrl;
  private final String login;
  private final String password;

  public LocationSmartSetup(String serviceUrl, String login, String password) {
    this.serviceUrl = serviceUrl;
    this.login = login;
    this.password = password;
  }

  public String getServiceUrl() {
    return serviceUrl;
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }
  
  public static LocationSmartSetup getInstance() throws Exception {
    String serviceUrl = UNKNOWN;
    String username = UNKNOWN;
    String password = UNKNOWN;
    
    Result searchResult = Functions.searchRecords("Location_Smart_Setup", "*", "");
    ParametersIterator resultIterator = searchResult.getIterator();
    
    if (resultIterator.hasNext()) {
      Parameters parameters = resultIterator.next();
      
      serviceUrl = parameters.get("service_url");
      username = parameters.get("login");
      password = parameters.get("password");
    }
    
    return new LocationSmartSetup(serviceUrl, username, password);
  }
}