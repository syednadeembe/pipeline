#! /bin/bash

###############################################################################
# Name: env.sh
###############################################################################
# Description:
# To define environment variable for Jenkins job
###############################################################################
# Copyright (c) 2018-2019 Software AG, Darmstadt, Germany
# and/or Software AG USA Inc., Reston, VA, USA, and/or its subsidiaries and/or
# its affiliates and/or their licensors.
# Use, reproduction, transfer, publication or disclosure is prohibited except
# as specifically provided for in your License Agreement with Software AG.
###############################################################################

###############################################################################
# Variable  Declaration
###############################################################################

envFile=${TAG}.env

###############################################################################
# Main Declaration
###############################################################################

if [[ -f ${envFile} && -x ${envFile} ]]
then
  . ./$envFile
  for var in `grep -v "^#" $envFile | cut -d"=" -f1`
  do
    export $var
  done
else
  echo "Error: ${envFile} does not exists or do not have execute permissions"
fi
