#!/usr/bin/env bash
(cd ..;mvn clean install) || exit 1
python3.5 generate_example_index.py
