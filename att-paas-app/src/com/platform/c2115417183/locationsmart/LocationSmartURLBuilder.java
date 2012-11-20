package com.platform.c2115417183.locationsmart;

public class LocationSmartURLBuilder {

  private LocationSmartSetup setup;

  public LocationSmartURLBuilder(LocationSmartSetup setup) {
    this.setup = setup;
  }

  public String createLocationUrl(String msisdn) throws Exception {
    String subscriptionGroup = setup.getSubscriptionGroup();
    String url = setup.getServiceUrl() + "/locreq.xml/?privacyConsent=False&tn=" + msisdn.substring(1) + "&subscriptionGroup=" + subscriptionGroup;
    url += "&accuracyReq=" + setup.getAccuracy().name();
    
    return url;
  }

  public String createSubscriptionUrl(String msisdn, String subscriptionAction) {
    String subscriptionGroup = setup.getSubscriptionGroup();
    String url = setup.getServiceUrl() + "/subscriptionreq.xml/?tn=" + msisdn.substring(1) + "&subscriptionGroup=" + subscriptionGroup + "&subscriptionAction="
        + subscriptionAction;
    return url;
  }

}