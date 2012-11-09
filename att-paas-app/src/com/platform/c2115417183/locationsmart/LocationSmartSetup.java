package com.platform.c2115417183.locationsmart;

import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class LocationSmartSetup {

  private static final String UNKNOWN = "unknown";

  private final String serviceUrl;
  private final String username;
  private final String password;
  private final String subscriptionGroup;

  public LocationSmartSetup(String serviceUrl, String username, String password, String subscriptionGroup) {
    this.serviceUrl = serviceUrl;
    this.username = username;
    this.password = password;
    this.subscriptionGroup = subscriptionGroup;
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

  public String getSubscriptionGroup() {
    return subscriptionGroup;
  }

  public static LocationSmartSetup getInstance() throws Exception {
    String serviceUrl = UNKNOWN;
    String username = UNKNOWN;
    String password = UNKNOWN;
    String subscriptionGroup = UNKNOWN;

    Result searchResult = Functions.searchRecords("LocationSmart_Setup", "*", "");
    ParametersIterator resultIterator = searchResult.getIterator();

    if (resultIterator.hasNext()) {
      Parameters parameters = resultIterator.next();

      serviceUrl = parameters.get("service_url");
      username = parameters.get("username");
      password = parameters.get("password");
      subscriptionGroup = parameters.get("subscription_group");
    }

    return new LocationSmartSetup(serviceUrl, username, password, subscriptionGroup);
  }
}