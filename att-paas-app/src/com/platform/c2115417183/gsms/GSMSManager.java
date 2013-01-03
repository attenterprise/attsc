/* 
 * Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
 * 
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
 * For more information contact: g15287@att.att-mail.com
 */
package com.platform.c2115417183.gsms;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;

import com.platform.api.Functions;
import com.platform.api.HttpConnection;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class GSMSManager {
  private static final String INVALID_MESSAGE_ID = "0";
  private static final Pattern MESSAGE_ID_PATTERN = Pattern.compile("Message-ID:\\s+([0-9]+)");

  public boolean sendSms(String msisdn, String assetId, String lat, String lng) throws GSMSException {
    final String message = String.format("Asset '%s' (%s, %s) is damaged", assetId, lat, lng);

    return sendSms(msisdn, message);
  }

  public boolean sendSms(String msisdn, String message) throws GSMSException {
    try {
      Logger.info("Sending SMS", GSMSManager.class);

      final GSMSSetup setup = GSMSSetup.getInstance();
      final GSMSRequestBuilder requestBuilder = new GSMSRequestBuilder(setup);

      Logger.info("Recipient: " + msisdn, GSMSManager.class);
      Logger.info("Message: " + message, GSMSManager.class);

      HttpConnection conn = requestBuilder.createSendSmsRequest(msisdn, message);
      conn.execute();

      final String gsmsResponse = conn.getResponse();
      Logger.info("Send SMS Response: " + gsmsResponse, GSMSManager.class);

      final String messageId = getMessageId(gsmsResponse);

      return !INVALID_MESSAGE_ID.equals(messageId);
    } catch (Exception e) {
      Logger.error("GSMS Error: " + e.getMessage(), GSMSManager.class);

      throw new GSMSException("Unable to send SMS.", e);
    }
  }

  private String getMessageId(String response) {
    String messageId = "0";

    Matcher matcher = MESSAGE_ID_PATTERN.matcher(response);
    if (matcher.find()) {
      messageId = matcher.group(1);
    }

    return messageId;
  }

  public void subscribe(String firstName, String lastName, String msisdn) throws GSMSException {
    final GSMSSetup setup = GSMSSetup.getInstance();
    final GSMSRequestBuilder requestBuilder = new GSMSRequestBuilder(setup);

    String contactId = createNewContact(firstName, lastName, msisdn, requestBuilder);
    addContactToDefaultGroup(setup, requestBuilder, contactId);
    updateSubscriptionStatus(msisdn, "SUBSCRIBED");
  }

  private String createNewContact(String firstName, String lastName, String msisdn, final GSMSRequestBuilder requestBuilder) throws GSMSException {
    try {
      HttpConnection addContact = requestBuilder.createContactRequest(firstName, lastName, msisdn);
      addContact.execute();

      GSMSResponseParser responseParser = new GSMSResponseParser(addContact.getResponse());

      if (responseParser.isStatusCodeValid()) {
        String contactId = responseParser.findContactId();
        Logger.info("Contact ID: " + contactId, GSMSManager.class);

        return contactId;
      } else {
        throw new GSMSException("Unable to create a new contact in GSMS");
      }
    } catch (JSONException e) {
      throw new GSMSException("Unable to create a new contact in GSMS", e);
    } catch (Exception e) {
      throw new GSMSException("Unable to create a new contact in GSMS", e);
    }
  }

  private void addContactToDefaultGroup(final GSMSSetup setup, final GSMSRequestBuilder requestBuilder, final String contactId) throws GSMSException {
    try {
      HttpConnection addToGroup = requestBuilder.createAddContactToGroupRequest(setup.getDefaultGroup(), contactId);
      addToGroup.execute();

      GSMSResponseParser responseParser = new GSMSResponseParser(addToGroup.getResponse());

      if (responseParser.isStatusCodeValid()) {
        Logger.info("Add contact to group response: " + addToGroup.getResponse(), GSMSManager.class);
      } else {
        throw new GSMSException("Unable to add a new contact to the default group.");
      }
    } catch (Exception e) {
      throw new GSMSException("Unable to add a new contact to the default group.", e);
    }
  }

  private void updateSubscriptionStatus(String msisdn, String newStatus) throws GSMSException {
    try {
      Result searchResult = Functions.searchRecords("Engineers", "id", "msisdn=" + msisdn);
      ParametersIterator paramsIter = searchResult.getIterator();

      if (paramsIter.hasNext()) {
        Parameters searchParams = paramsIter.next();
        String id = searchParams.get("id");

        Parameters params = new Parameters();
        params.add("gsms_subscription_status", newStatus);
        Functions.updateRecord("Engineers", id, params);

        Logger.info("Status of '" + msisdn + "' changed into: " + newStatus, GSMSManager.class);
      }
    } catch (NoSuchElementException e) {
      throw new GSMSException("Unable to update subscription status.", e);
    } catch (Exception e) {
      throw new GSMSException("Unable to update subscription status.", e);
    }
  }
}