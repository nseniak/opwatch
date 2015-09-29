sh all-instances-copy-files.sh ${instances} ${es_host_name} || exit 1
sh all-instances-update-descriptors.sh ${instances} ${es_host_name} || exit 1
sh all-instances-run.sh webapp ${webapp_instances} || exit 1
sh all-instances-run.sh watcher ${watcher_instances} || exit 1
sh all-instances-run.sh elk ${es_host_name} || exit 1
