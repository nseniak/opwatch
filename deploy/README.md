Commit and push all changes from alerter and tbalerts.

On the build machine:

    $ cd alerter-build/alerter/deploy
    $ git pull origin master
    $ sh build.sh

On the dev machine:

    $ sh redeploy.sh
