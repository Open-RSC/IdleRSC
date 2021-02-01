del IdleRSC.jar
dir /s /B *.java > sources.txt
javac -d bin -cp Open_RSC_Client.jar @sources.txt
jar cfe IdleRSC.jar bot.Main bin/*
cd bin/
jar -cvfm ../IdleRSC.jar META-INF/MANIFEST.MF *
cd ..
del sources.txt
pause
