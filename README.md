IdleRSC is a bot for OpenRSC. It uses code injection and reflection. It has it's own custom scripting API. It also has backwards compatibility with APOS and SBot. It also has built in staker bot functionality!

[![IdleRSC Demo Video](https://i.imgur.com/NMU2sbB.png)](https://www.youtube.com/watch?v=Bkp2M3Ja9qc)


## Starting IdleRSC:
1. Modify run.bat/sh.
2. Select which server you want.
3. **Optional**: if playing on a different server which allows botting, modify Cache/ip.txt and Cache/port.txt if needed.
4. Run .bat/.sh file!

## Rules:
User will NOT use this client to bot on non-botting servers. The only allowed openRSC botting servers are Coleslaw and Uranium. Use of this client to bot on Cabbage or Preservation will result in a BAN of the accounts.

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
#### Native Scripts and SBot: (Eclipse Method)
1. Open up the project in Eclipse
2. Add a new script to the `scripting` package.
3. Compile Jar with (compile_windows.bat) or (compile_linux.sh)
4. Run client with (run_windows.bat) or (run_linux.sh)
###Native Scripts and SBot: (IntelliJ Gradle Method)
1. Open up the project in IntelliJ
2. Save all script changes
3. Build project Class files with Gradle "Build Project"
4. Compile Jar with (compile_windows.bat) or (compile_linux.sh)
5. run client with (run_windows.bat) or (run_linux.sh)
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
4. Compile using Eclipse or IntelliJ Gradle (preferred) or the included compilation script (compile_windows.bat)

## Compiling IdleRSC Jar:
1. Clone the repository from https://gitlab.com/open-runescape-classic/idlersc
2. Open the project in Eclipse or IntelliJ, set up eclipse or gradle(preferred) compiler.
3. If client JAR is out of date, run patcher utility from https://gitlab.com/idlersc/idlersc_patcher (follow README in that project).
	- Only typically necessary when a server-side update adds sprites/textures/etc.
4. Test the compile process:
	a. Save all script changes
	b. "Build" project Class files with Gradle Build or Eclipse
	c. Compile Jar with (compile_windows.bat) or (compile_linux.sh)
	d. run client with (run_windows.bat) or (run_linux.sh)
If you have problems, you can create a new issue or ask for help in the OpenRSC Discord.

## Configuring Stake Switcher
Inside your run_windows.bat or run_linux.sh, add the following lines. Restart the bot. Press F5, F6, F7, F8 to validate.
```
--attack-items 123,456
--strength-items 123,456
--defence-items 123,456
--spell-id 2
```


## Contributing:
* Please submit issues, questions, bugs on Gitlab. Provide stack traces for crashes, etc.
* Please submit pull requests to Gitlab.
* Please do not use this on servers which do not allow botting. There are servers which allow botting, such as those already included UraniumCache/ or ColeslawCache/.

## Discussion:
Please join the <a href="https://discord.gg/CutQxDZ8Np">OpenRSC Discord</a> and post in the #botting-client-development channel. Other channels are not tied to botting, so please do not discuss botting in those channels.

## Donations:
IdleRSC is provided for free under GPLv3, due to passion for RSC botting.

## About:
This started as a project in April 2020 due to a distinct lack of botting clients available for RSC post-closure. After Jagex decided to nerf the blowpipe in OSRS, the original coder gained renewed interest in January 2021, and the project was resurrected and released.

The original owner is no longer part of this fork of the project. However all credit for writing the original client goes to DvorakKeys. 
Additional credit goes to various developers including but not limited to:  Damrau, Kaila, Kkoemets, and Searos 

The RSC botting scene WILL NEVER DIE! IdleRSC is the next iteration after APOS, STS, SBot, and AutoRune!
