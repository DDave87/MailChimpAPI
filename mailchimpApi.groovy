import grails.converters.JSON
import grails.transaction.Transactional
import groovy.json.JsonBuilder
import groovy.json.JsonException

@Transactional
class MailChimp {
    def config;

    //Pass email and List ID of MailChimp Where you want to subscribe emails
    def subScribe(String email ,def listId){
        def url = config.mailchimp.apiUrl+"lists/subscribe.json/";
        String result;
        String response = null;
        def connection;
        HashMap<String,String>body = getReqMap(listId)

        body.put("email[email]",email);
        connection = createConnection(url)
        connection = sendParameter(connection,body)

        def errorCode = connection.getHeaderField("X-MailChimp-API-Error-Code")
        println("ErrorCode returned by mailchimp${errorCode}");

        if(errorCode != null && errorCode == "214"){
            println "user already exist";
            result =  "userExist"
        }else if(errorCode == null){
            println "user added to the list";
            result = "newUser"
        }else{
            result =  "Success"
        }
        return result ;
    }

    //to add batch of emails in mailchimp list
    //Passing Parameters : List(array) of Valid Eamil ids & ListId
    def batchSubsCribe(def emailsList,def listId){

        def url = config.mailchimp.apiUrl+"lists/batch-subscribe.json/";
        HashMap<String,String>body = getReqMap(listId)
        ArrayList<String>users = new ArrayList<String>();
        String response = '';
        def connection;

        emailsList?.each{ item ->
            users.add("email":["email" :item ])
        }

        body.put("batch",users);
        body.put("update_existing",true);
        body.put("replace_interests",true);
        connection = createConnection(url)
        connection = sendParameter(connection,body)

        if(connection.responseCode != 500 ){
            JSONObject responseJson = new JSONObject();
            try{
                responseJson = JSON.parse(connection?.content?.text)
                response = " Added: " + responseJson.add_count + " Error:" + responseJson.error_count + " Updated:" + responseJson.update_count
            }catch (JSONException ex){
                println("Not able to Parse response Json ")
                response = "JSON PARSE Exception"
            }
        }
        return response;
    }

    //Call this method with list of Max:50 email & List ID,
    //Return : JSON of most recent 100 activities for particular list members (open, click, bounce, unsub, abuse, sent to, etc.)
    def getUserActivity(def users, def listID){
        def url = grailsApplication.config.mailchimp.apiUrl+"lists/member-activity.json/";
        def success_count;
        def error_count;
        def error;
        def username; // single email id
        JSONObject responseJson = new JSONObject();
        HashMap<String,String>body = getReqMap(listID) ;
        body.put("emails",users);
        body.put
        def connection = createConnection(url)
        connection = sendParameter(connection,body)

        if(connection.responseCode != 500 ){
            responseJson = JSON.parse(connection.content.text);
            /* To Iterate responseJson; */

            /*
            //success And Error count
            success_count = responseJson.success_count;
            error_count = responseJson.error_count;

            //Check Errors
            responseJson.errors.each{ iterror->
                username = iterror.email.email;
                error = iterror.error;
            }

            //Check Activity
            responseJson.data.each{ itdata ->
                username = itdata.email.email

                itdata.activity.each{ itact ->
                } //each Activity ended
            }//each Data ended
            */
            return responseJson;
        }else{
            println ("____________ 500 Error in getUserActivity _____________")
            return false;
        }
    }
    def getAllMailChimpList(){
        def url = grailsApplication.config.mailchimp.apiUrl+"lists/list.json"
        def apiKey = grailsApplication.config.mailchimp.apiKey;
        def connection = createConnection(url)
        connection = sendParameter(connection,["apikey":apiKey])

        if(connection.responseCode != 500 ){
            /*We can create List of MailChimpList and return them,
            But here sending JSON response
            */
            return JSON.parse(connection.content.text)
        }else{
            println "________ 500 getAllMailChimpList _______"+apiKey
            return false
        }
    }
    
    //To get All activity of User under specific List of Mail Chimp
    def getBunchListUsers(def listID){
        def url = "https://us9.api.mailchimp.com/export/1.0/list/";
        def replyList=[];
        def eachUser;
        def username;
        def FNAME;
        def LNAME;
        def todayDate = new Date();
        def sdf;
        HashMap<String,String>body = getReqMap(listID);

	    //if you are looking to get data from specific date use below line 
	    body.put('since',sdf);

        body.put

        def connection = createConnection(url)
        connection = sendParameter(connection,body)
        int i=0
        if(connection.responseCode != 500 ){

            replyList = connection.content.text.split("\n");
 
            for(i=1;i<replyList.size();i++){
                /*
		    ["Email Address","FirstName","LastName","MEMBER_RATING","OPTIN_TIME","OPTIN_IP","CONFIRM_TIME","CONFIRM_IP",
		    "LATITUDE","LONGITUDE","GMTOFF","DSTOFF","TIMEZONE","CC","REGION","LAST_CHANGED","LEID","EUID","NOTES"]
		    */
                eachUser = replyList[i].trim().replaceAll('\\[', '').replaceAll('\\]','').replaceAll('["]','').split(',');
		        //cleaning string response.
		        username = eachUser[0];
                FNAME = eachUser[1];
                LNAME = eachUser[2];
		        //process all Details as per your requirement
            }

            //JSONObject responseJson = new JSONObject();
            //responseJson = JSON.parse(connection.content.text);
            //you can return data or save to DB
            return true
        }
	else{ 
	        println "____________ 500 Error getBunchListUsers _____________" + listID
            return false
        }
    }

    
    //Wrappers for list-ID, APIKEY
    def createConnection(def url){
        URLConnection connection = new URL(url+"?").openConnection()
        connection.setRequestMethod("POST")
        connection.doOutput = true
        return connection;
    }

    def sendParameter(URLConnection connection,def paramMap){
        def writer = new OutputStreamWriter(connection.outputStream)
        writeBody( writer,paramMap )
        writer.flush()
        writer.close()
        connection.connect()
        return connection;
    }
    def getReqMap(def listId){
        def reqMap = [:]
        //or specify your own API key in String
        reqMap["apikey"] = config.mailchimp.apiKey
        if(listId == null || listId.size() == 0) {
            reqMap["id"] = config.mailchimp.defaultListId
        }else{
            reqMap["id"] = listId
        }
        return reqMap
    }


}
