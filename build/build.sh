#!/usr/bin/env bash
mvn_options=$*
(cd ..;mvn clean install ${mvn_options}) || exit 1
python3.5 generate_example_index.py
