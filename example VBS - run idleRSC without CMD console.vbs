Set WshShell = CreateObject("WScript.Shell") 
WshShell.Run chr(34) & "run_example.bat" & Chr(34), 0
Set WshShell = Nothing