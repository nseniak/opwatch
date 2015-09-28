# Update descriptors
echo "Updating descriptors"
. ./credentials.sh
descriptor_dir="tbalerts"
if [ -d ${descriptor_dir} ]
then
  echo "Updating ${descriptor_dir}"
  (cd ${descriptor_dir}; git pull origin master)
else
  echo "Cloning ${descriptor_dir}"
  git clone https://${UNTRACKR_GITHUB_TOKEN}@github.com/nseniak/${descriptor_dir}.git
fi
