package com.platform.c2115417183.location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.platform.api.Functions;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

public class LocationControllerParameters {

  private List<String> msisdnList = new ArrayList<String>();

  private String msisdn;

  private LocationControllerParameters() {
    // hidden
  }

  public List<String> getAllMSISDNs() {
    return msisdnList;
  }

  public String getMSISDN() {
    return msisdn;
  }

  @SuppressWarnings("rawtypes")
  public static LocationControllerParameters getInstance(HashMap requestParams) throws Exception {
    LocationControllerParameters params = new LocationControllerParameters();

    String ids = (String) requestParams.get(LocationController.IDS_PARAMETER);
    if (ids != null) {
      String[] idTable = ids.split(",");
      for (String id : idTable) {
        final Result searchResult = Functions.searchRecords("Engineers", "id,msisdn", "id=" + id);
        final ParametersIterator resultIterator = searchResult.getIterator();

        if (resultIterator.hasNext()) {
          Parameters result = resultIterator.next();

          params.msisdnList.add(result.get("msisdn"));
        }
      }
    }
    String id = (String) requestParams.get(LocationController.ID_PARAMETER);
    if (id != null) {
      final Result searchResult = Functions.searchRecords("Engineers", "id,msisdn", "id=" + id);
      final ParametersIterator resultIterator = searchResult.getIterator();
      if (resultIterator.hasNext()) {
        Parameters result = resultIterator.next();
        params.msisdn = result.get("msisdn");
      }
    }
    return params;
  }
}