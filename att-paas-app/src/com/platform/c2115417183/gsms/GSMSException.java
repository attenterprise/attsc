/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.gsms;

@SuppressWarnings("serial")
public class GSMSException extends Exception {

  public GSMSException(String message) {
    super(message);
  }
  
  public GSMSException(String message, Throwable cause) {
    super(message, cause);
  }
  
}