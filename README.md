# BlueMaps-on-Tomcat
this is a simple project dedicated to being able to use the blue map project on an apache tomcat web server


installation:
1. compile the BlueMapServlet.java and place it in /WEB-INF/classes   
   to compile this class you will need to add catalina.jar and servlet-api.jar to the class path  
2. edit web.xml in /WEB-INF to inclue the maping for this new servlet
3. you done. bluemaps should work without any further configuration

note: 
1. this most likely will not work with packaged WAR files
2. your Blue Maps render must be using FILE storage and not SQL


what do?:  
This servlet will prioritize sending compressed file (.json.gz) over the non compressed version.  
Sets the appropriate http headders for when a compressed file is being sent.  
If a compressed file is not found then it will fall back on the default behavior of tomcat when serving files.  
If a json file can not be found compressed or not it will send back an empty json file. This is to prevent getting a 404 on the client.
