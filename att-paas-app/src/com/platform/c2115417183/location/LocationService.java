/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.location;

import java.util.List;
import java.util.Map;

public interface LocationService {

  void subscribe(String msisdns) throws Exception;

  void unsubscribe(String msisdns) throws Exception;

  Map<String, Coordinates> locateMsisdns(List<String> msisdns) throws Exception;

}