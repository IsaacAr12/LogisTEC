@echo off
title LogisTEC - Ejecutar

echo ==========================================
echo        EJECUTANDO PROYECTO LOGISTEC
echo ==========================================
echo.

REM Verifica que exista la compilacion
if not exist out (
    echo ERROR: No existe la carpeta out.
    echo Primero ejecuta compilar.bat
    echo.
    pause
    exit /b 1
)

REM Verifica que exista Gson
if not exist lib\gson-2.10.1.jar (
    echo ERROR: No se encontro lib\gson-2.10.1.jar
    echo.
    pause
    exit /b 1
)

REM Verifica que exista el caso de prueba
if not exist data\caso_prueba.json (
    echo ERROR: No se encontro data\caso_prueba.json
    echo.
    pause
    exit /b 1
)

java -cp "out;lib\gson-2.10.1.jar" Main data\caso_prueba.json

echo.
echo ==========================================
echo        EJECUCION FINALIZADA
echo ==========================================
echo.

pause