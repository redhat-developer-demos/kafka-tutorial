#!/bin/sh

docker run -v `pwd`:/antora --rm -t antora/antora:2.3.1 --pull --stacktrace github-pages-site.yml