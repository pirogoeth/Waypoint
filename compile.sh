#!/usr/bin/env bash

version=`cat src/plugin.yml | grep version | awk '{print $2}'`

echo "[Waypoint(${version})] building.]"

javac -g -cp inc/craftbukkit.jar:inc/permissions.jar:inc/bukkit.jar:inc/vault.jar \
    src/me/pirogoeth/Waypoint/*.java src/me/pirogoeth/Waypoint/Core/*.java \
    src/me/pirogoeth/Waypoint/Commands/*.java src/me/pirogoeth/Waypoint/Events/*.java \
    src/me/pirogoeth/Waypoint/Util/*.java

jar cvf "Waypoint-${version}.jar" -C src/ .