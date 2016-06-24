# MailChimpAPI
How to fetch data from mailchimp with Groovy, Ready to use plug and play APIs
#### Config.Groovy

Here we are giving settings of Mailchimp API example
1. API URL 
2. API KEY
3. Default ListID etc.

## mailchimpApi.groovy

Here You can find all API which you can use in your code to get data from Mailchimp.
Mostly we need some basic level of data only from Mailchimp.

##### Subscribe 
To Subscribe to your newsletter, This API code will be useful 
Check this function : *def subScribe(String email ,def listId)*

##### Batch Subscribe
To Subscribe list of email ids to your NewsLetter List, use this API
Check this function : *def batchSubsCribe(def emailsList,def listId)*

##### Particular User's Activity 
To Get the most recent 100 activities for particular list members (open, click, bounce, unsub, abuse, sent to, etc.)
Check this function : *def getUserActivity(def users, def listID)*

##### MailChimp Lists
To Get all the list in your API we need to call this API, which gives list of ALL Mailchimp Lists
Check this function : *def getAllMailChimpList()*

##### All Users Lists
To Get all the Users present in the particular list in your code we need to call this API, which gives list of All Users data which are in given ListID.
Check this function : *def getBunchListUsers(def listID)*

##### All Activity of All Users
To Get all activity of all the users, we can use this API, This API get list of all Active Campaigns, and iterate over it.
After iteration it gives all activity present in all campaign.
Modification can be done to get specific activites of campaign, you can use "sdf" variable to get activities from particular date.

Check this function : *def getCampaignActivity()*

##### Wrapper Function (Utility Function)
createConnection , sendParameter, getReqMap
