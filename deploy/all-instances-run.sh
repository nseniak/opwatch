#!/bin/sh
app_name=$1
if [ -z "${app_name}" ]
then
  echo "Usage: $0 app_name instance_name..." 1>&2
  exit 1
fi
shift
instance_names=$*
for instance_name in ${instance_names}
do
  sh instance-run.sh ${app_name} ${instance_name} || exit 1
done
echo "All instances running: ${instances}"
