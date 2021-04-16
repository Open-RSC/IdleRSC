del IdleRSC.jar
dir /s /B *.java > sources.txt
javac -d bin -cp patched_client.jar @sources.txt
jar cfe IdleRSC.jar bot.Main bin/*
cd bin/
jar -cvfm ../IdleRSC.jar ../META-INF/MANIFEST.MF *
cd ..
del sources.txt
pause
