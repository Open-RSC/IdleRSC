#!/bin/sh
rm IdleRSC.jar
find -name "*.java" > sources.txt
javac -d bin -cp Open_RSC_Client.jar @sources.txt
find -name "*.class" > classes.txt
jar cfe IdleRSC.jar bot.Main bin/*
cd bin/
jar -cvfm ../IdleRSC.jar META-INF/MANIFEST.MF *
cd ..
rm sources.txt
rm classes.txt
