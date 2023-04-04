# Assets

This folder contains various assets that require some decoupling from the
main source.

## Core

This doesn't actually appear unless `make update-core` is invoked in the
root of the repository. All it does is ensures that the Open RSC framework
is made available for scripting/developer convenience.

## Cache

This contains all the models, sprites, landscapes, sounds required for the
Open RSC client. The cache can be refreshed when `make update-cache` is
invoked in the root of the repository.

## Map

This can be considered the "walkability" of the entire Runescape game, and
features heavily in all of the path walker algorithms present in the bot
codebase.

In order to view the data, run `hexdump -C data`.
