package com.platform.c2115417183.gsms;

import java.net.URLEncoder;

import sun.misc.BASE64Encoder;

import com.platform.api.CONSTANTS;
import com.platform.api.HttpConnection;
import com.platform.api.Logger;

public class GSMSRequestBuilder {

  private final GSMSSetup setup;
  private final GSMSUrlBuilder urlBuilder;

  public GSMSRequestBuilder(GSMSSetup setup) {
    this.setup = setup;
    this.urlBuilder = new GSMSUrlBuilder(setup);
  }

  public HttpConnection createContactRequest(String firstName, String lastName, String mobile) throws GSMSException {
    try {
      HttpConnection req = new HttpConnection(CONSTANTS.HTTP.METHOD.POST, urlBuilder.createContactUrl());

      req.addParameter("firstName", firstName);
      Logger.info("firstName: " + firstName, GSMSRequestBuilder.class);

      req.addParameter("lastName", lastName);
      Logger.info("lastName: " + lastName, GSMSRequestBuilder.class);

      req.addParameter("mobile", mobile);
      Logger.info("mobile: " + mobile, GSMSRequestBuilder.class);

      addAuthenticationHeader(req);

      return req;
    } catch (Exception e) {
      throw new GSMSException("GSMS Request cannot be prepared.", e);
    }
  }

  public HttpConnection createAddContactToGroupRequest(String groupId, String contactId) throws GSMSException {
    HttpConnection req;
    try {
      req = new HttpConnection(CONSTANTS.HTTP.METHOD.POST, urlBuilder.createAddContactToGroupUrl());

      req.addParameter("groupId", groupId);
      Logger.info("groupId: " + groupId, GSMSRequestBuilder.class);

      req.addParameter("contactIds", contactId);
      Logger.info("contactIds: " + contactId, GSMSRequestBuilder.class);

      req.addParameter("subAction", "50");
      Logger.info("subAction: 50", GSMSRequestBuilder.class);

      req.addParameter("invitationType", "2");
      Logger.info("invitationType: 2", GSMSRequestBuilder.class);

      req.addParameter("mobileStatus", "60");
      Logger.info("mobileStatus: 60", GSMSRequestBuilder.class);

      addAuthenticationHeader(req);
      
      return req;
    } catch (Exception e) {
      throw new GSMSException("GSMS Request cannot be prepared.", e);
    }
  }

  @SuppressWarnings("deprecation")
  public HttpConnection createSendSmsRequest(String destination, String text) throws Exception {
    HttpConnection req = new HttpConnection(CONSTANTS.HTTP.METHOD.POST, urlBuilder.createSendSmsUrl());

    req.addParameter("destination", destination);
    Logger.info("destination: " + destination, GSMSRequestBuilder.class);

    req.addParameter("text", URLEncoder.encode(text));
    Logger.info("text: " + text, GSMSRequestBuilder.class);

    addAuthenticationHeader(req);

    return req;
  }

  private void addAuthenticationHeader(HttpConnection req) throws GSMSException {
    try {
      BASE64Encoder encoder = new BASE64Encoder();
      String authString = setup.getUsername() + ":" + setup.getPassword();
      String authBase64 = encoder.encode(authString.getBytes());

      Logger.info("Authorization: Basic " + authBase64, GSMSRequestBuilder.class);
      req.addHeader("Authorization", "Basic " + authBase64);
    } catch (Exception e) {
      throw new GSMSException("GSMS Request cannot be prepared.", e);
    }
  }
}