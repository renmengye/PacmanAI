@ECHO OFF
set DIR=%CD%
SET JAVA_HOME="C:\Program Files\Java\jdk1.7.0_07\bin"
SET JARPATH="%CD%\pacman-java.jar"
SET SRC="%CD%\src\Player\PacPlayer.java"
SET CLASS_PATH="%CD%\build\classes"
SET CLASS="Player.PacPlayer"

%JAVA_HOME%\javac.exe -classpath %JARPATH%;%CLASS_PATH% %SRC% 2>&1
if ERRORLEVEL 1 goto :eof

%JAVA_HOME%\java.exe -jar %JARPATH% %CLASS_PATH% %CLASS% %*