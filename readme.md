# AT&T Showcase
Leverage AT&T platforms to deliver your services. 

## Project Description
This project showcases the possibilities using AT&T's services and platforms. A brief description of the platforms and services used by this project are as follows

* **AT&T M2M Platform powered by Axeda** - An M2M cloud service
* **AT&T Platform as a Service (PaaS)** - A complete development environment to build and launch custom applications quickly
* **LocationSmart & Locaid** - Location services
* **AT&T Global Smart Messaging Suite (GSMS)** - Web-based messaging service

To best highlight the possibilities and capabilities of these services, the following scenario was defined :

1. An M2M device provisioned on the AT&T network and having basic GSM & GPRS/EDGE connectivity. 
1. The device alarms a failure, and notifies the AT&T M2M Platform powered by Axeda. 
1. The Monitoring Application is notified of the failure and initiates an automated repair procedure
1. Diagnostics reveal that an on-site visit is necessary. AT&T M2M platform powered by Axeda can resolve the address of the M2M device by looking it up in the installed equipment database
1. The Application, then determines the closest repair engineer based on the location of the device. The AMS Cross-Carrier location service will be used to locate the engineers
1. The device of engineer ‘B’ is found to be closest to the faulty machine. A Work Order is created and engineer is notified

This project showcases how this is achieved using AT&T's platforms and services.

# Getting Started
## Registration
First we need to register on the platforms listed above, the registration process is easy and straight forward.

* AT&T M2M platform powered by Axeda, Sandbox environment - https://att-sandbox.axeda.com
* AT&T PaaS - https://paas1.attplatform.com/
* LocAid - http://pte.att.com/lis
* LocationSmart - http://pte.att.com/lis
* GSMS - http://pte.att.com/GSMS

## Installation

### AT&T PAAS Configuration

#### Importing package
1. Log into AT&T PAAS using your credentials
1. When you login to the platform please select a **Designer** from the menu on the left
1. Choose **Global Resources / Packages** - the new tab will be opened in the middle of the window
1. Click on **Install from File** button which is on the top of the tab
1. Platform will redirect you to a form that allow you to select a file that you can upload. Please pick **attsa_1.zip** and click on **Next** button
1. On the next screen please select **System Administrator** role and then click on **Install** button
1. When the platform finish processing your test application will be ready to use it

### AT&T M2M Platform powered by Axeda Configuration

1. Login using your credentials

#### Model
At the beginning you should create a Device Model

1. Click on the **Configuration Tab** link on the right side, then choose **New** -> **Model**
1. Create model. The Model name and number can be any text and number which describes your model best. Click **Next**
1. Add Data Item named **isDamaged**, type **Digital**, visible. Finish

#### Assets
We now need to create an asset, on which we raise an alarm. There can be multiple assets, the steps below list how to add one asset. 

1. In the **Configuration Tab**, select **New** -> **Asset**
1. Create Asset. The Asset name, serial number can be any text or number which best describes your asset. The Model number should be the model created in Step 2 above. Click **Next**
1. Select Organization and Location. Click **Next**
1. Select Asset Group. Click ** Finish**

#### Custom objects
In the second step you should create custom objects that will be responsible for integration Axeda with Long Jump. 

1. Please select **Add** from the top menu and then **Custom Object**
1. The name of first object is **ATTAssetIsDamaged** and its type is **Action**
1. Source code of this object you can find in **att-axeda-scripts/src/main/groovy/ATTAssetIsDamaged.groovy**
1. Please change your 'Long Jump' username and password in the script
1. The object requires two parameters: **serial**, **location** and **description**
1. When changes are completed, click **Finish** in order to save it
1. The second object should be called **ATTAssetIsFixed** and its type should be set to **Action**
1. Source code of this object is in **att-axeda-scripts/src/main/groovy/ATTAssetIsFixed.groovy**
1. Change your 'Long Jump' username and password in a script
1. The object requires single parameter **serial**
1. Click on **Finish** in order to save object

#### Expression rules
When test assets are ready and you have objects that interacts with Long Jump you should add rules that describes a interaction between them. We will need two expression rules: one for used for reporting that device is damaged and the other one for reporting that it was fixed.

1. Create Expression Rule: Please select from the top menu **New** -> **Expression Rule**
1. Name Expression Rule **DeviceIsDamaged**, type: **Alarm**
1. Click **Apply to assets** and check previously created Model
1. Check **Enabled** and **Execute each time rule evaluates to true**
1. In **If** field put **true**
1. In **Then** field put **ExecuteCustomObject("ATTAssetIsDamaged", Device.serial, Location.location, name)**
1. And click Save
1. The second expression rule should be called **DeviceIsFixed**, it's type should be set to **AlarmStateChange**
1. It should be applied to the same assets as in first rule,
1. In **If** field put **state == "CLOSED"**
1. In **Then** field put **ExecuteCustomObject("ATTAssetIsFixed", Device.serial)**
1. And click Save

## Setup
###AT&T M2M Platform powered by Axeda
The configuration for AT&T M2M powered by Axeda is listed in steps 3 and 8 in the *Custom Object* setup above. This is the entry of the AT&T PaaS credentials into the two groovy scripts. 

###AT&T PaaS
Once installed, the AT&T PaaS has a number of objects which must be setup. 

#### GSMS Setup
As the name suggests, this refers to the AT&T Global Smart Messaging Suite configuration. The user specific details required here are

* Username
* Password

As listed above, these credentials can be obtained by registering at http://pte.att.com/GSMS

#### LocAid Setup
This object contains the setup for the Location service Locaid. The user specific details required are

* Class ID
* Username
* Password
* Accuracy

As listed above, these credentials can be obtained by registering at http://pte.att.com/lis

#### LocationSmart Setup
This object contains the setup for the LocationSmart Location service. The user specific details required are

* Username
* Password
* Accuracy

As listed above, these credentials can be obtained by registering at http://pte.att.com/lis

#### Location Service Setup
This object specifies which Location Service should be used. The application is designed to allow either LocAid or LocationSmart sevices can be used for Location Services. 
To select, just chose either LocAid or LocationSmart from the the drop down box.
**NOTE** This must be done at system setup time. To change after this time, all engineer location subscriptions must first be cancelled, then Location Service can be changed.

##### LocationSmart Specific Setup
For the LocationSmart service, registration of MSISDNs and IP address which you will be using may be required on the LocationSmart platform. This will depend on the LocationSmart subscription you have. 
More information on this can be found in the LocationSmart documents, available for download from the PTE site. 

###Engineers Setup
Before testing you should add test engineers to the system. You can do this, by opening Engineers view and clicking on **New Record** button. Every engineer in the system should be subscribed to one of Location Services and to GSMS. You can manage those subscriptions using buttons available in engineer's details view. In order to subscribe an engineer to all external services you should follow those steps:

* Click **Subscribe to GSMS** button,
* Engineer gets an invitation to GSMS that should be confirmed by sending a response with a single word: in
* Once the engineer confirms the subscription, he will be able to receiving messages from GSMS
* You can test the GSMS subscription using **Send invitation SMS** button

Now you can subscribe the engineer to one of location services.

* Click **Subscribe to LIS** button
* Engineer gets an invitation to LIS that should be confirmed by sending appropriate response(all the details you can find in invitation SMS)
* Once the engineer confirms the subscription, you will be able to get information about its location
* Now you can test the LIS subscription using **Get location** button

## Testing
You can test integration of both systems using an Asset simulator which should be a part of AT&T M2M Platform powered by Axeda. 
You should be able to test two types of events:

* Reporing alerts by broken device
* Closing alerts

### Reporting alerts
1. Open Simulator: Go to [https://att-sandbox.axeda.com/apps/AssetSimulator/DeviceSimulatorWeb.html](https://att-sandbox.axeda.com/apps/AssetSimulator/DeviceSimulatorWeb.html)
1. Enter your credentials
1. Choose your Model and asset
1. Click on the tab **Location**, then locate and send
1. Click on the tab **Alarm**
1. Type the name and click on **Send**
1. Expression Rule should fire and it should add a record in **Alerts** object in AT&T PAAS

### Closing alerts
1. Open Platform: Go to [https://att-sandbox.axeda.com/](https://att-sandbox.axeda.com/)
1. Enter your credentials
1. Expand **Recent Assets** from the menu and choose the device for which you reported an alert
1. Find the alert in device's details and close it
1. Expression Rule should fire and it should change the status of an alert related with the asset to DONE.
