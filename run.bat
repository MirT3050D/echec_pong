@echo off
echo Cleaning old classes...
for /R %%i in (*.class) do del "%%i"

echo Execution...
echo Compilation...
javac -d . -sourcepath . -cp ".;lib\*" affichage/*.java jdbc/*.java main/*.java objet/*.java save/*.java reseau/*.java 2> error.txt
if %ERRORLEVEL% neq 0 (
    echo Erreur de compilation ! Voir error.txt
    type error.txt
    pause
    exit /b %ERRORLEVEL%
)

echo Execution...
java -cp ".;lib\*" affichage.MenuPrincipal
@REM java -cp ".;lib\*" main.Main
pause
