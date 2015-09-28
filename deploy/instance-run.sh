app_name=$1
instance_name=$2
if [ -z "${app_name}" -o -z "${instance_name}" ]
then
  echo "Usage: $0 app_name instance_name" 1>&2
  exit 1
fi
sh ${BASTION_HOME}/gcloud-ssh.sh ${instance_name} "cd alerter;sh run.sh ${app_name}"
