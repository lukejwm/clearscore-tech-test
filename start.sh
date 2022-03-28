#!/usr/bin/env bash

set -e

echo "Running ClearScore Tech Test App"

HTTP_PORT=${1}
CSCARDS_ENDPOINT=${2}
SCOREDCARDS_ENDPOINT=${3}

if [ ! -d "target" ]; then
	/usr/bin/sh ./build.sh
fi

/usr/bin/sh ./target/appassembler/bin/start.sh ${HTTP_PORT} ${CSCARDS_ENDPOINT} ${SCOREDCARDS_ENDPOINT}
