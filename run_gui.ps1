# =====================================================================
# DATAFORGE: HR and Project Management System GUI Dashboard Launcher
# =====================================================================

$PSScriptRoot = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
$LibDir = Join-Path $PSScriptRoot "lib"
$BinDir = Join-Path $PSScriptRoot "bin"
$JarPath = Join-Path $LibDir "mysql-connector-j-8.3.0.jar"
$ClassPath = "$BinDir;$JarPath"

Write-Host "[DATAFORGE] Launching Graphical Dashboard..." -ForegroundColor Green
Start-Process javaw -ArgumentList "-cp", "`"$ClassPath`"", "com.dataforge.gui.DashboardFrame"
