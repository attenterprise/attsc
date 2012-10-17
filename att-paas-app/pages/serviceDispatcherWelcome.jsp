<p>The Service Dispatcher manages your service requests and the agents that execute the work. To monitor the service agents, the application takes advantage of AT&T Location Information Services. 
This way you know exactly where your agents are at any moment. And, once a new service request added, it can be assigned to the closest agent for the execution. 
You can manage your agents and service request by clicking on the workspace area links to the left.
</p>
<p>Here are a few links to help you get started:</p>
<ul>
<li><a href=javascript:top.showTabInIframe('9b3ff9916d324118ae3fa2c36d35c725-1','','Service?t=101&targetpage=AppCenterVerifyObjectExistence.jsp&type=record&object_id=9b3ff9916d324118ae3fa2c36d35c725')>Add a new Agent</a></li>
<li><a href=javascript:top.showTabInIframe('8599166147354f0aa0b2ea75108ffa52-1','','Service?t=101&targetpage=AppCenterVerifyObjectExistence.jsp&type=record&object_id=8599166147354f0aa0b2ea75108ffa52')>Add a new Service Request</a></li>
</ul>
</p>
<p>
<p>The application takes advantage of two AT&T services:<br>
<ul>
<li>AT&T Global Smart Messaging Suit (GSMS) powered by Soprano</li>
<li>AT&T Location Information Services (LIS) powered by LocAid</li>
</ul>
In order to take advantage of the toolkit the accounts need to be setup for each service. If you have GSMS and LIS accounts open already, they can be re-used.
Please refer to <a href='../tools/enablers/' target='_blank'>Service Integration APIs</a> for more information.
</p>
<p>
<p>Upon installation, make sure the following steps are taken:<br>
<ul>
<li>Create a new user, e.g. ServiceDispatcherSMSReceiverUser_&lt;tenantID&gt;. It can be done under Settings-&gt;Administration-&gt;Users. Select 'My Team' as a primary team and 'Service Dispatcher SMS Receiver Role' as a role in the team.</li>
<li>Setup ServiceDispatcherSMSReceiverUser as a user to be used for unauthenticated site access. It can be done under Designer-&gt;Logic-&gt;Sites-&gt;ServiceDispatcherSMSReceiverSite. Edit the site and select the created user as 'Login As User for Unauthenticated Access'.
<li>To take advantage of the service request archiving feature, make sure to schedule com.platform.CServiceDispatcher.requests.ServiceRequestsCleanup class
 as a job to be executed at a desired time intervals. The completed and cancelled service request records will be moved from Service Requests object 
 to Service Requests Archive object. The job can be scheduled under Settings-&gt;DataManagement-&gt;JobScheduler.</li>
</ul>
</p>