# gchisto
A Hotspot JVM garbage collection log visualisation tool. Originally hosted at https://java.net/projects/gchisto.  Moved it to github and mavenized it.

# travis-ci build status
[![Build Status](https://travis-ci.org/jewes/gchisto.svg?branch=master)](https://travis-ci.org/jewes/gchisto)

# How to build gchisto
    mvn clean install

It will creates target/gchisto-\<version>.tar.gz which includes gchisto binary and its dependencies.

# How to run it
Extract the tar.gz and run the following command to launch gchisto:

    java -jar gchisto-\<version>.jar
