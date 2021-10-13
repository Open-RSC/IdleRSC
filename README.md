IdleRSC is a bot for OpenRSC. It uses code injection and reflection. It has it's own custom scripting API. It also has backwards compatibility with APOS and SBot.

[![IdleRSC Demo Video](https://i.imgur.com/NMU2sbB.png)](https://www.youtube.com/watch?v=Bkp2M3Ja9qc)


## Starting IdleRSC:
1. Modify run.bat/sh.
2. Select which server you want.
3. **Optional**: if playing on a different server which allows botting, modify Cache/ip.txt and Cache/port.txt if needed.
4. Run .bat/.sh file!

## Sleeping:
IdleRSC uses a sleeper server which is provided free of charge. However, if you would like, you can run the FOCR sleeper locally with --localOCR on the command line. Get the latest copy of FOCR here: [FOCR Resurrection](https://gitlab.com/idlersc/focr-resurrection). 

## Account Security:
Please be aware that run.bat is not encrypted. If someone has your run.bat, they have your username and password. 
Please be aware that you should not run any .class files from sources you do not trust. Running a rogue .class file is akin to running a .exe file on your computer. IF YOU DON'T TRUST IT, READ THE SOURCE CODE AND COMPILE IT!!!

## Lost Items, Accounts, etc:
Developers of IdleRSC are not responsible for ruined, banned, hacked accounts, or anything else. 
	
## Compiling Scripts:
### Linux:
1. Put the .java file in the src/scripting/(idlescript or sbot) folder
2. Run `gradle build`

### Windows:
#### Native Scripts and SBot:
1. Open up the Eclipse project
2. Add a new script to the `scripting` package.
3. Your class file will be spit out into bin/scripting/ if successfully compiled.		
#### APOS/SBot:
Easy Method: (NOT PREFERRED IF YOU ARE WANTING TO DEVELOP SCRIPTS) 
WARNING: This will delete your JAR file!!!
1. Place your script in src/scripting/apos/
2. Run compile_windows.bat (or compile_linux.sh)
3. Ensure no issues compiling (they will be towards the top.)
4. Re-run the bat file.
Read "converting SBot scripts" section for compilitation issues.

## Converting APOS Scripts:
APOS scripts require several changes in order to be made compatible. Please see changes made to scripts which were added. I will publish a basic script conversion tutorial at some point. 

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
1. Clone the repository
2. Open the project in Eclipse or IntelliJ
3. If client JAR is out of date, run patcher utility from https://gitlab.com/idlersc/idlersc_patcher (follow README in that project).
4. Test a compilation, then code away! If you have problems, you can create a new issue or ask for help in the OpenRSC Discord.
	
## Contributing:
* Please submit issues, questions, bugs on Gitlab. Provide stack traces for crashes, etc. 
* Please submit pull requests to Gitlab. 
* Please do not use this on servers which do not allow botting. There are servers which allow botting, such as those already included UraniumCache/ or ColeslawCache/.

## Discussion: 
Please join the <a href="https://discord.gg/CutQxDZ8Np">OpenRSC Discord</a> and post in the #botting-client-development channel. Other channels are not tied to botting, so please do not discuss botting in those channels.

New Discord Username: DvorakKeys#3329

## Donations:
IdleRSC is provided for free under GPLv3, due to passion for RSC botting. If you wish, you may donate via Monero to the following address: 83XpZtrvQTdK6aW5fp3DgtBmtemqpQYf97nPtqf7yPs9VtFa4p71NYyfCmcCe2D7kcEDUvtyc58Bi7xhp772v1ah8dax4CV

## About:
This started as a project in April 2020 due to a distinct lack of botting clients available for RSC post-closure. After Jagex decided to nerf the blowpipe in OSRS, the original coder gained renewed interest in January 2021, and the project was resurrected and released.

The RSC botting scene WILL NEVER DIE! IdleRSC is the next iteration after APOS, STS, SBot, and AutoRune!
