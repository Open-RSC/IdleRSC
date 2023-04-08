## Intro

Scripts are made up of tasks. Tasks are the building blocks of scripts. Tasks are the smallest unit of work that a
script can do.

### 1. Creating an IdleTask

Below are some general guidelines for creating tasks.They are helpful to contribute in a way that benefits everyone,
the community and devs, by trying to keep a similar structure across scripts and creating visible, reusable
parts. Certainly task based scripts are not the only way to write scripts, but they are a good way to get started. In
the end, it's up to you how you want to write your scripts.

1.1. When creating tasks keep in mind that tasks should be reusable and independent of each other.
This means that tasks should not have any dependencies on other tasks and should not be dependent on other tasks.
A task's main purpose is to manipulate the game state in a certain way, i.e. interact with the controller.

1.2. Tasks should not know anything about the scripts they are used in. Controller should not know about the tasks it is
being
used by.

1.3. Tasks should be created in a way that they can be used in any context. This means that tasks
should not check for any conditions that are specific to a certain context.

1.4. Keep tasks abstract as possible. E.g. instead of creating a task that kills a specific NPC, create a task that
kills any killable NPC and provide the ids in the script.

1.5. Avoid while loops in tasks if possible. Certain tasks might require a while loop, but try to avoid them as much as
possible. If a task requires a while loop, make sure that it is not an infinite loop.

1.6. Sleep times should be avoided as much as possible. public int tickDelay() in tasks will help you with this.

1.7. Remember that anonymous tasks can be also be defined in the script file itself.
This is useful for tasks that are only used in one script that are unique to the situation.
