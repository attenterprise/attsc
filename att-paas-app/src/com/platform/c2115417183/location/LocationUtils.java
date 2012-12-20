/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.location;

public class LocationUtils {

  /**
   * Method calculating distance.
   * 
   * @param lat1
   * @param long1
   * @param lat2
   * @param long2
   * @return distance between (lat1, long1) and (lat1, long2)
   */
  public static double distanceFrom(double lat1, double long1, double lat2, double long2) {
    double R = 6371; // km
    double rLat1 = Math.toRadians(lat1);
    double rLat2 = Math.toRadians(lat2);

    double dLat = rLat2 - rLat1;
    double dLon = Math.toRadians(long2 - long1);
    double a = Math.sin(dLat / 2.0) * Math.sin(dLat / 2.0) + Math.sin(dLon / 2.0) * Math.sin(dLon / 2.0) * Math.cos(rLat1) * Math.cos(rLat2);
    double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double d = R * c;
    
    return d;
  }

}