#!/usr/bin/env bash

version=`cat src/plugin.yml | grep version | awk '{print $2}'`

echo "[Waypoint(${version})] building.]"

javac -Xstdout compile_log.txt -g:none -cp inc/craftbukkit.jar:inc/permissions.jar:inc/bukkit.jar:inc/vault.jar \
    src/me/pirogoeth/Waypoint/*.java src/me/pirogoeth/Waypoint/Core/*.java \
    src/me/pirogoeth/Waypoint/Commands/*.java src/me/pirogoeth/Waypoint/Events/*.java \
    src/me/pirogoeth/Waypoint/Util/*.java src/net/eisental/common/page/*.java
    # src/net/eisental/common/parsing/*.java

errors=$(cat "./compile_log.txt" | grep "errors")

if ! test -z ${errors} && ! $(echo ${errors} | tr -d "[[:space:]]") == ""; then
    echo "$(cat compile_log.txt)"
    exit 1
fi

jar cvf "Waypoint-${version}.jar" -C src/ . 2>&1 1>archive_log.txt

cat archive_log.txt

rm ./*_log.txt