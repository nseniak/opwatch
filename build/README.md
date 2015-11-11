## Install the required libraries on the build machine

The first time you build, you need to install the required libraries. 

### Install pushover4j

	$ git clone https://github.com/nseniak/pushover4j.git
	$ cd pushover4j
	$ mvn clean install

## Build the alerter

Don't forget to commit and push alerter.

Then on the build machine:

    $ cd alerter-build/alerter
    $ git pull origin master
    $ cd build
    $ sh build.sh
