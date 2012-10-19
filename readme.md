# AT&T Showcase
Leverage AT&T cloud platforms to deliver your service. 

## Project Description
This project showcases the possibilities using AT&T's Axeda and Longjump platforms. 

The project is built around the following use case :
1. An M2M device provisioned on the AT&T network and having basic GSM & GPRS/EDGE connectivity. The device continuously delivers data to a central server. The device alarms a failure, and notifies the AT&T M2M Platform. The Monitoring Application is notified of the failure and initiates an automated repair procedure.
1. Remote Diagnostics revealed that an on-site visit is necessary. AT&T M2M platform can resolve the address of the M2M device by looking it up in the installed equipment database. 
1. The Application, then determines the closest repair engineer based on the location of the device. The AMS Cross-Carrier location service will be used to locate the engineers.
1. The device of engineer ‘B’ is found to be closest to the faulty machine. A WO is created and engineer is notified.

# Getting Started
## Installation

### AT&T PAAS Configuration

#### Importing package
1. Please create an account in AT&T PAAS
1. Then login to AT&T PAAS using your credentials
1. When you login to the platform please select a **Designer** from the menu on the left
1. Choose **Global Resources / Packages** - the new tab will be opened in the middle of the window
1. Click on **Install from File** button which is on the top of the tab
1. Platform will redirect you to a form that allow you to select a file that you can upload. Please pick **attsa_1.zip** and click on **Next** button
1. On the next screen please select **System Administrator** role and then click on **Install** button
1. When the platform finish processing your test application will be ready to use it

### Axeda Configuration
You will need an account in Axeda in order to configure test devices

1. Please create an account in Axeda
1. Log in to Axeda using your credentials


#### Model and assets
At the beginning you should create a model of a device and a few test assets

1. Click on the **Configuration** link on the right side, then choose **New** -> **Model**
1. Create model (named ex. **ATTSC_Model**, add some **Model number**), click **Next**
1. Add Data Item named **isDamaged**, type **Digital**, visible. Finish
	
#### Custom objects
In the second step you should create custom objects that will be responsible for integration Axeda with Long Jump. 

1. Please select **Add** from the top menu and then **Custom Object**
1. The name of first object is **ATTAssetIsDamaged** and its type is **Data Rule**
1. Source code of this object you can find in **att-axeda-scripts/src/main/groovy/ATTAssetIsDamaged.groovy**
1. Please change your 'Long Jump' username and password in the script
1. The object requires two parameters: **serial** and **location**
1. When changes are completed, click **Finish** in order to save it
1. The second object should be called **ATTAssetIsFixed** and its type should be set to **Action**
1. Source code of this object is in **att-axeda-scripts/src/main/groovy/ATTAssetIsFixed.groovy**
1. Change your 'Long Jump' username and password in a script
1. The object requires single parameter **serial**
1. Click on **Finish** in order to save object

#### Expression rules
When test assets are ready and you have objects that interacts with Long Jump you should add rules that describes a interaction between them. We will need two expression rules: one for used for reporting that device is damaged and the other one for reporting that it was fixed.

1. Create Expression Rule: Please select from the top menu **New** -> **Expression Rule**
1. Name Expression Rule **DeviceIsDamaged**, type: **Data**
1. Click **Apply to assets** and check previously created Model
1. Check **Enabled** and **Execute each time rule evaluates to true**
1. In **If** field put **DataItem.isDamaged.value == 1**
1. In **Then** field put **ExecuteCustomObject("ATTAssetIsDamaged", Device.serial, Location.location)**
1. And click Save
1. The second expression rule should be called **DeviceIsFixed**, it's type should be set to **Data**
1. It should be applied to the same assets as in first rule,
1. In **If** field put **DataItem.isDamaged.value == 0**
1. In **Then** field put **ExecuteCustomObject("ATTAssetIsFixed", Device.serial)**
1. And click Save

## Configuration
**NOTE** These are provided here on a temporary basis. It is expected that the end-user will have their own credentials for theAxeda & longjump
###Longjump
Login URL: 	https://paas1.attplatform.com/
User: 	1347644721
Pass: 	e21fe58b26048f43bb3b7ebdbf4cc918

### Axeda:
Login URL: 	http://developer.axeda.com/
Alias: 	sentaca
Email: 	magdalena.biala@sentaca.com
Password: 	sentaca2012

## Testing
You can test integration of both systems using Assets simulator which should be a part of a test account in Axeda. You should be able to test two types of events:

* reporing alerts by broken device
* closing alerts by fixed device

### Reporting alerts
1. Open Simulator: Go to [http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html](http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html)
1. Enter your credentials
1. Choose your Model and some asset
1. Click on the tab **Location**, then locate and send
1. Click on the tab **Data** and choose **isDamaged** (this should be the only one option)
1. Set the value to 1 and click on **Send**
1. Expression Rule should fire and it shuould add a record in **Alerts** object in AT&T PAAS

### Closing alerts
1. Open Simulator: Go to [http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html](http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html)
1. Enter your credentials
1. Choose your Model and some asset
1. Click on the tab **Data** and choose **isDamaged** (this should be the only one option)
1. Set the value to 0 and click on **Send**
1. Expression Rule should fire and it should change the status of an alert related with the asset to DONE.
