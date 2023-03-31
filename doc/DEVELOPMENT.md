# Development

## Compiling Scripts

### Linux

1. Put the .java file in the src/scripting/(idlescript or sbot) folder
1. Run `./gradlew build`

### Windows

#### Native Scripts and SBot: (Eclipse Method)

1. Open up the project in Eclipse
1. Add a new script to the `scripting` package.
1. Compile Jar with `compile_windows.bat` or `compile_linux.sh`
1. Run client with `run_windows.bat` or `run_linux.sh`

#### Native Scripts and SBot: (IntelliJ Gradle Method)

1. Open up the project in IntelliJ
1. Save all script changes
1. Build project Class files with Gradle "Build Project"
1. Compile Jar with `compile_windows.bat` or `compile_linux.sh`
1. run client with `run_windows.bat` or `run_linux.sh`

#### APOS/SBot

Easy Method: (NOT PREFERRED IF YOU ARE WANTING TO DEVELOP SCRIPTS)
WARNING: This will delete your JAR file!!!

1. Place your script in src/scripting/apos/
1. Run `compile_windos.bat` or `compile_linux.sh`
1. Ensure no issues compiling (they will be towards the top.)
1. Re-run the bat file.

Read "converting SBot scripts" section for compilitation issues.

## Converting APOS Scripts

APOS scripts require several changes in order to be made compatible.
Please see changes made to scripts which were added.
I will publish a basic script conversion tutorial at some point.

## Converting SBot Scripts

1. Open SBot script in a text editor.
1. Place these lines at the top of the file:

```
package scripting.sbot;
import compatibility.sbot.Script;
```

1. Remove the mudclient constructor. For example, for alch.java,
   you would want to remove the following lines:

```
public alch(mudclient rs)
{
  super(rs);
}
```

1. Compile using Eclipse or IntelliJ Gradle (preferred) or the included
   compilation script `compile_windows.bat`

## Compiling IdleRSC.jar

1. Clone the repository from [GitLab](https://gitlab.com/open-runescape-classic/idlersc).
1. Open the project in Eclipse or IntelliJ, set up eclipse or
   gradle (preferred) compiler.
1. If client JAR is out of date, run patcher utility from the Gitlab
   [repository](https://gitlab.com/open-runescape-classic/tools/idlersc_patcher)
   (follow README in that project).
  - Only typically necessary when a server-side update adds sprites/textures/etc.
1. Test the compile process:
  a. Save all script changes
  b. "Build" project Class files with Gradle Build or Eclipse
  c. Compile Jar with `compile_windows.bat` or `compile_linux.sh`
  d. run client with `run_windows.bat` or `run_linux.sh`

If you have problems, you can create a new issue or ask for help in the OpenRSC Discord.
