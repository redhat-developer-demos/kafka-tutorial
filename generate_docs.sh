#!/bin/sh

docker run -v `pwd`:/antora --rm -t antora/antora:2.0¡2.0 --pull --stacktrace gh-pages-site.yml