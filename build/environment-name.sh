# This file is copied from untrackr/root/deployment/google/bastion
public_instance_hostname=$1
if [ -z "${public_instance_hostname}" ]
then
  echo "Usage: environment-name.sh public_instance_hostname" 1>&2
  exit 1
fi
network=$(gcloud compute instances describe ${public_instance_hostname} | grep "network:" | sed -n -e 's/^.*networks\///p')
if [ "$network" = "default" ]
then
	echo "prod"
else
	echo "${network}"
fi
