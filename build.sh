#!/usr/bin/env bash

read -p "[Shaun, did you remember to change the version number if this is a release build? (y/n)]=> " vn_c

if [ ${vn_c} == "y" ] || [ ${vn_c} == "Y" ]; then
    # skippans
    echo
elif [ ${vn_c} == "n" ] || [ ${vn_c} == "N" ]; then
    echo "Good job buddy."
    exit 0
fi

javac -g -cp inc/craftbukkit.jar:inc/permissions.jar:inc/bukkit.jar \
    src/me/pirogoeth/Waypoint/*.java src/me/pirogoeth/Waypoint/Core/*.java \
    src/me/pirogoeth/Waypoint/Commands/*.java src/me/pirogoeth/Waypoint/Events/*.java \
    src/me/pirogoeth/Waypoint/Util/*.java

read -p "[Compile Finished] " aaa

jar cf Waypoint.jar -C src/ .

read -p "[Archival Finished] " bbb

read -p "[Copy to ~/minecraft/plugins/? (y/n)]=> " confirm

if [ ${confirm} == "y" ] || [ ${confirm} == "Y" ]; then
    cp -v Waypoint.jar ${HOME}/minecraft/plugins/Waypoint.jar
    read -p "[Copy Finished] " ccc
    exit 0
elif [ ${confirm} == "n" ] || [ ${confirm} == "N" ]; then
    read -p "[Source Processing Finished] " ccc
    exit 0
fi
