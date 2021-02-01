#!/bin/sh
javac -classpath ".:../../../IdleRSC.jar" *.java

mv *class ../../../bin/scripting/sbot/

echo If there are no errors, compilation is complete.
echo If there are errors, check syntax and ensure you are using the latest jar.
