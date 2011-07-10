#!/usr/bin/env bash

javac -g -verbose -extdirs inc -cp inc/bukkit.jar -cp inc/permissions.jar src/me/pirogoeth/Waypoint/Waypoint*.java

jar cvf Waypoint.jar -C src/ .

scp -P12345 Waypoint.jar miyoko@irc.maio.me:/home/miyoko/minecraft/plugins

echo "done."