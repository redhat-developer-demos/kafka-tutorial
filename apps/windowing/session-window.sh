#!/bin/bash

kcat -b localhost:29092 -t played -P -l -K: apps/windowing/first-batch.json

echo "Sleeping 30s"

sleep 30

echo "Insert a new batch"

kcat -b localhost:29092 -t played -P -l -K: apps/windowing/first-batch.json

echo "Sleeping 40s"

sleep 45

echo "Insert a new batch"

kcat -b localhost:29092 -t played -P -l -K: apps/windowing/first-batch.json

echo "Notice that since the beginning it has passed more than 60s but still in the same time window"

echo "Sleeping 65s"

sleep 65

echo "Insert a new batch"

kcat -b localhost:29092 -t played -P -l -K: apps/windowing/first-batch.json

echo "Now new session window"