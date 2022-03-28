#!/usr/bin/env bash

set -e

echo "Running build script for ClearScore Tech Test App"

mvn install
mvn package appassembler:assemble
