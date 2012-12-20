/* Licensed by AT&T under 'Software Development Kit Tools Agreement.' 2012
 * TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: 
 * http://developer.att.com/apsl-1.0
 * Copyright 2012 AT&T Intellectual Property. All rights reserved. http://developer.att.com
 * For more information refer to http://pte.att.com/Engage.aspx
 */
package com.platform.c2115417183.engineers;

import java.util.ArrayList;
import java.util.List;

import com.platform.api.Functions;
import com.platform.api.Logger;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class EngineersDao {

  /**
   * Methods returns list of field's values from Engineers object
   * 
   * @param clause
   *          criteria to specify record to set
   * @param field
   *          field from which value will be returned
   * @return list of field's values
   * @throws Exception
   */
  public List<String> searchEngineers(String clause, String field) throws Exception {
    List<String> msisdnList = new ArrayList<String>();
    Logger.info("Engineer's search. clause: " + clause + ", field: " + field, EngineersService.class);
    Result result = Functions.searchRecords("Engineers", field, clause);
    int resultCode = result.getCode();
    Logger.info("resultCode: " + resultCode, EngineersService.class);
    if (resultCode < 0) {
      // Some error happened.
      String msg = "Engineers could not be retrieved";
      Logger.error(msg + "Engineers could not be retrieved:\n" + result.getMessage(), EngineersService.class);
      Functions.throwError(msg + "."); // Error dialog
    } else if (resultCode == 0) {
      // No records found.
      Logger.info("No records of Engineers found.", EngineersService.class);
    } else {
      // Records retrieved successfully
      ParametersIterator iterator = result.getIterator();
      while (iterator.hasNext()) {
        Parameters params = iterator.next();
        msisdnList.add(params.get(field));
      }
    }
    return msisdnList;
  }
  
  public void assignEngineer(String msisdn, String alertId) throws Exception {
    List<String> engineersIdsList = searchEngineers("msisdn=" + msisdn, "id");
    if (engineersIdsList.size() == 1) {
      String engineerId = engineersIdsList.get(0);
      Parameters params = new Parameters();
      params.add("related_to_engineers", engineerId);
      Functions.updateRecord("Alerts", alertId, params);
    } else {
      Logger.error("Cannot find engineers with msisdn: " + msisdn, EngineersService.class);
    }
  }

}