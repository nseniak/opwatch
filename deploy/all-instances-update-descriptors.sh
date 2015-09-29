instance_names=$*
for instance_name in ${instance_names}
do
  sh instance-update-descriptors.sh ${instance_name} || exit 1
done
echo "All descriptors successfully updated on ${instance_names}"
