# Meeting-Scheduler
Instructions to build and run

application requires jdk 11 

database must also be set up

example

download xampp

start apache and mysql

navigate to http://localhost/phpmyadmin in web browser

create new database individual_project

import src/main/resources/database/setup.sql

if a different database name is chosen or a custom username or password is chosen the static variables in connect.java must be changed as appropriate

open command prompt

navigate to project folder

enter command gradlew.bat run

to build and run the project
