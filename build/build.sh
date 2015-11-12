(cd ..;mvn install) || exit 1
version="1.0-SNAPSHOT"
src_jar_name="alerter-${version}.jar"
src_jar_file=../target/${src_jar_name}
target_jar_name="alerter.jar"
export UNTRACKR_ENVIRONMENT_NAME=$(sh environment-name.sh $(hostname))
if [ -z "${UNTRACKR_ENVIRONMENT_NAME}" ]
then
  echo "Undefined variable: UNTRACKR_ENVIRONMENT_NAME" 1>&2
  exit 1
fi
gsutil cp ${src_jar_file} gs://trackbuster-${UNTRACKR_ENVIRONMENT_NAME}/builds/${target_jar_name} || exit 1
echo "Jar file ${src_jar_name} successfully uploaded as ${target_jar_name}"
