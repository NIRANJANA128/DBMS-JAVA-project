@echo off
REM =====================================================================
REM DATAFORGE: HR and Project Management System Runner
REM =====================================================================

setlocal

set "LIB_DIR=%~dp0lib"
set "BIN_DIR=%~dp0bin"
set "SRC_DIR=%~dp0src\main\java"
set "CP=%BIN_DIR%;%LIB_DIR%\mysql-connector-j-8.3.0.jar"

if not exist "%BIN_DIR%" mkdir "%BIN_DIR%"

echo [DATAFORGE] Compiling Java source files...
javac -cp "%LIB_DIR%\mysql-connector-j-8.3.0.jar" -d "%BIN_DIR%" "%SRC_DIR%\com\dataforge\config\*.java" "%SRC_DIR%\com\dataforge\model\*.java" "%SRC_DIR%\com\dataforge\service\*.java" "%SRC_DIR%\com\dataforge\util\*.java" "%SRC_DIR%\com\dataforge\Main.java"

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    pause
    exit /b %ERRORLEVEL%
)

echo [DATAFORGE] Starting Application...
java -cp "%CP%" com.dataforge.Main

endlocal
