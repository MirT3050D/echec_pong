@echo off
echo Cleaning old classes...
for /R %%i in (*.class) do del "%%i"

echo Compilation...
javac -d . -sourcepath . affichage/*.java jdbc/*.java main/*.java objet/*.java save/*.java 2> error.txt
if %ERRORLEVEL% neq 0 (
    echo Erreur de compilation ! Voir error.txt
    type error.txt
    pause
    exit /b %ERRORLEVEL%
)

echo Execution...
java -cp ".;lib\postgresql-42.7.8.jar" main.Main
pause
