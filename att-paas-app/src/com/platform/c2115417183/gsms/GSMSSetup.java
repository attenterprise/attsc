package com.platform.c2115417183.gsms;

import java.util.NoSuchElementException;

import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class GSMSSetup {
  
  private static final String UNKNOWN = "unknown";

  private final String serviceUrl;
  private final String username;
  private final String password;
  private final String defaultGroup;
  
  private GSMSSetup(String serviceUrl, String username, String password, String defaultGroup) {
    this.serviceUrl = serviceUrl;
    this.username = username;
    this.password = password;
    this.defaultGroup = defaultGroup;
  }
  
  public static GSMSSetup getInstance() throws GSMSException {
    try {
      String serviceUrl = UNKNOWN;
      String username = UNKNOWN;
      String password = UNKNOWN;
      String defaultGroup = UNKNOWN;
      
      Result searchResult = Functions.searchRecords("GSMS_Setup", "*", "");
      ParametersIterator resultIterator = searchResult.getIterator();
      
      if (resultIterator.hasNext()) {
        Parameters parameters = resultIterator.next();
        
        serviceUrl = parameters.get("service_url");
        username = parameters.get("username");
        password = parameters.get("password");
        defaultGroup = parameters.get("default_group");
      }
      
      return new GSMSSetup(serviceUrl, username, password, defaultGroup);
    } catch (NoSuchElementException e) {
      throw new GSMSException("GSMS configuration is invalid.", e);
    } catch (Exception e) {
      throw new GSMSException("GSMS configuration is invalid.", e);
    }
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

  public String getDefaultGroup() {
    return defaultGroup;
  }
}