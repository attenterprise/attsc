<html >
<body>

<p>Welcome to AT&T Mobility Enablers Toolkit.</p>
<p>
The purpose of the toolkit is to demonstrate the Mobility Enablers usage within PaaS.
The toolkit contains various elements of PaaS, such as, Components, Pages, policy action Java methods, Java controller classes. It also includes the SMS Receiver Site. 
</p>
<p>
<h3>Mobility Enablers available:</h3>
<ul>
<li>AT&T Global Smart Messaging Suit (GSMS) powered by Soprano</li>
<li>AT&T Location Information Services (LIS) powered by LocAid</li>
</ul>
</p>
<p>
In order to take advantage of the toolkit the accounts need to be setup for each service. If you have GSMS and LIS accounts open already, they can be re-used.
Please refer to <a href='../tools/enablers/' target='_blank'>Service Integration APIs</a> for more information.
</p>
<p>
<h3>Upon installation, make sure the following steps are taken:</h3>
<ul>
<li>Create a new user, e.g. SMSReceiverUser_&lt;tenantID&gt;. It can be done under Settings-&gt;Administration-&gt;Users. Select 'My Team' as a primary team and 'SMS Receiver Role' as a role in the team.</li>
<li>Setup SMSReceiverUser as a user to be used for unauthenticated site access. It can be done under Designer-&gt;Logic-&gt;Sites-&gt;SMSReceiverSite. Edit the site and select the created user as 'Login As User for Unauthenticated Access'.
</ul>
</p>
<p>
<h3>Objects and Functionality:</h3>
<ul>
<li><b>GSMS Setup</b> is an object containing GSMS configuration. The configuration includes the service URL, user, password, and a company short code. 
Optionally, GSMS Receiver URL can be configured. The URL can be found under Designer-&gt;Logic-&gt;Sites-&gt;SMS Receiver Site. 
Choose the "Default Secure Web Address".</li>
<li><b>GSMS Overview</b> demonstrates different ways of adding GSMS commands to the object. That includes Actions and Data Policies. 
Actions are exposed as single or multi-selection buttons. 
Data policies are attached to the add/edit operations on the record and can be activated by selecting the corresponding to a command checkbox.
The URL to GSMS Receiver site is passed with each SMS text message and is used to receive SMS replies on PaaS.</li>
<li><b>LocAid Setup</b> is an object containing LIS configuration. The configuration includes the service URLs, user's login, 
password, and an application class ID. </li>
<li><b>LocAid Overview</b> demonstrates different ways of adding LIS functionality to the object. That includes Actions and Data Policies. 
Actions are exposed as single or multi-selection buttons. 
Data policies are attached to the add/edit operations on the record and can be activated by selecting the corresponding to a command checkbox.</li>
</ul>
</p>
</body>
</html>