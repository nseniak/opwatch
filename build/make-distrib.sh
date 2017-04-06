git pull origin master
(cd ..;mvn install) || exit 1
version="1.0-SNAPSHOT"
src_jar_name="alerter-${version}.jar"
src_jar_file=../target/${src_jar_name}
target_jar_name="alerter.jar"

gsutil cp ${src_jar_file} gs://senders-deployment/prod/builds/${target_jar_name} || exit 1
echo "Jar file ${src_jar_name} successfully uploaded as ${target_jar_name}"
