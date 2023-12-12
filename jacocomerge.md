# How to Measure Merged Code Coverage Using Jacoco


## 1. Setup

1. clone project from https://github.com/kulasangeles/spring-petclinic
2. Follow the necessary set-up commands in [readme.md](readme.md)
3. Create a libs folder and download jacocoagent.jar from https://www.eclemma.org/jacoco/ (this project uses 0.8.11), unzip then copy the jar file to your libs folder

## 2. Generating the unit test jacoco exec and report

&ast; <em>note that other unit tests were moved to /testbackup to show low unit test report</em>
* Run the command below for running the test and generating the jacoco-ut.exec file
~~~
./mvnw clean -Djacoco.destFile=./target/coverage-reports/jacoco-ut/jacoco-ut.exec test jacoco:prepare-agent
~~~
* Run the command below to generate the report for jacoco-ut
~~~
./mvnw jacoco:report@jacoco-unit-test-report   
~~~

## 2. Run the application and attach a javaagent
* Look for the [PetClinicApplication.java](src%2Fmain%2Fjava%2Forg%2Fspringframework%2Fsamples%2Fpetclinic%2FPetClinicApplication.java), right click then run or alternatively create a run cofiguration then add this VM option to activate the jacoco listener
~~~
-javaagent:./libs/jacocoagent.jar=output=tcpserver,address=*,port=6300
~~~
* Run the application


## 3. Run your automated tests or do manual exploratory actions

## 4. Run dump command
* Run this command in the terminal (while the app is running)
~~~
./mvnw jacoco:dump@jacoco-dump  
~~~
* Run the command below to generate the report for jacoco-sit
~~~
./mvnw jacoco:report@jacoco-sit-test-report   
~~~

## 4. Run the merge command
* Run this command in the terminal to merge the reports
~~~
./mvnw jacoco:merge@jacoco-merge
~~~
* Run the command below to generate the report for jacoco-aggregated
~~~
./mvnw jacoco:report@jacoco-aggregated-test-report   
~~~

## 5. Validate aggregated report
* Locate the aggregated report in /target/coverage-reports/jacoco-aggregated
