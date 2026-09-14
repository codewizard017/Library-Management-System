@echo off
title Library Management System 2026
echo Starting Library Management System 2026...
java -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" Main
java --enable-native-access=ALL-UNNAMED -cp ".;flatlaf-3.5.4.jar;jbcrypt-0.4.jar;ojdbc17-23.26.3.0.0.jar" Main
pause

