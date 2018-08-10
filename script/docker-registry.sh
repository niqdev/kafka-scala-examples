#!/bin/bash

# unofficial bash strict mode
set -euo pipefail
IFS=$'\n\t'

title() {
cat<<"EOT"
TODO title
EOT
}
title

echo "TODO publish"
docker images
