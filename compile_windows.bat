@echo off
echo  [35m###############################################
echo  #                                             #
echo  #      [95mCompiling IdleRSC Botting Client[35m       #
echo  #                                             #
echo  ############################################### [0m

gradlew.bat build

@rem pause here will NEVER activate unless gradle build fails to process.
@rem previously cmd would close without any error codes

echo [91m ERROR: Could not Compile [0m
pause

