#!/usr/bin/env bash

set -e

mvn install
mvn package appassembler:assemble