!WARNING! !WARNING! !WARNING! !WARNING! !WARNING! !WARNING!!WARNING! !WARNING! !WARNING! !WARNING! 

This is an alpha version. I am not responsible for any lost items or ruined accounts.

!WARNING! !WARNING! !WARNING! !WARNING! !WARNING! !WARNING!!WARNING! !WARNING! !WARNING! !WARNING! 

*****

IdleRSC is a reflection and packet injection bot for OpenRSC.
It has it's own custom scripting API. It also has backwards compatibility with SBot. STS and APOS support are planned.


## Starting IdleRSC:
1. Modify run.bat/sh
2. Select the proper cache file:
* If you are running OpenRSC "preservation" (i.e. original RSC menus, items, etc.) then rename the "UraniumCache" folder to "Cache".
* If you are running OpenRSC with modifications (such as Coleslaw), then rename "ColeslawCache" to "Cache".
3. Modify Cache/ip.txt and Cache/port.txt if needed.
4. Run .bat/.sh file!

## Sleeping:
IdleRSC does not have a built in sleeper. Anyone with experience dealing with making OCR programs, please see issue #25.

## Account Security:
Please be aware that run.bat is not encrypted. If someone has your run.bat, they have your username and password. 
Please be aware that you should not run any .class files from sources you do not trust. Running a rogue .class file is akin to running a .exe file on your computer. IF YOU DON'T TRUST IT, READ THE SOURCE CODE AND COMPILE IT!!!
	
## Compiling Scripts:
### Linux:
1. Put the .java file in the src/scripting/(idlescript or sbot) folder
2. Run `gradle build`

### Windows:
#### Native Scripts and SBot:
1. Open up the Eclipse project
2. Add a new script to the `scripting` package.
3. Your class file will be spit out into bin/scripting/ if successfully compiled.		
#### SBot:
Easy Method: (((NOT PREFERRED IF YOU ARE WANTING TO DEVELOP SCRIPTS))) WARNING: This will delete your JAR file!!!
1. Place your script in src/scripting/sbot/
2. Run compile_windows.bat (or compile_linux.sh)
3. Ensure no issues compiling (they will be towards the top.)
4. Re-run the bat file.
Read "converting SBot scripts" section for compilitation issues.
		
## Converting SBot Scripts:
1. Open SBot script in a text editor.
2. Place these lines at the top of the file: 
```
package scripting.sbot;
import compatibility.sbot.Script;
```
3. Remove the mudclient constructor. For example, for alch.java, you would want to remove the following lines:
```
public alch(mudclient rs)
{
	super(rs);
}
```
4. Compile using Eclipse (preferred) or the included compilation script.
	
## Compiling IdleRSC:
### Linux:
1. Clone the repository
2. Run `gradle build`
### Windows:
1. Clone the repository
2. Open the project in Eclipse
3. Right click the IdleRSC project in Package Explorer, click Properties.
4. Click Java Build Path
5. Click the Libraries tab
6. Update "Open_RSC_Client.jar" to point to the latest jar file.
7. Apply and close
8. Code away!
	
## Contributing:
* Please submit issues, questions, bugs on Gitlab. Provide stack traces for crashes, etc. 
* Please submit pull requests to Gitlab. 
* Please do not use this on servers which do not allow botting. There are servers which allow botting.
	
## About:
This started as a project in April 2020 due to a distinct lack of botting clients available for RSC post-closure.
	
This is the next step after APOS.
APOS was the next step after STS.
STS was the next step after SBot.
SBot was the next step after many more... 

Jagex can't kill the RSC botting scene, even by killing RSC. Cheers to the OGs who made this scene what it was, and here's to hoping it will come back. 
	
After Jagex decided to nerf the blowpipe in OSRS, the original coder gained renewed interest in January 2021, and the project was resurrected and released.
	
