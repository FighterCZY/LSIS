zc259  Zhengyu Cai

Running The Project
you can run this project by deploying the .war file in the tomcat webapps folder.
or you can just use the tomcat/manager to do the deployment.
you can use the url http://localhost:8080/p1a/Handler

Overall Design
Session ID: clientIP address + expiresTime + random(1000)
			the random number is to garantee there is no duplicate

Cookie:	SessionID + versionNumber + location + isLoggedOut
		use the "#" to split the data
		
		the versionNumber is start with one.
		each "replace" or "refresh" operation will increase the number

SessionTable:
		use the ConcurrentHashMap  key->sessionID  value->sessionObject
		this concurrentHashMap can easily handle the synchronization problem
		two thread cannot modify the HashMap at the same time

Description:
	I have three .java files
	Handler.java:
		the servlet file used to generate the HTML file
		this class receive the doGet and doPost request from the clients
		It will call Session.java to create Session Object
				call Cleanup.java to do the periodically clean-up job
	
	Session.java:
		this is the Session Object.
		has several method to deal with the Session data. Handles the sessionId, versionNumber,etc
		
	Cleanup.java:
		this is a cleanup thread which is called by the Handler.java every 10minutes
		use the timestamp in each session object to determine whether the session object should be removed.