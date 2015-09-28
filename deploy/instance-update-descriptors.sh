instance_name=$1
if [ -z "${instance_name}" ]
then
  echo "Usage: $0 instance_name" 1>&2
  exit 1
fi
sh ${BASTION_HOME}/gcloud-ssh.sh ${instance_name} "cd alerter;sh update-descriptors.sh"
