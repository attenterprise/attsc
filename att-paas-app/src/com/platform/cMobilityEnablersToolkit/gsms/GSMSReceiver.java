package com.platform.cMobilityEnablersToolkit.gsms;

import java.util.HashMap;

import com.att.enablers.gsms.GSMSAttributes;
import com.platform.api.*;

/**
 * Controller class responsible for processing SMS messages received on the platform.
 * The message source is checked to make sure it is configured on the platform.
 * If the source is not a valid phone number listed in the GSMS Overview object, the message
 * will be rejected.
 * The valid message details are printed into the debug log.
 * 
 * @author Marina Bashtovaya
 *
 */
public class GSMSReceiver implements Controller{
	
	@SuppressWarnings("rawtypes")
	public ControllerResponse execute(HashMap params) 
	throws Exception {
		
		// extra security - can only get replies to the messages sent
		String source = (String)params.get(GSMSAttributes.SendMessageAttributes.SOURCE);
		com.platform.api.Logger.debug("GSMSReceiver source="+source, this.getClass());

		if (GSMSHelper.isValidSender(source)){
			// retrieve parameters of the message
			String destination = (String)params.get(GSMSAttributes.SendMessageAttributes.DESTINATION);	
			String text = (String)params.get(GSMSAttributes.SendMessageAttributes.TEXT);
			String clientMessageId = (String)params.get(GSMSAttributes.SendMessageAttributes.CLIENT_MESSAGE_ID);
			String costCentre = (String)params.get(GSMSAttributes.SendMessageAttributes.COST_CENTRE);

			// in case if the message is a multi-part message
			String segmentCount = (String)params.get(GSMSAttributes.ReceiverAttributes.SEGMENT_COUNT);
			String segmentNumber = (String)params.get(GSMSAttributes.ReceiverAttributes.SEGMENT_NUMBER);
			String referenceNumber = (String)params.get(GSMSAttributes.ReceiverAttributes.REFERENCE_NUMBER);
			
			com.platform.api.Logger.debug("GSMSReceiver destination="+destination, this.getClass());
			com.platform.api.Logger.debug("GSMSReceiver text="+text, this.getClass());
			com.platform.api.Logger.debug("GSMSReceiver clientMessageId="+clientMessageId, this.getClass());
			com.platform.api.Logger.debug("GSMSReceiver costCentre="+costCentre, this.getClass());
			com.platform.api.Logger.debug("GSMSReceiver multi-part: segmentCount="+segmentCount+"; segmentNumber="+segmentNumber+"; referenceNumber="+referenceNumber, this.getClass());

			// ----------------------------------------------------
			// TODO: Business logic here			

		}
		else{
			com.platform.api.Logger.error("GSMSReceiver - rejected because of unknown senders phone: "+source, this.getClass());
		}
		
		// ----------------------------------------------------
		// page that will be returned as a reply to GSMS
		String successPage = (String)params.get("successPage");
		if (successPage == null){
			throw new Exception("GSMSReceiver - success page is not defined for the GSMS reply");
		}
		
		ControllerResponse cr = new ControllerResponse();
		cr.setData(params);
		cr.setTargetPage(successPage);
		
		return cr;
	}
	
}