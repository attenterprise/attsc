package com.platform.c2115417183.locationsmart;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import com.platform.api.CONSTANTS;
import com.platform.api.Functions;
import com.platform.api.HttpConnection;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.c2115417183.engineers.EngineersService;
import com.platform.c2115417183.location.Coordinates;
import com.platform.c2115417183.location.LocationService;

public class LocationSmartService implements LocationService {

  private DocumentBuilder documentBuilder;

  private EngineersService engineersService = new EngineersService();

  public LocationSmartService() {
    super();
    try {
      documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
    } catch (ParserConfigurationException e) {
      Logger.error(e.getMessage(), LocationSmartService.class);
    }
  }

  @Override
  public void subscribe(String msisdn) throws Exception {
    manageSubscription(msisdn, "optin", "SUBSCRIBED");
  }

  @Override
  public void unsubscribe(String msisdn) throws Exception {
    manageSubscription(msisdn, "optout", "NOT SUBSCRIBED");
  }

  /**
   * Method for subscribe/unsubscribe msisdn.
   * 
   * @param msisdns
   *          list of msisdn
   * @param subscriptionAction
   *          optin/optout
   * @param status
   *          status to set after (un)subscription
   * @throws Exception
   */
  private void manageSubscription(String msisdn, String subscriptionAction, String status) throws Exception {
    LocationSmartSetup setup = LocationSmartSetup.getInstance();
    LocationSmartURLBuilder urlBuilder = new LocationSmartURLBuilder(setup);
    String url = urlBuilder.createSubscriptionUrl(msisdn, subscriptionAction);
    HttpConnection httpConnection = new HttpConnection(CONSTANTS.HTTP.METHOD.GET, url);
    httpConnection.execute();
    String responseString = httpConnection.getResponse();
    Logger.info("Response : " + responseString, LocationSmartService.class);
    Document documentResponse = documentBuilder.parse(new InputSource(new ByteArrayInputStream(responseString.getBytes("utf-8"))));
    Node node = null;
    for (int i = 0; i < documentResponse.getFirstChild().getChildNodes().getLength(); i++) {
      node = documentResponse.getFirstChild().getChildNodes().item(i);
      if (node.getNodeName().equals("errorCode")) {
        break;
      }
    }
    if (Integer.valueOf(node.getTextContent()) == 0) {
      List<String> result = engineersService.searchEngineers("msisdn=" + msisdn, "id");
      if (result.size() == 1) {
        String id = result.get(0);
        Parameters params = new Parameters();
        params.add("lis_subscription_status", status);
        Functions.updateRecord("Engineers", id, params);
      } else {
        Logger.error("Cannot update engineer's status.", LocationSmartService.class);
      }
    } else {
      Node errorMsgNode = null;
      for (int i = 0; i < documentResponse.getFirstChild().getChildNodes().getLength(); i++) {
        errorMsgNode = documentResponse.getFirstChild().getChildNodes().item(i);
        if (errorMsgNode.getNodeName().equals("errorMsg")) {
          break;
        }
      }
      Logger.error(errorMsgNode.getTextContent(), LocationSmartService.class);
      throw new Exception(errorMsgNode.getTextContent());
    }
  }

  @Override
  public Map<String, Coordinates> locateMsisdns(List<String> msisdns) throws Exception {
    LocationSmartURLBuilder urlBuilder = new LocationSmartURLBuilder(LocationSmartSetup.getInstance());
    Map<String, Coordinates> resultMap = new HashMap<String, Coordinates>();
    for (String msisdn : msisdns) {
      String url = urlBuilder.createLocationUrl(msisdn);
      HttpConnection httpConnection = new HttpConnection(CONSTANTS.HTTP.METHOD.GET, url);
      httpConnection.execute();
      String responseString = httpConnection.getResponse();
      Logger.info("Response : " + responseString, LocationSmartService.class);
      Document documentResponse = documentBuilder.parse(new InputSource(new ByteArrayInputStream(responseString.getBytes("utf-8"))));
      int errorCode = 0;
      String errorMsg = null;
      double latitude = 0;
      double longitude = 0;
      for (int i = 0; i < documentResponse.getFirstChild().getChildNodes().getLength(); i++) {
        Node node = documentResponse.getFirstChild().getChildNodes().item(i);
        if (node.getNodeName().equals("errorCode")) {
          errorCode = Integer.valueOf(node.getTextContent());
        }
        if (node.getNodeName().equals("errorMsg")) {
          errorMsg = node.getTextContent();
        }
        if (node.getNodeName().equals("geoAddress")) {
          for (int j = 0; j < node.getChildNodes().getLength(); j++) {
            Node subNode = node.getChildNodes().item(j);
            if (subNode.getNodeName().equals("latitude") && subNode.getTextContent() != null && !subNode.getTextContent().isEmpty()) {
              latitude = Double.valueOf(subNode.getTextContent()).doubleValue();
            }
            if (subNode.getNodeName().equals("longitude") && subNode.getTextContent() != null && !subNode.getTextContent().isEmpty()) {
              longitude = Double.valueOf(subNode.getTextContent()).doubleValue();
            }
          }
        }
      }
      if (errorCode == 0) { // ok
        resultMap.put(msisdn, new Coordinates(latitude, longitude));
      } else {
        Logger.error("Cannot locate msisdn: " + msisdn + ". Error: " + errorCode + ". " + errorMsg, LocationSmartService.class);
      }
    }
    return resultMap;
  }

}