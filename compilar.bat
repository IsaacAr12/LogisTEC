@echo off
echo ================================
echo Compilando LogisTEC...
echo ================================

if exist out (
    rmdir /s /q out
)

mkdir out

dir /s /b src\*.java > sources.txt

javac -cp "lib\gson-2.10.1.jar" -d out @sources.txt

if %errorlevel% neq 0 (
    echo.
    echo Hubo errores de compilacion.
    pause
    exit /b %errorlevel%
)

echo.
echo Compilacion completada correctamente.
pause