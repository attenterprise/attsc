<!--
Licensed by AT&T under AT&T Public Source License Version 1.0.' 2012
TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION: http://developer.att.com/apsl-1.0
Copyright 2012 AT&T Intellectual Property. All rights reserved. http://pte.att.com/index.aspx
For more information contact: g15287@att.att-mail.com
-->



# AT&T Showcase
Use AT&T APIs and cloud platforms to mobilize your enterprise and solve business challenges.

## Project Description

This project showcases the possibilities of how enterprises can utilize AT&T Platforms and APIs to solve their business challenges and mobilize their workforce. A brief description of the platforms and services used by this project are as follows:

* **AT&T M2M Application Platform Powered by Axeda** - For fast M2M application development and deployment.
* **AT&T Platform as a Service (PaaS)** - A complete development environment to build and launch custom applications quickly.
* **Location Information Services (LIS)** – two cross-carrier API offerings: one powered by LocationSmart and another by LocAid.
* **AT&T Global Smart Messaging Suite (GSMS)** - enables 2-way SMS and e-mail communication and can reach employees and opted-in consumer subscribers at most wireless carriers globally.

To best highlight the possibilities and capabilities of these services, the following scenario was created:

1. An M2M device provisioned on the AT&T network and having basic GSM & GPRS/EDGE connectivity.
2. The device alarms a failure, and notifies the M2M Application Platform. 
3. The Monitoring Application is notified of the failure and initiates an automated repair procedure.
4. Diagnostics reveal that an on-site visit is necessary. The M2M platform can resolve the address of the connected device by looking it up in the installed equipment database.
5. LIS which is cross-carrier, then determines the closest repair engineer based on the location of the device. The device of engineer ‘B’ is found to be closest to the faulty machine. A work order is created and the engineer is notified.

This project showcases how this is achieved using AT&T platforms and services.

# Getting Started
## Registration

First we need to register for the platforms and APIs listed below.  The registration process is easy and straightforward.

* AT&T M2M Application Platform Powered by Axeda, Sandbox environment - https://att-sandbox.axeda.com
* AT&T Platform as a Service - https://paas1.attplatform.com/
* AT&T Location Information Services - http://pte.att.com/lis
* AT&T Global Smart Messaging Suite - http://pte.att.com/GSMS


Trials are subject to approval for qualified users. Once registered, the user will have to activate their LIS and GSMS trials. Trials have a limit of 60 days or product-specific usage threshold. Other restrictions apply. See above-listed sites for details.


## Installation

### AT&T PaaS Configuration

#### Importing package

1. Log into AT&T PaaS using your credentials.
2. From the menu on the left, select **Designer** > **Global Resources** > **Packages** - the Packages tab will open in the middle of the window.
3. Click **Install from File** button at the top of the tab.
4. Platform will redirect you to a form that allows you to select a file that you can upload. Select **attsa_1.zip** and click **Next**.
5. Select System Administrator role and click **Install**.
6. When the platform finishes processing, your test application will be ready to use it.

### AT&T M2M Application Platform Powered by Axeda Configuration

Login using your credentials

#### Model
At the beginning you should create a Device Model

1. Click on the **Configuration** Tab link on the right side, then choose **New** > **Model**.
2. Model Definition. Model number: can be any text/number which describes your model best. Default asset group name will prepopulate with entered Model number. Model type: Standalone Model. Click **Next**.
3. Model Data Items. Name: isDamaged. Type: Digital. Attributes: Visible. Click **Next**.
4. Model Properties. Leave as is. Click **Next**.
5. Missing Assets. Lease as is. Click **Next**.
6. Auto Register Assets. Leave as is. Click **Next**.
7. Confirmation. Click **Finish**.

#### Assets
We now need to create an asset, on which we raise an alarm. There can be multiple assets. The steps below list how to add one asset. 

1. In the **Configuration** Tab, select **New** > **Asset**.
2. Asset Definition. The Asset name and serial number can be any text or number which best describes your asset; however both need to be the same. From the Model Number dropdown, select the model previously created in Step 2 above ‘Create Model’. Click **Next**.
3. Asset Organization. Select Organization. Select Location. Click Next.
4. Asset Groups. Select Asset Group. Click **Next**.
5. Asset Contacts. Leave as is. Click **Next**.
6. Asset Properties. Leave as is. Click **Next**.
7. Confirmation. Click **Finish**.

To add additional assets, repeat the above steps.

#### Custom objects
In the second step create two custom objects that will be responsible for integration of M2M Platform with PaaS.

1. Select **New** from the top menu and then **Custom Object**.
2. The first object - Create Custom Object. Name: ATTAssetIsDamaged. Type: Action Rule.
3. Source Code: Copy and paste source code from att-axeda-scripts/src/main/groovy/ATTAssetIsDamaged.groovy
4. Change your PaaS username and password within the script source code.
5. Parameters: Click **Configure parameters**. This object requires a single parameter: Variable Name: location, Display Name: Device location. Click **Save Changes**. Click **Finish**.
6. The second object - Create Custom Object. Name: ATTAssetIsFixed. Type: Action Rule.
7. Source Code: Copy and paste source code from att-axeda-scripts/src/main/groovy/ATTAssetIsFixed.groovy
8. Change your PaaS username and password within the script source code.
9. Parameters: This object does not require any parameters. Click Save Changes. Click Finish.

#### Expression rules
When test assets are ready and you have objects that interact with PaaS you should add rules to describe the interaction between them. We will need two expression rules: one used for reporting damaged device and the other for reporting that it was fixed.

1. First expression rule - From the top menu select **New** > **Expression Rule**.
2. Configure the expression rule. Name: DeviceIsDamaged. Type: Alarm.
3. Enabled: check. Execute each time rule evaluates to true: check.
4. If: true
5. Then: ExecuteCustomObject("ATTAssetIsDamaged", Location.location, name)
6. Click **Apply to asset**. Search for previously created Model, select model once found, click **Add** to add to All assets of these models field. Click **OK**.
7. Click **Save**.
8. Second expression rule - select **New** > **Expression Rule**. 
9. Configure the expression rule. Name: DeviceIsFixed, Type: AlarmStateChange.
10. Click **Apply to asset**. Apply to the same assets as the first rule.
11. If: state == "CLOSED"
12. Then: ExecuteCustomObject("ATTAssetIsFixed")
13. Click **Save**.

## Setup
### AT&T M2M Application Platform Powered by Axeda
The configuration for the M2M Platform is listed in steps 3 and 8 in the Custom Object setup above. This is the entry of the PaaS credentials into the two groovy scripts.

### AT&T PaaS
Once installed, PaaS has a number of objects which must be setup.

#### GSMS Setup
This refers to the AT&T Global Smart Messaging Suite configuration. The user specific details required are:

* Username
* Password

As listed above, these credentials can be obtained by registering at http://pte.att.com/GSMS. Trials are subject to approval for qualified users. Once registered, the user will have to activate their GSMS trial. Trials have a limit of 60 days or 1000 messages. Other restrictions apply. See above-listed site for details.

#### Location Information Service Setup
This object specifies which Location Information Service (LIS) should be used. The application is designed to allow either LocAid or LocationSmart services to be used for LIS. To select, chose either LocAid or LocationSmart from the dropdown box. 

NOTE: This must be done at system setup time. To change after this time, all engineer location subscriptions must first be cancelled then LIS Service can be changed.

#### AT&T Location Information Services for LocAid Setup
This object contains the setup for the Location service LocAid. The user specific details required are

* Class ID
* Username
* Password
* Accuracy

#### AT&T Location Information Services for LocationSmart Setup
This object contains the setup for the LocationSmart Location service. The user specific details required are

* Username
* Password
* Accuracy

The credentials for LIS can be obtained by registering at http://pte.att.com/lis. Trials are subject to approval for qualified users. Once registered, the user will have to activate their LIS and GSMS trials. Trials have a limit of 60 days or product-specific usage threshold. Other restrictions apply. See above-listed site for details. 

#### AT&T Location Information Services for LocationSmart Specific Setup
For the LocationSmart service, registration of MSISDNs and IP address which you will be using may be required on the LocationSmart platform. This will depend on the LocationSmart subscription you have. 

Additional documentation can be found on Location Information Services on the PTE site (http://pte.att.com). To get access to this documentation, register for PTE site and request a LIS trial. Trials are subject to approval for qualified users. Other restrictions apply. See above-listed site or details.  


### Engineers Setup
Before testing you should add test engineers to the system. You can do this, by opening Engineers view and clicking on New Record button. Every engineer in the system should be subscribed to one of LIS choices and to GSMS. You can manage those subscriptions using buttons available in engineer's details view. In order to subscribe an engineer to all external services follow these steps:

* Click **Subscribe to GSMS** button.
* Engineer gets an invitation to GSMS that should be confirmed by sending a response with a single word: ‘IN’.
* Once the engineer confirms the subscription, the engineer will be able to receive messages from GSMS.
* The engineer’s GSMS subscription can be tested by using Send Test SMS button.

Now you can subscribe the engineer to one of the LIS choices.

* Click **Subscribe to LIS** button.
* Engineer gets an invitation to LIS that should be confirmed by sending appropriate response: LocAid reply YES 58B62, LocationSmart reply YES.
* Once the engineer confirms the subscription, you will be able to receive information about their location.
* The engineer’s LIS subscription can be tested by using **Get location** button.

Note : AT&T Location Information Services includes controls to manage privacy-related actions such as user opt-in, opt-out and communication preferences. 

### Testing
You can test integration of both systems using an Asset simulator which should be a part of AT&T M2M Application Platform Powered by Axeda. 

Two types of events can be tested:

* Reporting alerts by a broken device.
* Closing alerts by a fixed device.


### Reporting alerts
1. Open Simulator at https://att-sandbox.axeda.com/apps/AssetSimulator/DeviceSimulatorWeb.html
2. Enter your credentials.
3. Choose Model and Asset name from dropdowns.
4. Click **Location tab**. Type an Address. Click **Locate**. Click **Send**.
5. Click **Alarm** tab. Type the name of the alarm. Click **Send**. If completed correctly, a timestamp will appear next to the **Send** button.
6. Expression Rule should fire and it should add a record in Alerts object in AT&T PAAS.


### Closing alerts
1. Open platform at https://att-sandbox.axeda.com/. 
2. Enter your credentials.
3. Expand **Recent Assets** from the top left menu. Choose the device for which you reported the alert.
4. Within the device details under the **Alarms** section, locate and close the alert.
5. Expression Rule should fire and it should change the status of the alert to DONE.

