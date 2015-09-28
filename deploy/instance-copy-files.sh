instance_name=$1
if [ -z "${instance_name}" ]
then
  echo "Usage: $0 instance_name" 1>&2
  exit 1
fi
sh ${BASTION_HOME}/gcloud-ssh.sh ${instance_name} "mkdir -p alerter/logs"
sh ${BASTION_HOME}/gcloud-scp.sh files/update-descriptors.sh ${instance_name}:alerter/
sh ${BASTION_HOME}/gcloud-scp.sh files/run.sh ${instance_name}:alerter/
sh ${BASTION_HOME}/gcloud-scp.sh ${CREDENTIAL_HOME}/credentials.sh ${instance_name}:alerter/
alerter_jar="alerter.jar"
alerter_jar_temp="${alerter_jar}.new"
sh ${BASTION_HOME}/gcloud-ssh.sh ${instance_name} "gsutil cp gs://trackbuster-alerter-builds/${alerter_jar} alerter/${alerter_jar_temp}"
