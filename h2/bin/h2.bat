@java -cp "h2-2.2.220.jar;%H2DRIVERS%;%CLASSPATH%" org.h2.tools.Console %* -webAllowOthers -tcpAllowOthers
@if errorlevel 1 pause
