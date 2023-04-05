# IdleRSC

IdleRSC is a bot for [Open RSC][open-rsc]. It uses code injection and
reflection. It has it's own custom scripting API called IdleScript, as well as
backwards compatibility with APOS and SBot scripts. It also provides staker
bot functionality!

![screenshot of main window](doc/main-window.png "Main Window")

## Starting IdleRSC

Releases are automatically generated upon every commit to the GitLab
[repository][repository], the last successful build can be downloaded
[here][download].

1. Modify run.bat/sh.
1. Select which server you want.
1. **Optional**: if playing on a different server which allows botting,
   modify Cache/ip.txt and Cache/port.txt if needed.
1. Run .bat/.sh file!

## Commands

![screenshot of in-game help menu](doc/help-menu.png "Help Menu")

### In-game

* `::bothelp` - show help menu with all in-game commands & keyboard shortcuts
* `::show` - show the bot side-pane
* `::gfx` - toggle graphics rendering
* `::screenshot` - take a screenshot
* `::hidepaint`/`::showpaint` - toggle paint left-side menu
* `::toggleid` - toggle item/object/npc IDs on right-click
* `::interlace` - toggle graphics interlacing

### Shortcuts

* `F1` - toggle graphics interlacing
* `F2` - toggle left-side sub menu
* `F3` - return camera zoom to default setting
* `F4` - toggles between first & third person view
* `F5`/`F6`/`F7` - attack/defense/strength item swapping (stake switching)
* `F8` - spell id casting
* `F9` - take a screenshot
* `F10` - lock the client's camera position until `F10` is pressed or
  mouse click
* `F11` - stop the current script and load a new one
* `F12` - show help menu with all in-game commands & keyboard shortcuts

## Configuration

The client provided by Open RSC has an alternate UI that can be toggled by
opening `Cache/config.txt` and changing the number on the `Menu:` line to
0 (classic) or 1 (alternate).

### Stake Switcher

The stake switcher can only be configured by use of command-line parameters.
Inside your `run_windows.bat` or `run_linux.sh` script, add the following
lines:

```
--attack-items 123,456
--strength-items 123,456
--defence-items 123,456
--spell-id 2
```

e.g. `java -jar IdleRSC.jar --attack-items 123,456` and so on.

Restart the bot. Press `F5`, `F6`, `F7`, `F8` to validate.

## Rules

User will NOT use this client to bot on non-botting servers. The only
allowed openRSC botting servers are Coleslaw and Uranium. Use of this
client to bot on Cabbage or Preservation will result in a BAN of the
accounts.

## Sleeping

Note: The below paragraph is out of date. The sleeper server no longer
runs, and the local OCR functionality has not *yet* been tested by the
current developers.

IdleRSC uses a sleeper server which is provided free of charge. However,
if you would like, you can run the FOCR sleeper locally with --localOCR
on the command line. Get the latest copy of FOCR [here][focr].

## Account Security

Please be aware that run.bat is not encrypted. If someone has your
run.bat, they have your username and password.
Please be aware that you should not run any .class files from sources
you do not trust. Running a rogue .class file is akin to running a .exe
file on your computer. IF YOU DON'T TRUST IT, READ THE SOURCE CODE AND
COMPILE IT!!!

## Lost Items, Accounts, etc

Developers of IdleRSC are not responsible for ruined, banned, hacked accounts,
or anything else.

## Contributing

* Please submit issues, questions, bugs on GitLab's [issue tracker][issue-tracker].
* Provide stack traces for crashes, etc.
* Please submit pull requests to [GitLab][merge-requests].

## Discussion

Please join the [OpenRSC discord][discord] and post in the
`#botting-client-development` channel. Other channels are not tied to botting,
so please do not discuss botting in those channels.

## Donations

Like [Open RSC][open-rsc], donations are not accepted. IdleRSC is
provided for free under [GPLv3](LICENSE), due to passion for RSC botting.

## About

This started as a project in April 2020 due to a distinct lack of botting
clients available for RSC post-closure. After Jagex decided to nerf the
blowpipe in OSRS, the original coder gained renewed interest in January 2021,
and the project was resurrected and released.

The original owner is no longer part of this fork of the project. However all
credit for writing the original client goes to DvorakKeys.
Additional credit goes to various developers including but not limited to:
Damrau, grawlinson, JonathanB31, Kaila, Kkoemets, and Searos.

The RSC botting scene WILL NEVER DIE! IdleRSC is the next iteration after
APOS, STS, SBot, and AutoRune!

[open-rsc]: https://rsc.vet
[repository]: https://gitlab.com/open-runescape-classic/idlersc
[issue-tracker]: https://gitlab.com/open-runescape-classic/idlersc/-/issues
[merge-requests]: https://gitlab.com/open-runescape-classic/idlersc/-/merge_requests
[discord]: https://discord.gg/CutQxDZ8Np
[focr]: https://gitlab.com/open-runescape-classic/tools/focr-resurrection
[download]: https://gitlab.com/open-runescape-classic/idlersc/-/jobs/artifacts/master/browse?job=build
