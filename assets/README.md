# Assets

This folder contains various assets that require some decoupling from the
main source.

## Core

This doesn't actually appear unless `make update-core` is invoked in the
root of the repository. All it does is ensure that the Open RSC [framework][0]
is made available for scripting/developer convenience.

## Cache

This contains all the models, sprites, landscapes, sounds required for the
Open RSC client. The cache can be refreshed when `make update-cache` is
invoked in the root of the repository.

It is found [here][1] in the core repository.

## Map

This can be considered the "walkability" of the entire Runescape game, and
features heavily in all of the path walker algorithms present in the bot
codebase.

In order to view the data, run `hexdump -C data`. A small snippet is shown
below:

```shell
002e7650  00 00 00 00 00 00 00 00  00 00 00 00 00 00 01 01  |................|
002e7660  01 01 01 01 00 01 00 01  01 01 01 01 01 01 01 01  |................|
002e7670  00 01 00 01 01 01 00 01  01 01 01 01 00 01 01 01  |................|
002e7680  01 00 01 01 00 00 00 01  01 01 01 01 01 01 01 00  |................|
002e7690  01 01 00 01 01 01 01 00  01 01 00 01 00 00 01 01  |................|
002e76a0  00 00 00 00 00 01 01 00  00 01 00 01 01 01 01 00  |................|
002e76b0  00 01 01 00 00 01 01 01  01 00 01 01 01 01 00 00  |................|
002e76c0  00 00 00 00 00 00 00 00  00 00 00 00 00 00 00 00  |................|
```

[0]: https://gitlab.com/open-runescape-classic/core
[1]: https://gitlab.com/open-runescape-classic/core/-/tree/develop/Client_Base/Cache
