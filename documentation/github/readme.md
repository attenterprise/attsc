# Installation manual

## AT&T PAAS Configuration

1. Please create an account in AT&T PAAS,
2. Then login to AT&T PAAS using your credentials,
3. When you login to the platform please select a **Designer** from the menu on the left,
4. And choose **Global Resources / Packages** - the new tab will be opened in the middle of the window,
5. Click on **Install from File** button which is on the top of the tab,
6. Platform will redirect you to a form that allow you to select a file that you can upload. Please pick **attsa_1.zip** and click on **Next** button,
7. On the next screen please select **System Administrator** role and then click on **Install** button,
8. When the platform finish processing your test application will be ready to use it.

## Axeda Configuration

1. Please create an account in Axeda
2. Then log in to Axeda using your credentials,
3. Click on the **Configuration** link on the right side, then choose New -> Model
4. Create model (named ex. **ATTSC_Model**, add some **Model number**), click **Next**
5. Add Data Item named **isDamaged**, type **Digital**, visible. Finish.
6. Add new Custom Object: New -> Custom Object
7. Name object ex. ATTRest, type **Action**.
8. Paste code from groovy script to area **Source code**
9. Configure parameters: **serial**, **location**
10. Finish
11. Create Expression Rule: New -> Expression Rule
12. Name Expression Rule **DeviceIsDamaged**, type: **Data**
13. Click **Apply to assets** and check previously created Model
14. Check **Enabled** and **Execute each time rule evaluates to true**
15. In **If** field put **DataItem.isDamaged.value == 1**
16. In **Then** field put **ExecuteCustomObject("ATTRest", Device.serial,Location.location)**
17. Save
18. Simulator: Go to [http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html](http://dev6.axeda.com/apps/AssetSimulator/AssetSimulator.html)
19. Enter your credentials
20. Choose your Model and some asset
21. Click on the tab **Location**, then locate and send
22. Click on the tab **Data** and choose **isDamaged** (this should be the only one choose)
23. Choose **Boolean**, enter value 1 and **Send**
24. Expression Rule should fire and add  record in **Alerts** object in AT&T PAAS 
