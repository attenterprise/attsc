/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
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