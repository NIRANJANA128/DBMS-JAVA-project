# =====================================================================
# DATAFORGE: HR and Project Management System PowerShell Runner
# =====================================================================

$ErrorActionPreference = "Stop"

$PSScriptRoot = Split-Path -Parent -Path $MyInvocation.MyCommand.Definition
$LibDir = Join-Path $PSScriptRoot "lib"
$BinDir = Join-Path $PSScriptRoot "bin"
$SrcDir = Join-Path $PSScriptRoot "src\main\java"
$JarPath = Join-Path $LibDir "mysql-connector-j-8.3.0.jar"

if (-not (Test-Path $BinDir)) {
    New-Item -ItemType Directory -Path $BinDir | Out-Null
}

Write-Host "[DATAFORGE] Compiling Java source files..." -ForegroundColor Cyan

$JavaFiles = Get-ChildItem -Path $SrcDir -Filter "*.java" -Recurse | Select-Object -ExpandProperty FullName

& javac -cp $JarPath -d $BinDir $JavaFiles

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[DATAFORGE] Launching HR and Project Management System..." -ForegroundColor Green
$ClassPath = "$BinDir;$JarPath"
& java -cp $ClassPath com.dataforge.Main
