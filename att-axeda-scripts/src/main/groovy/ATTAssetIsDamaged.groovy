/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */

import groovyx.net.http.RESTClient
import static groovyx.net.http.ContentType.*


/*
 * Object name: ATTAssetIsDamaged
 * 
 * Script is responsible for propagating information about device failure to Long Jump.
 * 
 * Parameters:
 * 
 * location - Device's location,
 */

// URL For PaaS connection
RESTClient paasRestClient = new RESTClient("http://paas1.attplatform.com/");

// Credentials for AT&T platform
String username = "1351178822";
String password = "hjatwnspy19e";

try {
  //Login to AT&T platform request
  String loginXmlRequest = "<platform><login><userName>"+username+"</userName><password>"+password+"</password></login></platform>"

  def loginResponse = paasRestClient.post(path: "/networking/rest/login",
      requestContentType: XML,
      contentType: JSON,
      body: loginXmlRequest);

  def sessionId = loginResponse.getData().platform.login.sessionId

  // Parameters of broken Asset
  def serial = alarm.device.serialNumber
  def description = alarm.name
  def id = alarm.id
  
  String location = parameters.location;
  def lat = location.split(",")[0]
  def lng =location.split(",")[1]
  
  // Adding new record in AT&T PAAS
  def newRecord = "<platform><record>" + 
    "<device_id>" + serial + "</device_id>" + 
    "<latitude>" + lat + "</latitude>" +
    "<longitude>" + lng + "</longitude>" +
    "<description>" + description + "</description>" +
    "<external_id>" + id + "</external_id>" +
    "<status>NEW</status></record></platform>";

  paasRestClient.post(path: "/networking/rest/record/Alerts",
      requestContentType: XML,
      contentType: XML,
      body: newRecord,
      headers: [Cookie : "JSESSIONID=" + sessionId]);

  //Logout request
  paasRestClient.get(path: "/networking/rest/logout",
      requestContentType: URLENC,
      contentType: XML,
      headers: [Cookie : "JSESSIONID=" + sessionId]);
    
} catch(Exception e){
  logger.error(e.getMessage());
}
