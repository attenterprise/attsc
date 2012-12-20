/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */

import groovyx.net.http.RESTClient
import groovy.json.*
import static groovyx.net.http.ContentType.*

RESTClient paasRestClient = new RESTClient("http://paas1.attplatform.com/");

/*
 * Object name: ATTAssetIsFixed
 *
 * Script is responsible for propagating information about fixed device to Long Jump.
 */

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
  
  // Identifier of current session
  def sessionId = loginResponse.getData().platform.login.sessionId
  
  // Parameters of Alarm
  def alarmId = alarm.id
  
  // Find active alert for Asset
  def filter = "external_id equals '" + alarmId + "'";
      
  def searchResponse = paasRestClient.get(path: "/networking/rest/record/Alerts",
    requestContentType: JSON,
    contentType: JSON,
    params : [fieldList : "id,device_id,status", filter : filter],
    headers: [Cookie : "JSESSIONID=" + sessionId]);
  
  def data = searchResponse.getData()
  
  def update = { recordId ->
      updateReq = "<platform><record><status>DONE</status></record></platform>"

      paasRestClient.put(path: "/networking/rest/record/Alerts/" + recordId,
        requestContentType: XML,
        contentType: JSON,
        body: updateReq,
        headers: [Cookie : "JSESSIONID=" + sessionId]);
  }
  
  if (data.platform.record != null) {
    if (data.platform.record[0] == null) {
        update(data.platform.record.id)
    } else {
      data.platform.record.id.each{
        update(it.value)
      }
    }
  }
  
  // Logout request
  paasRestClient.get(path: "/networking/rest/logout",
      requestContentType: URLENC,
      contentType: XML,
      headers: [Cookie : "JSESSIONID=" + sessionId]);
  
} catch(Exception e){
  logger.error(e.getMessage());
}
