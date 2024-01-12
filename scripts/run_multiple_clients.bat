@echo off

@title Launching Multiple Bots > NUL
@echo  [35m#############################################################
@echo  #                                                           #
@echo  #             [95mScript to Launch Multiple Bots[35m                #
@echo  #      [95mOpen (edit) this .bat file for setup directions[35m      #
@echo  #                                                           #
@echo  ############################################################# [0m
@echo  [92m

@rem   DIRECTIONS:
@rem   leave TIMEOUT /t 2 > NUL to ensure OS has time to process each run command (~2s)
@rem   TIMEOUT could probably be reduced to 1s, but could cause some issue

TIMEOUT /t 1 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 1st Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 2nd Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 3rd Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 4th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 5th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 6th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 7th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 8th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 9th Bot
TIMEOUT /t 2 > NUL
start javaw -jar IdleRSC.jar --auto-start --account "name"
echo Launched 10th Bot
TIMEOUT /t 5
