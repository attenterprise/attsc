package com.platform.cMobilityEnablersToolkit.gsms;

import java.util.*;

import com.platform.api.*;
import com.att.enablers.gsms.*;

/**
 * Class that implements data policies used by GSMS Overview object.
 * 
 * @author Marina Bashtovaya
 *
 */
public class GSMSDataPolicies {
	
	/**
	 * Normalize the entered phone by removing spaces, dashes and parenthesis.<br>
	 * It gets triggered on Add/Update action for the object.<br>
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void normalizePhone(Parameters params) throws Exception
	{
		HashMap params_map = params.getParams();

		String phone = (String) params_map.get("phone");
		
		if (phone != null){
			com.platform.api.Logger.debug("Entered phone="+phone, this);
			
			if (!phone.matches("([ ()0-9]|-)+")){
				throw new Exception("Invalid cell phone format.");
			}
			
			phone = phone.replaceAll("-", "").replaceAll("[ ()]", "");
			
			com.platform.api.Logger.debug("Normalized phone="+phone, this);
			params_map.put("phone", phone);
		}
	}

	/**
	 * send SMS message with the provided text.
	 * The result is printed in the debug log.
	 * 
	 * @param params
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendSMS(Parameters params){
		HashMap params_map = params.getParams();

		try{
			String text = (String) params.get("sms");
			String send_sms = (String) params_map.get("send_sms");
			if (text != null && !text.trim().isEmpty() && "1".equalsIgnoreCase(send_sms)){
				params_map.put("send_sms", "0");

				com.platform.api.Logger.debug("Message="+text, java.lang.String.class);
				
				String phone = (String) params.get("phone");
				if (phone == null){
					com.platform.api.Logger.error("Phone is not provided", this);
					return;
				}
	
				// find GSMS settings
				String[] account = GSMSHelper.getSMSConfiguration();
		
				GSMSServices gsms = new GSMSServices(account[1], account[2], account[3]);
				
				Map<String,Object> attributes = new HashMap<String, Object>();				
				attributes.put(GSMSAttributes.SendMessageAttributes.DESTINATION, phone); 
				attributes.put(GSMSAttributes.SendMessageAttributes.TEXT, text);
				attributes.put(GSMSAttributes.SendMessageAttributes.RESPONSE_TYPE, GSMSAttributes.ResponseType.XML);
				if (account[5] != null){
					// Warning: source must be excluded when the reply to is URL
					attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO_TON, GSMSAttributes.ReplyTo.TON_URL);
					attributes.put(GSMSAttributes.SendMessageAttributes.REPLY_TO, account[5]);
				}				
				int status_code = gsms.sendMessage(attributes);
				
				GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
	
				if (response == null){
					com.platform.api.Logger.error("Internal error - unknown response of the sendSMS command: "+response, this);
					return;
				}
				
				if (status_code == 200){
					List<GSMSResponseMessage> messages = response.getMessages();
					if (messages != null){
						for (GSMSResponseMessage message: messages){
							if (message.getErrorCode() != 0){
								com.platform.api.Logger.error("("+message.getErrorCode()+" "+message.getResponseCode()+") "+message.getResponseMessage(), this);
							}
							com.platform.api.Logger.debug("MessageID="+message.getMessageID(), this);
							com.platform.api.Logger.debug("Message="+message.getResponseMessage(), this);

							com.platform.api.Logger.debug("Destination="+message.getDestination(), this);
							com.platform.api.Logger.debug("DestinationCode="+message.getDestinationCode(), this);
							com.platform.api.Logger.debug("DestinationDescription="+message.getDestinationDescription(), this);							
						}
					}
				}				
			}
		}
		catch(Exception e){
			com.platform.api.Logger.error(e, this);
		}
	}

	/**
	 * send WAP Push message with the provided text.
	 * The result is printed in the debug log.
	 * 
	 * @param params
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendWapPush(Parameters params){
		HashMap params_map = params.getParams();

		try{
			String text = (String) params.get("wap_push_message");
			String send_wap_push = (String) params_map.get("send_wap_push");
			if (text != null && !text.trim().isEmpty() && "1".equalsIgnoreCase(send_wap_push)){
				params_map.put("send_wap_push", "0");
				
				com.platform.api.Logger.debug("wap_push_message="+text, java.lang.String.class);

				String phone = (String) params.get("phone");
				if (phone == null){
					com.platform.api.Logger.error("Phone is not provided", this);
					return;
				}
	
				// find GSMS settings
				String[] account = GSMSHelper.getSMSConfiguration();
	
				GSMSServices gsms = new GSMSServices(account[1], account[2], account[3]);
				
				int status_code = gsms.sendWapPush(account[4], phone, "wap.mysite.com/wap", text, true, GSMSAttributes.ResponseType.XML);
				
				GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
	
				if (response == null){
					com.platform.api.Logger.error("Internal error - unknown response of the sendWapPush command: "+response, this);
					return;
				}
				
				if (status_code == 200){
					List<GSMSResponseMessage> messages = response.getMessages();
					if (messages != null){
						for (GSMSResponseMessage message: messages){
							if (message.getErrorCode() != 0){
								com.platform.api.Logger.error("("+message.getErrorCode()+" "+message.getResponseCode()+") "+message.getResponseMessage(), this);
							}
							com.platform.api.Logger.debug("MessageID="+message.getMessageID(), this);
							com.platform.api.Logger.debug("Message="+message.getResponseMessage(), this);
	
							com.platform.api.Logger.debug("Destination="+message.getDestination(), this);
							com.platform.api.Logger.debug("DestinationCode="+message.getDestinationCode(), this);
							com.platform.api.Logger.debug("DestinationDescription="+message.getDestinationDescription(), this);					
						}
					}
				}
			}
		}
		catch(Exception e){
			com.platform.api.Logger.error(e, this);
		}
	}
	
	/**
	 * send SMS message using start/send batch commands.
	 * The result is printed in the debug log.
	 * 
	 * @param params
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendSMSBatch(Parameters params){
		HashMap params_map = params.getParams();

		try{
			String text = (String) params.get("batch_sms");
			String startsend_batch_sms = (String) params_map.get("startsend_batch_sms");
			if (text != null && !text.trim().isEmpty() && "1".equalsIgnoreCase(startsend_batch_sms)){
				params_map.put("startsend_batch_sms", "0");

				com.platform.api.Logger.debug("Template="+text, java.lang.String.class);
	
				// find GSMS settings
				String[] account = GSMSHelper.getSMSConfiguration();

				GSMSServices gsms = new GSMSServices(account[1], account[2], account[3]);
					
				String[][] contents = new String[1][];
				contents[0] = new String[3];
				contents[0][0] = (String) params.get("phone");
				contents[0][1] = (String) params.get("first_name");
				contents[0][2] = (String) params.get("last_name");
				
				int status_code = gsms.startBatch(text, ",", GSMSAttributes.ResponseType.XML);
				
				GSMSResponse response = gsms.getHTTPResponseXMLAsObject();
	
				if (response == null){
					com.platform.api.Logger.error("Internal error - unknown response of the startbatch command: "+response, this);
					return;
				}
				
				if (status_code == 200){
					List<GSMSResponseMessage> messages = response.getMessages();
					if (messages != null){
						for (GSMSResponseMessage message: messages){
							if (message.getErrorCode() != 0){
								com.platform.api.Logger.error("("+message.getErrorCode()+" "+message.getResponseCode()+") "+message.getResponseMessage(), this);
							}
							String batchId = message.getBatchID();
							com.platform.api.Logger.debug("BatchID="+batchId, this);
							com.platform.api.Logger.debug("MessageID="+message.getMessageID(), this);
							com.platform.api.Logger.debug("Message="+message.getResponseMessage(), this);

							com.platform.api.Logger.debug("Destination="+message.getDestination(), this);
							com.platform.api.Logger.debug("DestinationCode="+message.getDestinationCode(), this);
							com.platform.api.Logger.debug("DestinationDescription="+message.getDestinationDescription(), this);
							
							if (batchId != null){
								status_code = gsms.sendBatch(batchId, contents, ",", GSMSAttributes.ResponseType.XML);
								
								response = gsms.getHTTPResponseXMLAsObject();
								
								if (response == null){
									com.platform.api.Logger.error("Internal error - unknown response of the startbatch command: "+response, this);
									return;
								}
								
								if (status_code == 200){
									messages = response.getMessages();
									if (messages != null){
										for (GSMSResponseMessage message2: messages){
											if (message2.getErrorCode() != 0){
												com.platform.api.Logger.error("("+message2.getErrorCode()+" "+message2.getResponseCode()+") "+message2.getResponseMessage(), this);
											}
											com.platform.api.Logger.debug("BatchID="+message2.getBatchID(), this);
											com.platform.api.Logger.debug("MessageID="+message2.getMessageID(), this);
											com.platform.api.Logger.debug("Message="+message2.getResponseMessage(), this);

											com.platform.api.Logger.debug("Destination="+message2.getDestination(), this);
											com.platform.api.Logger.debug("DestinationCode="+message2.getDestinationCode(), this);
											com.platform.api.Logger.debug("DestinationDescription="+message2.getDestinationDescription(), this);
										}
									}
								}
								
							}
						}
					}
				}
			}
		}
		catch(Exception e){
			com.platform.api.Logger.error(e, this);
		}
		
	}

}