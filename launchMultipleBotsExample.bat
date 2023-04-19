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
@rem   You can launch .vbs or .bat files with this
@rem   .vbs simply redirect to the .bat file, and run without a cmd window popup
@rem   Ensure linked files dont use space, ONLY underscores i.e. _
@rem   for Example "run_exampleAccount_1.bat" will work correctly.
@rem   Do NOT use "run exampleAccount 1.bat" with spaces, it will not work.
@rem   This counts for the actual file name as well as in this batch file

@rem   Replace (edit) each "run_exampleAccount_1.bat" within this bat file
@rem   With the name of your individual account launch files
@rem   CLI (Command Line Interface) parameters go on each account's .bat file
@rem   for example --scriptname K_Paladins --scriptarguments autostart

@rem   leave TIMEOUT /t 2 > NUL to ensure OS has time to process each run command (~2s)
@rem   TIMEOUT could probably be reduced to 1s, but could cause some issue
@rem   Below is main script of this batch file, CHANGE CONTENTS BELOW:

TIMEOUT /t 1 > NUL
run_exampleAccount_01.vbs
echo Launched 1st Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_02.vbs
echo Launched 2nd Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_03.vbs
echo Launched 3rd Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_04.vbs
echo Launched 4th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_05.vbs
echo Launched 5th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_06.bat
echo Launched 6th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_07.bat
echo Launched 7th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_08.bat
echo Launched 8th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_09.bat
echo Launched 9th Bot
TIMEOUT /t 2 > NUL
run_exampleAccount_10.bat
echo Launched 10th Bot
TIMEOUT /t 5
