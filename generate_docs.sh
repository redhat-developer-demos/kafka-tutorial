#!/bin/sh

docker run -v `pwd`:/antora --rm -t antora/antora:2.2.0 --pull --stacktrace github-pages-site.yml