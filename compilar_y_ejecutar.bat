@echo off
title LogisTEC - Compilar y Ejecutar

echo ==========================================
echo     COMPILAR Y EJECUTAR LOGISTEC
echo ==========================================
echo.

REM Verifica que exista la carpeta src
if not exist src (
    echo ERROR: No se encontro la carpeta src.
    echo Ejecuta este archivo desde la raiz del proyecto LogisTEC.
    echo.
    pause
    exit /b 1
)

REM Verifica que exista Gson
if not exist lib\gson-2.10.1.jar (
    echo ERROR: No se encontro lib\gson-2.10.1.jar
    echo.
    echo Asegurate de tener esta estructura:
    echo LogisTEC\lib\gson-2.10.1.jar
    echo.
    pause
    exit /b 1
)

REM Verifica que exista el JSON
if not exist data\caso_prueba.json (
    echo ERROR: No se encontro data\caso_prueba.json
    echo.
    pause
    exit /b 1
)

REM Limpia compilacion anterior
if exist out (
    rmdir /s /q out
)

mkdir out

REM Crea lista de archivos Java
dir /s /b src\*.java > sources.txt

echo Compilando...
echo.

javac -encoding UTF-8 -cp "lib\gson-2.10.1.jar" -d out @sources.txt

if %errorlevel% neq 0 (
    echo.
    echo ==========================================
    echo      HUBO ERRORES DE COMPILACION
    echo ==========================================
    echo.
    pause
    exit /b %errorlevel%
)

echo.
echo Compilacion correcta.
echo.
echo Ejecutando...
echo.

java -cp "out;lib\gson-2.10.1.jar" Main data\caso_prueba.json

echo.
echo ==========================================
echo        PROGRAMA FINALIZADO
echo ==========================================
echo.

pause