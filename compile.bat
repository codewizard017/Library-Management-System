@echo off
title Compiling Library Management System 2026...
echo Compiling all Java files...
javac -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" *.java
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ========================================
    echo Compilation Successful!
    echo Run the app using run.bat or:
    echo java -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" Main
    echo ========================================
) else (
    echo.
    echo Compilation Failed! Please check errors above.
)
pause

