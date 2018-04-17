#!/usr/bin/env bash
python_exec="python3.6"
python_path=$(which ${python_exec})
if [ -z "${python_path}" ]
then
  echo "Documentation generation requires ${python_exec}. Please install it."
  exit 1
fi
mvn_options=$*
(cd ..;mvn clean install ${mvn_options}) || exit 1
${python_exec} generate_example_index.py
