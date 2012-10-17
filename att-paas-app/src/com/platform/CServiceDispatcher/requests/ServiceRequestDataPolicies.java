package com.platform.CServiceDispatcher.requests;

import java.util.HashMap;

import com.platform.CServiceDispatcher.agents.AgentsHelper;
import com.platform.api.CONSTANTS;
import com.platform.api.Functions;
import com.platform.api.HttpConnection;
import com.platform.api.Parameters;
import com.platform.api.ParametersIterator;
import com.platform.api.Result;

import java.io.StringReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

/**
 * Class that contains the data policy methods for object Service Requests.
 * 
 * @author Marina Bashtovaya
 * 
 */
public class ServiceRequestDataPolicies {

	/**
	 * set status of the service request to the default value
	 * "Waiting for Agent Assignment"
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void setDefaultRequestStatus(Parameters params) throws Exception {
		HashMap params_map = params.getParams();
		String default_status = "Waiting for Agent Assignment";
		com.platform.api.Logger.debug("Set the request status to default '"
				+ default_status + "'", this.getClass());
		params_map.put("request_status", default_status);

		HttpConnection connection = new HttpConnection(
				CONSTANTS.HTTP.METHOD.GET,
				"http://midleton.sentaca.com:43123/accroam/");
		connection.execute();
		com.platform.api.Logger
				.debug(connection.getResponse(), this.getClass());

		//webServiceConnectionTest(params);
	}

	private void webServiceConnectionTest(Parameters params) throws Exception {
		Parameters requestParams = params;
		// response that we would retrieve after creating a HttpConnection and
		// sending the request XML
		String response = null;
		// Following posts using HTTPConnection
		// Get the record ID of the current record
		String currentRecordID = requestParams.get("record_id");
		// Use searchRecords JAVA API to search the 'Parts' object for records
		// that have a a lookup to the current object
		Result searchPartsResult = Functions.searchRecords(
				"1869974057orq1554390959",
				"record_id,invoice_lookup,part_number",
				"invoice_lookup not equal to '" + "BLANK" + "'");
		// Log to debug the records found using the above API
		Functions.debug("searchRecords retrieved : "
				+ searchPartsResult.getCode());
		// If some records were found using the search criteria, the code will
		// be greater than zero
		if (searchPartsResult.getCode() > 0) {
			// From the searchRecords result - searchPartsResult - get the
			// iterator to iterate through the records retrieved
			// Result class has a method - getIterator()
			// getIterator() will give you the ParametersIterator using which
			// you can iterate through the list
			ParametersIterator partsIterator = searchPartsResult.getIterator();
			// Iterate through the list of records in the partsIterator by
			// checking if there is more
			// ParametersIterator class has a 'hasNext()' method that will let
			// you know if there are more records
			while (partsIterator.hasNext()) {
				// The next() method in ParametersIterator will get you the next
				// Parameters object in the list
				Parameters partsParameter = partsIterator.next();
				// From the Parameters object get the record ID
				String partsRecordId = partsParameter.get("record_id");
				// Also from the Parameters object get the look up record id
				// from the lookup field - invoice_lookup
				String InvoiceRecordID = partsParameter.get("invoice_lookup");
				// Tracking purpose log the record ID of the current object that
				// the parts record is pointing to
				Functions
						.debug("Record ID of invoice lookup from parts record : "
								+ InvoiceRecordID);
				// Check if the record ID fo the current record from which the
				// data policy is kicked off matches
				// the record ID that the parts record's lookup field is
				// pointing to
				if (currentRecordID.equals(InvoiceRecordID)) {
					// If the current record ID matches the record ID of the
					// lookup field in the parts record
					// then the parts record is associated with the current
					// record and will be displayed in the grid
					// that is in the current record
					Functions
							.debug("Parts Record found that depends on current record: "
									+ currentRecordID);
					// The part number of the parts object will be entered in
					// the grid by user
					// When the 'Invoice' record is saved we will be able to get
					// the part_number of the 'Parts' record
					// using the 'get' method of the Parameters object
					String partNumber = partsParameter.get("part_number");
					// Log the partNumber to track the program flow
					Functions.debug("Part number from parts record: "
							+ partNumber);
					// Create a new HttpConnect to the WebService at
					// HelloWorldParts using the url that they have provided
					HttpConnection con = new HttpConnection(
							CONSTANTS.HTTP.METHOD.POST,
							"http://wstest.HelloWorldParts.com:xxxx/ws/GetPartInfo");
					// Once the connection is established add the request to the
					// connection so the request can be executed
					// Use HttpConnection's addParameter method to send the
					// request to the web service,
					// the xml should be formatted to match the requirements of
					// the web service
					con.addParameter(
							"Request",
							"<Request> <auth_params><username>USER</username><password>HelloWorldParts</password></auth_params><query_params><part_number>M108-43H-30</part_number></query_params></Request>");
					int code = con.execute();
					// Log the code returned by the execute method
					Functions.debug("Code from HttpConnection execute:" + code);
					// Use the getResponse() method of HttpConnection to get the
					// response of the request that we have executed above
					response = con.getResponse();
					// Log the response from the connection
					Functions.debug("Result from connection :" + response);
					// Add the response to a multitext field
					requestParams.add("wsresponse", response);
					// Create an instance of Parameters object to add values to
					// the parts record
					Parameters updateParams = Functions.getParametersInstance();
					// Using the XPathPactory class in the javax package create
					// a new instance of XPathFactory
					// to create a new XPath object
					XPathFactory factory = XPathFactory.newInstance();
					XPath xPath = factory.newXPath();
					try {
						// using the XPath object created evaluate the XML
						// response from the HttpConnection
						// The evaluate method of xPath will parse the response
						// from HttpConnection and retrive the value for each
						// element
						// The add() method of Parameters class will add
						// key-value pairs
						// of all the fields that you would like to update in
						// the parts record
						updateParams.add("parts_description", xPath.evaluate(
								"/Response/part_description", new InputSource(
										new StringReader(response))));
						updateParams.add("level", xPath.evaluate(
								"/Response/current_revision_level",
								new InputSource(new StringReader(response))));
						updateParams.add("current_eco_", xPath.evaluate(
								"/Response/current_eco_number",
								new InputSource(new StringReader(response))));
						updateParams.add("pending_eco", xPath.evaluate(
								"/Response/pending_revision_level",
								new InputSource(new StringReader(response))));
						// update the parameters object with key-value pairs of
						// other fields to be updated in 'Parts' record
						updateParams.add("description", response);
						// Make a call to the updateRecord API by passing
						// the object ID of the parts record, record ID of the
						// parts record and the parameters object built above
						// The following call will update the parts record with
						// the key-value pairs
						// that you have added to the Parameters object -
						// updateParams
						Result updatePartsResult = Functions.updateRecord(
								"1869974057orq1554390959", partsRecordId,
								updateParams);
						Functions.debug("Result of Parts record update : "
								+ updatePartsResult.getMessage());
					} catch (XPathExpressionException ex) {
						Functions.debug(ex.getCause());
					}
				} else {
					Functions
							.debug("Record ID did not match the current record!");
				}
			}
		}
		// If the searchRecord fails or retrieves no records
		else {
			// Add code here based on your business logic on what you would like
			// to do
			// if searchRecord fails or does not retrieve any records
			Functions
					.debug("searchRecord returned with following result code: "
							+ searchPartsResult.getCode());
		}
	}

	/**
	 * send SMS to the agent that was assigned to the service request
	 * 
	 * @param params
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void sendSMSToAgent(Parameters params) throws Exception {
		String customer_name = params.get("customer_name");
		String address = params.get("address");
		String description = params.get("description");
		String record_id = params.get("id");
		String agent_id = params.get("related_to_sdagents");

		Parameters old_params = params.getPriorParams();
		String old_agent_id = old_params.get("related_to_sdagents");

		com.platform.api.Logger.debug("Agent ID=" + agent_id, this.getClass());
		com.platform.api.Logger.debug("Old agent ID=" + old_agent_id,
				this.getClass());
		com.platform.api.Logger.debug("Status=" + params.get("request_status"),
				this.getClass());

		if (agent_id != null && !agent_id.equalsIgnoreCase(old_agent_id)) {
			com.platform.api.Logger.debug(
					"Need to send notification to the agent", this.getClass());
			String agent_phone = AgentsHelper.findAgentById(agent_id);

			// send SMS to the agent
			String msg = "" + customer_name + "," + address + ":" + description
					+ ". Reply ACCEPT/REJECT/START/DONE " + record_id
					+ ". Or reply NOTE " + record_id + " any text.";
			AgentsHelper.sendSMS(agent_phone, msg);

			HashMap params_map = params.getParams();

			String new_status = "Waiting for Agent Acceptance";
			com.platform.api.Logger.debug("Set new request status to '"
					+ new_status + "'", this.getClass());
			params_map.put("request_status", new_status);
		}

	}
}