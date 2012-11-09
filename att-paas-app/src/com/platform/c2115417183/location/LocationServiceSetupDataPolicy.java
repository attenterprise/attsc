package com.platform.c2115417183.location;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class LocationServiceSetupDataPolicy {

  public void checkIfCanChangeLocationService(Parameters parameters) throws Exception {
    Logger.info("Checking if any enginner is subscribed to current location service", LocationServiceSetupDataPolicy.class);

    String engineersNames = "";
    Result allEngineers = Functions.searchRecords("Engineers", "first_name,last_name,lis_subscription_status", "");

    ParametersIterator engineersIterator = allEngineers.getIterator();
    while (engineersIterator.hasNext()) {
      Parameters engineer = engineersIterator.next();
      String firstName = engineer.get("first_name");
      String lastName = engineer.get("last_name");
      String status = engineer.get("lis_subscription_status");

      Logger.info(firstName + " " + lastName + ": " + status, LocationServiceSetupDataPolicy.class);

      if ("SUBSCRIBED".equals(status)) {
        engineersNames += firstName + " " + lastName + ",";
      }
    }

    if (engineersNames.length() > 0) {
      Logger.info("Engineers are subscribed: " + engineersNames, LocationServiceSetupDataPolicy.class);

      throw new Exception("Following enginners are subscribed to the current Location Service: " + engineersNames);
    } else {
      Logger.info("Can change location service", LocationServiceSetupDataPolicy.class);
    }
  }
}