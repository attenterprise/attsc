package com.platform.c2115417183.gsms;

import com.platform.api.Logger;

public class GSMSUrlBuilder {

  private final GSMSSetup setup;

  public GSMSUrlBuilder(GSMSSetup setup) {
    this.setup = setup;
  };
  
  public String createContactUrl() {
    StringBuilder urlBuilder = new StringBuilder();
    
    urlBuilder.append(this.setup.getServiceUrl());
    urlBuilder.append("/sei/contactApiCreate.do");
    
    Logger.info("create contact URL: " + urlBuilder.toString(), GSMSUrlBuilder.class);
    
    return urlBuilder.toString();
  }
  
  public String createAddContactToGroupUrl() {
    StringBuilder urlBuilder = new StringBuilder();
    
    urlBuilder.append(this.setup.getServiceUrl());
    urlBuilder.append("/sei/groupContactApiCreateDeactivateInvite.do");
    
    Logger.info("add contact to group URL: " + urlBuilder.toString(), GSMSUrlBuilder.class);
    
    return urlBuilder.toString();
  }
  
  public String createSendSmsUrl() {
    StringBuilder urlBuilder = new StringBuilder();
    
    urlBuilder.append(this.setup.getServiceUrl());
    urlBuilder.append("/cgphttp/servlet/sendmsg");
    
    Logger.info("send SMS URL: " + urlBuilder.toString(), GSMSUrlBuilder.class);
    
    return urlBuilder.toString();
  }

}