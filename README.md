# IdleRSC

IdleRSC is a bot for OpenRSC. It uses code injection and reflection. It has
it's own custom scripting API. It also has backwards compatibility with APOS
and SBot. It also has built in staker bot functionality!

![screenshot of main window](doc/main-window.png "Main Window")

## Starting IdleRSC

Releases are automatically regenerated upon every commit to the Gitlab
repository, the last successful build can be found
[here](https://gitlab.com/open-runescape-classic/idlersc/-/jobs/artifacts/master/browse?job=build).

1. Modify run.bat/sh.
1. Select which server you want.
1. **Optional**: if playing on a different server which allows botting,
   modify Cache/ip.txt and Cache/port.txt if needed.
1. Run .bat/.sh file!

## Rules

User will NOT use this client to bot on non-botting servers. The only
allowed openRSC botting servers are Coleslaw and Uranium. Use of this
client to bot on Cabbage or Preservation will result in a BAN of the
accounts.

## Sleeping

IdleRSC uses a sleeper server which is provided free of charge. However,
if you would like, you can run the FOCR sleeper locally with --localOCR
on the command line. Get the latest copy of FOCR here:
[FOCR Resurrection](https://gitlab.com/open-runescape-classic/tools/focr-resurrection).

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

## Configuring Stake Switcher

Inside your `run_windows.bat` or `run_linux.sh`, add the following lines.
Restart the bot. Press `F5`, `F6`, `F7`, `F8` to validate.

```
--attack-items 123,456
--strength-items 123,456
--defence-items 123,456
--spell-id 2
```

## Contributing

* Please submit issues, questions, bugs on Gitlab.
* Provide stack traces for crashes, etc.
* Please submit pull requests to Gitlab.

## Discussion

Please join the [OpenRSC discord](https://discord.gg/CutQxDZ8Np) and post
in the `#botting-client-development` channel. Other channels are not tied
to botting, so please do not discuss botting in those channels.

## Donations

Like [OpenRSC](https://rsc.vet), donations are not accepted. IdleRSC is
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
