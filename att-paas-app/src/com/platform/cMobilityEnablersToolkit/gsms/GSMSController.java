package com.platform.cMobilityEnablersToolkit.gsms;

import java.util.*;
import com.att.enablers.gsms.*;
import com.platform.api.*;

/**
 * Controller class that communicates with the AT&T GSMS service.
 * Action is a parameter that determines the outcome:<br>
 * <ul>
 * <li>"sendSMS" to send sms message</li>
 * <li>"sendBatch" to create a message template and send the batch of messages using it</li>
 * <li>"sendWAPPush" to send a WAP Push message</li>
 * <li>"cancelSMS" to cancelled the queued message</li>
 * <li>"querySMS" to retrieve the status of the message delivery</li>
 * </ul>
 * 
 * @author Marina Bashtovaya
 * 
 */
public class GSMSController implements Controller
{
	protected static final String ACTION = "action";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ControllerResponse execute(HashMap params) 
	throws Exception {
		
		GSMSHelper.printAllInputParams(params);

		// --------------------------------------------------------------------
		// find GSMS settings
		String[] account = GSMSHelper.getSMSConfiguration();

		GSMSServices gsms = new GSMSServices(account[1], account[2], account[3]);
		GSMSResponse response = null;
		String responseStr = null;

		String action = (String) params.get(ACTION);
		if ("sendSMS".equalsIgnoreCase(action)){
			response = sendSMS(params, account, gsms);
		}
		else if ("sendBatch".equalsIgnoreCase(action)){
			response = startSendBatch(params, account, gsms);
		}
		else if ("sendWAPPush".equalsIgnoreCase(action)){
			response = sendWAPPush(params, account, gsms);
		}
		else if ("cancelSMS".equalsIgnoreCase(action)){
			responseStr = cancelSMS(params, account, gsms);
		}
		else if ("querySMS".equalsIgnoreCase(action)){
			responseStr = querySMS(params, account, gsms);
		}
		else{
			throw new Exception("Unknown action: "+action);
		}
		
		// --------------------------------------------------------
		if (response != null){
			params.put("gsms_response", response);
		}
		if (responseStr != null){
			params.put("gsms_response_str", responseStr);
		}
		
		ControllerResponse cr = new ControllerResponse();
		cr.setData(params);
		cr.setTargetPage("smsDisplayStatus.jsp");		
		
		return cr;
	}
	
	@SuppressWarnings("rawtypes")
	protected GSMSResponse sendSMS(HashMap params, String[] account, GSMSServices gsms)
	throws Exception{
		String phone = (String) params.get("phone");
		if (phone == null){
			throw new Exception("Internal error - phone is not provided");
		}			
		com.platform.api.Logger.debug("Phone="+phone, this.getClass());
		
		String text = (String) params.get("message");
		if (text == null || text != null && text.trim().isEmpty()){
			throw new Exception("Internal error - message is not provided");
		}			
		com.platform.api.Logger.debug("Message="+text, this.getClass());
		
		String delay = (String) params.get("delay");
		com.platform.api.Logger.debug("delay="+delay, this.getClass());
		
		Map<String,Object> attributes = new HashMap<String, Object>();
		attributes.put(GSMSAttributes.SendMessageAttributes.DESTINATION, phone); 
		attributes.put(GSMSAttributes.SendMessageAttributes.TEXT, text);
		attributes.put(GSMSAttributes.SendMessageAttributes.RESPONSE_TYPE, GSMSAttributes.ResponseType.XML);
		if (delay != null && !delay.trim().isEmpty()){
			attributes.put(GSMSAttributes.SendMessageAttributes.DELAY, delay);
		}
		
		if (account[5] != null){
			// Warning: source must be excluded when the reply to is URL
			attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO_TON, GSMSAttributes.ReplyTo.TON_URL);
			attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO, account[5]);
		}
		
		com.platform.api.Logger.debug("SMS attributes: "+attributes, this.getClass());

		int status_code = gsms.sendMessage(attributes);
		
		com.platform.api.Logger.debug("SMS sent - status code = "+status_code, this.getClass());
		
		GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
		if (response == null){
			throw new Exception("Internal error - unknown response of the sendSMS command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}
	
	@SuppressWarnings("rawtypes")
	protected GSMSResponse startSendBatch(HashMap params, String[] account, GSMSServices gsms)
	throws Exception{
		String ids = (String) params.get("ids");
		if (ids == null){
			throw new Exception("Internal error - record IDs are not provided");
		}			
		com.platform.api.Logger.debug("Record IDs="+ids, this.getClass());
		
		List<String> phone_list = GSMSHelper.getPhonesForIds(ids);
		if (phone_list == null){
			throw new Exception("Internal error - phone list is not provided");
		}			
		com.platform.api.Logger.debug("Phones="+phone_list, this.getClass());
		
		String template = (String) params.get("template");
		if (template == null){
			throw new Exception("Internal error - template is not provided");
		}			
		com.platform.api.Logger.debug("Batch template="+template, this.getClass());
		
		Map<String,Object> attributes = new HashMap<String, Object>();
		attributes.put(GSMSAttributes.StartBatchAttributes.TEMPLATE, template); 
		attributes.put(GSMSAttributes.StartBatchAttributes.DELIMITER, ",");
		attributes.put(GSMSAttributes.StartBatchAttributes.RESPONSE_TYPE, GSMSAttributes.ResponseType.XML);
		
		com.platform.api.Logger.debug("SMS attributes: "+attributes, this.getClass());

		int status_code = gsms.startBatch(attributes);
		
		com.platform.api.Logger.debug("SMS sent - status code = "+status_code, this.getClass());
		
		GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
		if (response == null){
			throw new Exception("Internal error - unknown response of the startBatch command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}

		// should be one response returned
		String batch_id = null;
		for (GSMSResponseMessage msg: response.getMessages()){ 
			batch_id = msg.getBatchID();
			break; 
		}

		// step 2 -- send the batch
		String[][] contents = new String[phone_list.size()][1];
		int i = 0;
		for (String a_phone: phone_list){
			contents[i ++][0] = a_phone;
		}

		status_code = gsms.sendBatch(batch_id, contents, ",", GSMSAttributes.ResponseType.XML);
		response = gsms.getHTTPResponseXMLAsObject();
		if (response == null){
			throw new Exception("Internal error - unknown response of the sendBatch command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}

	@SuppressWarnings("rawtypes")
	protected GSMSResponse sendWAPPush(HashMap params, String[] account, GSMSServices gsms)
	throws Exception{
		String phone = (String) params.get("phone");
		if (phone == null){
			throw new Exception("Internal error - phone is not provided");
		}			
		com.platform.api.Logger.debug("Phone="+phone, this.getClass());
		
		String text = (String) params.get("message");
		if (text == null || text != null && text.trim().isEmpty()){
			throw new Exception("Internal error - message is not provided");
		}			
		com.platform.api.Logger.debug("Message="+text, this.getClass());
		
		String url = (String) params.get("url");
		if (url == null || url != null && url.trim().isEmpty()){
			throw new Exception("Internal error - URL is not provided");
		}			
		com.platform.api.Logger.debug("URL="+text, this.getClass());

		boolean is_https = false;
		String https = (String) params.get("is_https");
		if ("1".equalsIgnoreCase(https)){
			is_https = true;
		}
		com.platform.api.Logger.debug("HTTPS="+is_https, this.getClass());
		
		Map<String,Object> attributes = new HashMap<String, Object>();
		attributes.put(GSMSAttributes.SendWapPushAttributes.DESTINATION, phone); 
		attributes.put(GSMSAttributes.SendWapPushAttributes.MESSAGE, text);
		attributes.put(GSMSAttributes.SendWapPushAttributes.URL, url);
		attributes.put(GSMSAttributes.SendWapPushAttributes.HTTPS, is_https);
		attributes.put(GSMSAttributes.SendWapPushAttributes.SOURCE, account[4]); 
		attributes.put(GSMSAttributes.SendMessageAttributes.RESPONSE_TYPE, GSMSAttributes.ResponseType.XML);
		
		com.platform.api.Logger.debug("SMS attributes: "+attributes, this.getClass());

		int status_code = gsms.sendWapPush(attributes);
		
		com.platform.api.Logger.debug("SMS sent - status code = "+status_code, this.getClass());
		
		GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
		if (response == null){
			throw new Exception("Internal error - unknown response of the sendBarcode command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}

	@SuppressWarnings("rawtypes")
	protected String cancelSMS(HashMap params, String[] account, GSMSServices gsms)
	throws Exception{
		String phone = (String) params.get("phone");
		if (phone == null){
			throw new Exception("Internal error - phone is not provided");
		}			
		com.platform.api.Logger.debug("Phone="+phone, this.getClass());
		
		String message_id = (String) params.get("message_id");
		if (message_id == null || message_id != null && message_id.trim().isEmpty()){
			throw new Exception("Internal error - message_id is not provided");
		}			
		com.platform.api.Logger.debug("Message ID="+message_id, this.getClass());
		
		int status_code = gsms.cancelMessage(message_id);
		
		com.platform.api.Logger.debug("Cancel SMS sent - status code = "+status_code, this.getClass());
		
		String response = gsms.getHTTPResponse();
		if (response == null){
			throw new Exception("Internal error - unknown response of the cancelMessage command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}

	@SuppressWarnings("rawtypes")
	protected String querySMS(HashMap params, String[] account, GSMSServices gsms)
	throws Exception{
		String phone = (String) params.get("phone");
		if (phone == null){
			throw new Exception("Internal error - phone is not provided");
		}			
		com.platform.api.Logger.debug("Phone="+phone, this.getClass());
		
		String message_id = (String) params.get("message_id");
		if (message_id == null || message_id != null && message_id.trim().isEmpty()){
			throw new Exception("Internal error - message_id is not provided");
		}			
		com.platform.api.Logger.debug("Message ID="+message_id, this.getClass());
		
		int status_code = gsms.queryMessage(message_id);
		
		com.platform.api.Logger.debug("Query SMS sent - status code = "+status_code, this.getClass());
		
		String response = gsms.getHTTPResponse();
		if (response == null){
			throw new Exception("Internal error - unknown response of the queryMessage command: "+response);
		}
		if (status_code != 200){
			throw new Exception("Error - status code returned: "+status_code);
		}
		
		return response;
	}
}