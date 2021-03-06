/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.locaid;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

/**
 * Class encapsulating setup information for LocAid.
 * 
 * @author Magdalena Biala
 * 
 */
public class LocAidSetup {

  private String recordId;

  private String registrationServiceUrl;

  private String locationServiceUrl;

  private String addressServiceUrl;

  private String geofencingServiceUrl;

  private String username;

  private String password;

  private String classId;
  
  private String technology;

  public static LocAidSetup getInstance() throws Exception {
    Result searchResult = Functions.searchRecords("LocAid_Setup", "*", "");
    // "record_id, registration_service_url, location_service_url, address_service_url, geofencing_service_url, username, password, class_id",
    // "");

    int resultCode = searchResult.getCode();
    if (resultCode < 0) {
      String msg = "Configuration information for LocAid could not be retrieved";
      Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidSetup.class);
      throw new Exception(msg + ". See log for more details.");
    } else if (resultCode == 0) {
      // No records found.
      String msg = "Please configure account in the LocAid Setup object first";
      Logger.error(msg + ":(" + resultCode + ")" + searchResult.getMessage(), LocAidSetup.class);
      throw new Exception(msg);
    }

    // Records retrieved successfully
    LocAidSetup setup = new LocAidSetup();
    ParametersIterator iterator = searchResult.getIterator();

    while (iterator.hasNext()) {
      Parameters p = iterator.next();
      setup.setRecordId(p.get("record_id"));
      setup.setRegistrationServiceUrl(p.get("registration_service_url"));
      setup.setLocationServiceUrl(p.get("location_service_url"));
      setup.setAddressServiceUrl(p.get("address_service_url"));
      setup.setGeofencingServiceUrl(p.get("geofencing_service_url"));
      setup.setUsername(p.get("username"));
      setup.setPassword(p.get("password"));
      setup.setClassId(p.get("class_id"));
      setup.setTechnology(p.get("technology"));
    }

    return setup;
  }

  public String getRecordId() {
    return recordId;
  }

  public void setRecordId(String recordId) {
    this.recordId = recordId;
  }

  public String getRegistrationServiceUrl() {
    return registrationServiceUrl;
  }

  public void setRegistrationServiceUrl(String registrationServiceUrl) {
    this.registrationServiceUrl = registrationServiceUrl;
  }

  public String getLocationServiceUrl() {
    return locationServiceUrl;
  }

  public void setLocationServiceUrl(String locationServiceUrl) {
    this.locationServiceUrl = locationServiceUrl;
  }

  public String getAddressServiceUrl() {
    return addressServiceUrl;
  }

  public void setAddressServiceUrl(String addressServiceUrl) {
    this.addressServiceUrl = addressServiceUrl;
  }

  public String getGeofencingServiceUrl() {
    return geofencingServiceUrl;
  }

  public void setGeofencingServiceUrl(String geofencingServiceUrl) {
    this.geofencingServiceUrl = geofencingServiceUrl;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClassId() {
    return classId;
  }

  public void setClassId(String classId) {
    this.classId = classId;
  }

  public String getTechnology() {
    return technology;
  }

  public void setTechnology(String technology) {
    this.technology = technology;
  }
}