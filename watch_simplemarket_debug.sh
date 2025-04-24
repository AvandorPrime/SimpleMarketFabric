#!/bin/bash

# Tail the debug log and filter for "simplemarket"
tail -f run/logs/debug.log | grep --color=always -i simplemarket