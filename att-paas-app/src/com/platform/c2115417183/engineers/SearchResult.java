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