@echo off
javac -classpath .;../../../IdleRSC.jar *.java
move *class ../../../bin/scripting/sbot/
echo If there are no errors, compilation is complete.
echo If there are errors, check syntax and ensure you are using the latest jar.
pause
