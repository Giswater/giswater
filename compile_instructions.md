This documentation explains how to create a project to develop, test and also be able to generate new executables (.jar file) of Giswater. 
We will recommend some tools, but of course you can achieve similar results using other ones. 

Download last version of Eclipse IDE for Java developers from this link:
http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/neon1a

Use any client GIT software to keep track of the project:
https://github.com/Giswater/giswater.git

Under Windows platfform we recommend Source Tree:
https://www.sourcetreeapp.com/

Before create a new project in Eclipse, make sure your computer has a Java Runtime Environment already installed. Although is possible to use any version > 1.6, we recommend to use 1.8.xxx
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

In Eclipse, create a new Java Project called giswater in your workspace folder. For example: 
C:\workspace\giswater

Configure Build Path:
Make sure folders 'src' and 'i18n' are set as a source folder
Make sure default output for binary files is set to: giswater/bin

In that point, you have to be able to Run the application.
In Project Explorer, search for the MainClass.java under package or.giswater.gui
Right button - Run As - Java Application
In a few seconds you will be running giswater!

If everything is fine, now you can make any changes in the source code and run the application again to test them...

If we wanto to create a new executable version we have to execute an XML file containing an Ant buildfile: create_runnable_jar_2.0.xml. This file is located in the root folder
Right button - Run As - Ant build

We can edit that file with any text editor, and make any changes. One of the parameters we have to modify for sure, is the destination folder where application files will be generated (including executable giswater.jar). That parameter is located in line number 12: jar_folder
Executable full number version consist in 3 digits following this pattern: x.y.zzz
x: Major version
y: Minor version
zzz: Build version

x and y values are defined in the propery version (line 4)
zzz values will be defined in an external file used by Ant build tool. We have to create that file will this content:
[default]
build.number=100

Every time we run this Ant file, we're generating an executable with a diferent build version number (zzz). 
Ant tool will manage and autoincrement this number in the file buid_2.0.num. We also can manually modify this file.


Once generated, we can update giswater.jar, and maybe other folders if that has been updated also, to Giswater installation folder. Default folder is:
c:\program files\giswater\x.y

For more information and documentation about Ant buildfiles:
http://help.eclipse.org/neon/index.jsp?topic=%2Forg.eclipse.platform.doc.user%2FgettingStarted%2Fqs-93_project_builder.htm


