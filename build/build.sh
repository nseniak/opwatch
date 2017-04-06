#!/usr/bin/env bash
(cd ..;mvn clean install) || exit 1
cp ../target/*.jar ../bin/lib
