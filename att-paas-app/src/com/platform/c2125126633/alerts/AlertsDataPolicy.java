package com.platform.c2125126633.alerts;

import com.platform.api.Parameters;
import com.platform.api.Logger;

/**
 * Class that implements data policies used by Alerts object.
 * 
 * @author Magdalena Biala
 * 
 */
public class AlertsDataPolicy {
	public void processAlert(Parameters requestParams) {
		Logger.info("New failure alert received!", this.getClass());
	}

}