#!/usr/bin/env bash

set -e

HTTP_PORT=${1}
CSCARDS_ENDPOINT=${2}
SCOREDCARDS_ENDPOINT=${3}

./target/appassembler/bin/start.sh ${HTTP_PORT} ${CSCARDS_ENDPOINT} ${SCOREDCARDS_ENDPOINT}