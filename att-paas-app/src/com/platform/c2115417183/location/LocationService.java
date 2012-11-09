package com.platform.c2115417183.location;

import java.util.List;
import java.util.Map;

public interface LocationService {

  void subscribe(String msisdns) throws Exception;

  void unsubscribe(String msisdns) throws Exception;

  Map<String, Coordinates> locateMsisdns(List<String> msisdns) throws Exception;

}