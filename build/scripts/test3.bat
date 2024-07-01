@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      https://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@if "%DEBUG%"=="" @echo off
@rem ##########################################################################
@rem
@rem  test3 startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%"=="" set DIRNAME=.
@rem This is normally unused
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Resolve any "." and ".." in APP_HOME to make it shorter.
for %%i in ("%APP_HOME%") do set APP_HOME=%%~fi

@rem Add default JVM options here. You can also use JAVA_OPTS and TEST3_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if %ERRORLEVEL% equ 0 goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH. 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto execute

echo. 1>&2
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME% 1>&2
echo. 1>&2
echo Please set the JAVA_HOME variable in your environment to match the 1>&2
echo location of your Java installation. 1>&2

goto fail

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\test3-1.0-SNAPSHOT.jar;%APP_HOME%\lib\okhttp-4.11.0.jar;%APP_HOME%\lib\okio-jvm-3.2.0.jar;%APP_HOME%\lib\kotlin-stdlib-jdk8-1.7.21.jar;%APP_HOME%\lib\aws-java-sdk-s3-1.12.744.jar;%APP_HOME%\lib\logback-classic-1.2.6.jar;%APP_HOME%\lib\s3-2.17.79.jar;%APP_HOME%\lib\aws-xml-protocol-2.17.79.jar;%APP_HOME%\lib\aws-query-protocol-2.17.79.jar;%APP_HOME%\lib\protocol-core-2.17.79.jar;%APP_HOME%\lib\aws-core-2.17.79.jar;%APP_HOME%\lib\auth-2.17.79.jar;%APP_HOME%\lib\regions-2.17.79.jar;%APP_HOME%\lib\sdk-core-2.17.79.jar;%APP_HOME%\lib\arns-2.17.79.jar;%APP_HOME%\lib\profiles-2.17.79.jar;%APP_HOME%\lib\apache-client-2.17.79.jar;%APP_HOME%\lib\netty-nio-client-2.17.79.jar;%APP_HOME%\lib\http-client-spi-2.17.79.jar;%APP_HOME%\lib\metrics-spi-2.17.79.jar;%APP_HOME%\lib\json-utils-2.17.79.jar;%APP_HOME%\lib\utils-2.17.79.jar;%APP_HOME%\lib\slf4j-api-1.7.32.jar;%APP_HOME%\lib\aws-lambda-java-core-1.2.1.jar;%APP_HOME%\lib\aws-lambda-java-events-3.11.6.jar;%APP_HOME%\lib\aws-java-sdk-sqs-1.12.745.jar;%APP_HOME%\lib\aws-java-sdk-kms-1.12.744.jar;%APP_HOME%\lib\aws-java-sdk-core-1.12.745.jar;%APP_HOME%\lib\jmespath-java-1.12.745.jar;%APP_HOME%\lib\jackson-dataformat-cbor-2.17.1.jar;%APP_HOME%\lib\jackson-databind-2.17.1.jar;%APP_HOME%\lib\jackson-annotations-2.17.1.jar;%APP_HOME%\lib\jackson-core-2.17.1.jar;%APP_HOME%\lib\jackson-module-kotlin-2.17.1.jar;%APP_HOME%\lib\kotlin-stdlib-jdk7-1.7.21.jar;%APP_HOME%\lib\kotlin-reflect-1.7.22.jar;%APP_HOME%\lib\kotlin-stdlib-1.7.22.jar;%APP_HOME%\lib\logback-core-1.2.6.jar;%APP_HOME%\lib\joda-time-2.12.7.jar;%APP_HOME%\lib\annotations-2.17.79.jar;%APP_HOME%\lib\kotlin-stdlib-common-1.7.22.jar;%APP_HOME%\lib\annotations-13.0.jar;%APP_HOME%\lib\netty-reactive-streams-http-2.0.5.jar;%APP_HOME%\lib\netty-reactive-streams-2.0.5.jar;%APP_HOME%\lib\reactive-streams-1.0.3.jar;%APP_HOME%\lib\eventstream-1.0.1.jar;%APP_HOME%\lib\httpclient-4.5.13.jar;%APP_HOME%\lib\httpcore-4.4.13.jar;%APP_HOME%\lib\netty-codec-http2-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-http-4.1.68.Final.jar;%APP_HOME%\lib\netty-handler-4.1.68.Final.jar;%APP_HOME%\lib\netty-codec-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-native-epoll-4.1.68.Final-linux-x86_64.jar;%APP_HOME%\lib\netty-transport-native-unix-common-4.1.68.Final.jar;%APP_HOME%\lib\netty-transport-4.1.68.Final.jar;%APP_HOME%\lib\netty-buffer-4.1.68.Final.jar;%APP_HOME%\lib\netty-resolver-4.1.68.Final.jar;%APP_HOME%\lib\netty-common-4.1.68.Final.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.15.jar;%APP_HOME%\lib\third-party-jackson-core-2.17.79.jar


@rem Execute test3
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %TEST3_OPTS%  -classpath "%CLASSPATH%" MainKt %*

:end
@rem End local scope for the variables with windows NT shell
if %ERRORLEVEL% equ 0 goto mainEnd

:fail
rem Set variable TEST3_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
set EXIT_CODE=%ERRORLEVEL%
if %EXIT_CODE% equ 0 set EXIT_CODE=1
if not ""=="%TEST3_EXIT_CONSOLE%" exit %EXIT_CODE%
exit /b %EXIT_CODE%

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
