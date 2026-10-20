@echo off
REM =====================================================================
REM DATAFORGE: HR and Project Management System GUI Dashboard Launcher
REM =====================================================================

setlocal

set "LIB_DIR=%~dp0lib"
set "BIN_DIR=%~dp0bin"
set "CP=%BIN_DIR%;%LIB_DIR%\mysql-connector-j-8.3.0.jar"

echo [DATAFORGE] Starting Interactive GUI Dashboard...
start javaw -cp "%CP%" com.dataforge.gui.DashboardFrame

endlocal
