#!/bin/sh
rm IdleRSC.jar

find . | grep "\.java" > sources.txt

javac -d bin -cp patched_client.jar @sources.txt

jar cfe IdleRSC.jar bot.Main bin/*

cd bin || exit
jar -cvfm ../IdleRSC.jar ../META-INF/MANIFEST.MF ./*
cd .. || exit

rm sources.txt
