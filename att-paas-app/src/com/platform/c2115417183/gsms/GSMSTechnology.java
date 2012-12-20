/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.gsms;

public enum GSMSTechnology {
  LEAST_EXPENSIVE("LEAST_EXPENSIVE"),
  MOST_ACCURATE("MOST_ACCURATE"),
  CELL("CELL"),
  AGPS("A-GPS");
  
  private final String value;
  
  private GSMSTechnology(String value) {
    this.value = value;
  }
  
  public String getGSMSValue() {
    return value;
  }
}