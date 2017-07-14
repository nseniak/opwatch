@echo on

IF DEFINED JAVA_HOME (
  set JAVA="%JAVA_HOME%\bin\java.exe"
) ELSE (
  FOR %%I IN (java.exe) DO set JAVA="%%~$PATH:I"
)
IF NOT EXIST %JAVA% (
  ECHO Java executable not found. Please install java in your PATH or set JAVA_HOME 1>&2
  EXIT /B 1
)

set BIN_DIR=%~dp0
set LOG_DIR=%BIN_DIR%\log
set LOG_BASENAME=opwatch
IF NOT EXIST %LOG_DIR% (
  kdir %LOG_DIR%
)
%JAVA%^
	%JAVA_OPTS%^
	-Dapp.log.dir=%LOG_DIR%^
	-Dapp.log.basename=%LOG_BASENAME%^
	-Dapp.home=%BIN_DIR%\..^
	-jar %BIN_DIR%\lib\opwatch-v0.9.4.jar^
	%*
