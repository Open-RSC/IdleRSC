Set WshShell = CreateObject("WScript.Shell") 
WshShell.Run chr(34) & "run_exampleAccount_01.vbs" & Chr(34), 0
Set WshShell = Nothing