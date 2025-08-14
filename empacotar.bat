@echo off
echo Compilando arquivos Java...

REM Gera lista de todos os arquivos .java
dir /B /S src\*.java > sources.txt

REM Compila todos os arquivos juntos
javac -d out @sources.txt

echo Criando arquivo JAR...
"C:\Program Files\Java\jdk-24\bin\jar.exe" cfm sudoku.jar manifest.txt -C out .

echo Executando o jogo...
java -jar sudoku.jar

pause
