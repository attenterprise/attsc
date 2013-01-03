/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.engineers;

public class SearchResult implements Comparable<SearchResult> {

	private final String msisdn;
	private final double distance;

	public SearchResult(String msisdn, double distance) {
		this.msisdn = msisdn;
		this.distance = distance;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public double getDistance() {
		return distance;
	}

	@Override
	public int compareTo(SearchResult otherResult) {
		return Double.compare(distance, otherResult.distance);
	}

}