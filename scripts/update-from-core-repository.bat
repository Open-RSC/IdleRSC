@echo off
:: Updates client/src and cache from the Core Framework Gitlab repository.
cls

:: --------------VARIABLES--------------
:: Colors for echo (These have invisible escape characters, don't delete them)
set GREEN=[32m
set YELLOW=[33m
set RED=[31m
set PURPLE=[35m
set LIGHTBLUE=[36m
set RESET=[0m

:: Project-specific variables
set ASSET_DIR=assets
set CLIENT_DIR=client

:: Repository-related variables
set CORE_REPOSITORY=https://gitlab.com/openrsc/openrsc.git
set CORE_REPOSITORY_NAME=core
set CORE_REPOSITORY_DIR=%ASSET_DIR%\%CORE_REPOSITORY_NAME%

:: Folders within the core repository
set CLIENT_BASE_DIR=%CORE_REPOSITORY_DIR%\Client_Base
set PC_CLIENT_DIR=%CORE_REPOSITORY_DIR%\PC_Client

::  ------------SCRIPT--START------------
:: Check if the script is run from the project's root directory
if not exist gradlew (
	echo %RED%This script needs to be ran from the root project directory!%RESET%
	exit 1
)

:: Check if git is installed
git --version > nul 2>&1
if %errorlevel% neq 0 (
    echo %RED%Git must be installed for this script to run!%RESET%
	exit 1
)


:run_confirmation
	cls
	echo %GREEN%THIS SCRIPT WILL DO THE FOLLOWING:%RESET%
	echo %LIGHTBLUE% - %PURPLE%Execute the '%YELLOW%gradle clean%PURPLE%' task to clean old files.%RESET%
	echo %LIGHTBLUE% - %PURPLE%Clone the Core Framework (%YELLOW%~1GB%PURPLE%) GitLab repository.%RESET%
	echo %LIGHTBLUE% - %PURPLE%Copy the needed files from the Core Framework download%RESET%
	echo %LIGHTBLUE% - %PURPLE%Delete the unnecessary files from the Core Framework download%RESET%
	echo %LIGHTBLUE% - %PURPLE%Execute the '%YELLOW%gradle build%PURPLE%' task to rebuild IdleRSC.%RESET%
	echo.

	set /p userInput=Do you want to continue? (Y/N):
	echo.

	if "%userInput%"=="" (
		goto run_confirmation
	) else (
		if /i "%userInput%"=="Y" (
			goto continue
		) else if /i "%userInput%"=="N" (
			echo %RED%Script aborted%RESET%
			echo.
			exit 1
		) else (
			goto run_confirmation
		)
	)

:continue
	:: Run the clean task
	echo %LIGHTBLUE%Running gradle clean task!%RESET%
	call gradlew.bat clean

	:: Clone the Core Framework Repo
	echo.
	echo %LIGHTBLUE%Cloning the Core Framework repository!%RESET%
	git clone "%CORE_REPOSITORY%" "%CORE_REPOSITORY_DIR%

	:: Copy the new files over
	echo.
	echo %LIGHTBLUE%Copying over the newly cloned cache and client/src%RESET%
	mkdir "%CLIENT_DIR%\src\main\java"
	mkdir "%CLIENT_DIR%\src\main\resources"
	mkdir "%ASSET_DIR%\cache"

	xcopy "%CLIENT_BASE_DIR%\Cache\audio" "%ASSET_DIR%\cache" /e /i /h /y
	xcopy "%CLIENT_BASE_DIR%\Cache\video" "%ASSET_DIR%\cache" /e /i /h /y
	xcopy "%CLIENT_BASE_DIR%\src\*" "%CLIENT_DIR%\src\main\java" /e /i /h /y
	xcopy "%PC_CLIENT_DIR%\src\*" "%CLIENT_DIR%\src\main\java" /e /i /h /y
	move "%CLIENT_DIR%\src\main\java\res" "%CLIENT_DIR%\src\main\resources"

	:: Clean up leftover files
	echo.
	echo %LIGHTBLUE%Cleaning up leftover Core Framework files to save space!%RESET%
	if exist "%CORE_REPOSITORY_DIR%" rmdir /s /q "%CORE_REPOSITORY_DIR%"
	if exist "%CLIENT_DIR%\src\main\java\res" rmdir /s /q "%CLIENT_DIR%\src\main\java\res"

	:: Run the build task
	echo.
	echo %LIGHTBLUE%Running gradle build task!%RESET%
	call gradlew.bat build

	echo.
	echo %GREEN%FINISHED!%RESET%
	echo.
pause
