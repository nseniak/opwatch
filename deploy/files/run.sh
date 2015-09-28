app_name=$1
if [ -z "${app_name}" ]
then
  echo "Usage: $0 app_name" 1>&2
  exit 1
fi
# Exit current process
alerter_jar="alerter.jar"
alerter_jar_temp="${alerter_jar}.new"
pid=$(pgrep -f "^java.*${alerter_jar}")
if [ -z "${pid}" ]
then
  echo -n
else
	kill ${pid}
	echo "Waiting for ${alerter_jar} (${pid}) to exit"
	while kill -0 ${pid} > /dev/null 2>&1
	do
		sleep 0.5
	done
	echo "Exited"
fi
# Start new process
if [ -f "${alerter_jar_temp}" ]
then
	mv ${alerter_jar_temp} ${alerter_jar}
fi
export ALERTER_PATH=tbalerts/common
export LOG_DIR=${HOME}/latest/logs
nohup java \
	-DALERTER_MAIN=tbalerts/${app_name}/main.json \
	-DALERTER_DESCRIPTOR_PATH=tbalerts/common:tbalerts/common_app \
	-jar ${alerter_jar} \
	>> logs/alerter.log 2>&1 &
