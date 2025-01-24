<div align="center">

![idlersc logo](doc/images/logo.png "IdleRSC Logo")

# IdleRSC
[![openrsc badge](https://img.shields.io/badge/OpenRSC_Launcher-0?style=flat&label=Play&color=045CDD)][launcher]

[![releases badge](https://gitlab.com/openrsc/idlersc/-/badges/release.svg "Latest IdleRSC Releases")][releases]
[![pipeline badge](https://gitlab.com/openrsc/idlersc/badges/master/pipeline.svg?key_text=Pipeline "Idlersc Commits")][commits]
[![discord badge](https://img.shields.io/discord/459699205674369025?logo=discord&logoColor=%23FFFFFF&label=Open%20RSC&color=%235865F2 "Open Runescape Classic Discord")][discord]

[![openrsc wiki badge](https://img.shields.io/badge/OpenRSC-0?style=flat&color=045CDD&label=Wiki "OpenRSC Wiki")][openrsc-wiki]
[![idlersc wiki badge](https://img.shields.io/badge/IdleRSC-0?style=flat&color=454545&label=Wiki "IdleRSC Wiki")][wiki]
[![idlersc api badge](https://img.shields.io/badge/IdleRSC%20API-0?style=flat&logo=gitlab&logoColor=ff8800&color=454545  "IdleRSC API")][api]

IdleRSC is a bot for the [OpenRSC][openrsc] coleslaw and uranium worlds that uses code injection and
reflection. It has its own custom scripting API called IdleScript, as well as
backwards compatibility with APOS and SBot scripts. It also provides staker
bot functionality!

![main window screenshot](doc/images/main-window-new.png "Main window")

---

### Rules
***DO NOT*** use this client to bot on non-botting servers.

The only allowed OpenRSC botting servers are Coleslaw and Uranium.

Using this client on disallowed OpenRSC servers will result in a ***BAN***.

---

### Requirements
  Download and install [Java 8][java8] for your device.

  Download the [Open Runescape Classic Launcher][launcher].

  *Note: If you're still not able to run OpenRSC.jar after installing Java 8 try running [jarfix][jarfix].*

  *You can also ask for help in the **#setup-help** or **botting-client-development** channels of the [OpenRSC discord][discord]*

---

### Account Security

Please be aware that account property files are not encrypted.

If someone has your properties file, they have access to your account.

***Don't run scripts from untrusted sources***

Running a rogue .class file is akin to running a .exe
file on your computer.

***IF YOU DO NOT TRUST IT, READ THE SOURCE CODE BEFORE
COMPILING IT!!!***

---

### Lost Items, Accounts, etc

Developers of IdleRSC and OpenRSC are not responsible for ruined, banned, hacked accounts,
or anything else. ALWAYS use unique passwords for different services. All passwords are
hashed and salted on server side for security purposes.

---
### More Info

<details><summary>Commands and Shortcuts</summary>

---
#### In-Game Commands and Shortcuts
In-Game commands are typed into the game chat box.

![help menu screenshot](doc/images/help-menu.png "Help Menu")

#### CLI Startup Commands
Command Line Interface (CLI) commands can be included following a command line startup
for example `java -jar IdleRSC.jar`, or added to the `run_windows.bat` or `run_linux.sh`
script files that execute the same startup command.
<div align="left">

```
 --auto-start - Auto start bot, bypassing Account Selection window
   Must include either:
     --account
     --username and --password
 --account <account> - Load from saved account
 --script-arguments <arguments> - pass arguments to the script (e.g. dragonstone)
 --auto-login - Enable automatic log-in with credentials (--username, --password)
 --debug - Enable debug logging
 --log-window - Display log window
 --disable-gfx - Disable graphics refresh
 --help - Show help menu (F12)
 --hide-side-panel - Hide side panel
 --init-cache <server> - Initialise cache for specified server (coleslaw|uranium)
 --interlace - Enable graphics interlacing
 --ocr-type <type> - Configure OCR sleeper (internal|remote|manual)
 --ocr-server <url> - OCR server URL for remote sleep solver
 --log-window - Display log window
 --script-name <name> - Name of the script to run
 --password <password> - Account password
 --script-selector - Display script selector window
 --username <username> - Account username
 --unstick - Unstick side panel from main window
 --version - Show version
 --attack-items <item1,item2> - stake switcher attack item swapping
 --defence-items <item1,item2> - stake switcher defence items swapping
 --strength-items <item1,item2> - stake switcher strength items swapping
 --spell-id <id> - Spell id for stake switcher casting
```
</div>

---
### Stake Switcher
The stake switcher can only be configured by use of command-line parameters.
Inside your `run_windows.bat` or `run_linux.sh` script, add the following
lines:
<div align="left">

```
--attack-items 123,456
--strength-items 123,456
--defence-items 123,456
--spell-id 2
```
</div>

e.g. `java -jar IdleRSC.jar --attack-items 123,456` and so on.

Restart the bot. Press `F5`, `F6`, `F7`, `F8` to validate.

---

</details>

<details><summary>Compiling Scripts</summary>

---

### Linux
<div align="left">

```
1. Put the .java file in the src/scripting/(idlescript or sbot) folder
2. Run `./gradlew build`
```
</div>

### Windows

#### Native Scripts and SBot: (Eclipse Method)
<div align="left">

```
1. Open up the project in Eclipse
2. Add a new script to the `scripting` package.
3. Compile Jar with (compile_windows.bat) or (compile_linux.sh)
4. Run client with (run_windows.bat) or (run_linux.sh)
```
</div>

#### Native Scripts and SBot: (IntelliJ Gradle Method)
<div align="left">

```
1. Open up the project in IntelliJ
2. Save all script changes
3. Build project Class files with Gradle "Build Project"
4. Compile Jar with (compile_windows.bat) or (compile_linux.sh)
5. run client with (run_windows.bat) or (run_linux.sh)
```
</div>

#### APOS/SBot
<div align="left">

```
Easy Method: (NOT PREFERRED IF YOU ARE WANTING TO DEVELOP SCRIPTS)
WARNING: This will delete your JAR file!!!

1. Place your script in src/scripting/apos/
2. Run compile_windows.bat (or compile_linux.sh)
3. Ensure no issues compiling (they will be towards the top.)
4. Re-run the bat file.

Read "converting SBot scripts" section for compilation issues.
```
</div>

---
</details>
<details><summary>Converting Scripts</summary>

---

### Converting APOS Scripts

APOS scripts require several changes in order to be made compatible.
Please see changes made to scripts which were added.

### Converting SBot Scripts

<div align="left">

```
1. Open SBot script in a text editor.
2. Place these lines at the top of the file:
  package scripting.sbot;
  import compatibility.sbot.Script;

3. Remove the mudclient constructor.
For example, for alch.java, you would want to remove the following lines:
  public alch(mudclient rs)
  {
    super(rs);
  }
```
</div>

Compile using Eclipse or IntelliJ Gradle (preferred) or the included compilation script (compile_windows.bat)

---
</details>



<details><summary>About IdleRSC</summary>

---
  This started as a project in April 2020 due to a distinct lack of botting
  clients available for RSC post-closure.

  After Jagex decided to nerf the
  blowpipe in OSRS, the original coder gained renewed interest in January 2021,
  and the project was resurrected and released.

  The original owner is no longer part of this fork of the project. However,
  credit for writing the original client goes to DvorakKeys.

  The RSC botting scene WILL NEVER DIE! IdleRSC is the next iteration after
  APOS, STS, SBot, and AutoRune!

---
</details>

  <details><summary>FAQ</summary>

---

  <div align="left">

    Q: Can I donate to this project?
    A: No donations nor subscriptions are accepted. We don't want any money.
      We also don't believe that dumping player money into ads will make any
      difference for long term player growth and retention.
      The best way to help the team is to help with submitting bug reports,
      submitting GitLab merge requests, and spreading the word about us to
      your friends so they will want to be a part of this too!

  </div>
</details>

---
### Discussion and Contributing

Feel free to join the **#botting-client-development** channel in the [OpenRSC discord][discord].

Other channels are not tied to botting,
so please do not discuss botting in those channels.

If you would like to contribute to IdleRSC, but don't know where to start check out the [documentation][documentation].

---
### Reporting Issues
 Please submit issues, questions, bugs on GitLab's [issue tracker][issue-tracker].

 Provide stack traces for crashes, etc.

 Please submit pull requests to [GitLab][merge-requests].

 ---

### Contributors
<a href="https://gitlab.com/openrsc/idlersc/-/graphs/master?ref_type=heads">
    <img src="https://contrib.rocks/image?repo=open-rsc/idlersc" alt="IdleRSC Contributors" title="IdleRSC Contributors" width="400"/>
</a>


</div>

[openrsc]: https://rsc.vet
[openrsc-wiki]: https://rsc.vet/wiki/index.php/Open_RuneScape_Classic_Wiki
[discord]: https://discord.gg/CutQxDZ8Np
[commits]: https://gitlab.com/openrsc/idlersc/-/commits/master
[repository]: https://gitlab.com/openrsc/idlersc
[documentation]: https://openrsc.gitlab.io/idlersc
[releases]: https://gitlab.com/openrsc/idlersc/-/releases
[launcher]: https://rsc.vet/downloads/OpenRSC.jar
[issue-tracker]: https://gitlab.com/openrsc/idlersc/-/issues
[merge-requests]: https://gitlab.com/openrsc/idlersc/-/merge_requests
[contributors]: https://gitlab.com/openrsc/idlersc/-/graphs/master?ref_type=heads
[wiki]: https://gitlab.com/openrsc/idlersc/-/wikis/home
[api]: https://openrsc.gitlab.io/idlersc/overview-summary.html
[java8]: https://adoptium.net/temurin/releases/?version=8&package=jdk
[jarfix]: https://johann.loefflmann.net/downloads/jarfix.exe
