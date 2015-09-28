instances=$*
for instance_name in ${instance_names}
do
  sh instance-copy-files.sh ${instance_name} || exit 1
done
echo "All files successfully copied to ${instances}"
