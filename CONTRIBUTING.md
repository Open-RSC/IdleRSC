# Contribution Guide

Hello, thank you for your interest in contributing to this project.

In order to add code to this project using git version control, there are a number of steps to take.
This guide will focus on these concepts, if you have further questions feel free to make an issues/MR thread,
join our discord, or ask a mod.

Please join the [OpenRSC discord][discord] and post in the
`#botting-client-development` channel.

---
#### Guide for git:

1. Create a fork of the of the Idlersc project
2. Use git to pull down the master branch from your own remote gitlab repository.
3. Create a new named branch of master and use that branch to develop changes on.
   * If Idlersc-master is updated remotely while you are developing changes you will need to rebase before merging.
   * Go to your fork of Idlersc in a web browser and click the "update fork" button
   * Pull(fast-forward) down the now updated master branch to your local environment.
   * Rebase your named dev branch onto the updated local master branch
   * Push your updated branch to your remote.
4. When changes are ready to be merged, go to the main Idlersc branch and create a merge request.
   * Merge requests are under Code category on the left side bar.
   * The source is your named branch and target is Idlersc-master.

---

If you have any more questions, feel free to ask in the discord (linked above).

Thank you, have a great day!


[discord]: https://discord.gg/CutQxDZ8Np
