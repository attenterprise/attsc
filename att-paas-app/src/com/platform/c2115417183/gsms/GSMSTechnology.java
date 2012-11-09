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