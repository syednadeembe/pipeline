#! /bin/bash
###############################################################################
# Name: IS_TestPkgs.sh
###############################################################################
# Description:
# To check if required packages are installed
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

SAG_HOME=/opt/softwareag
pkgDir=$SAG_HOME/IntegrationServer/instances/default/packages
errCount=0
DIRNAME=`dirname $0`
respFile=`mktemp -u -p ${DIRNAME} -t IS_XXXXX.out`
pkgFile=wmpackage.txt
sleepTime=10
maxDependWaitCount=20
user=Administrator
passwd=manage
errCount=0

###############################################################################
# Function   Declaration
###############################################################################

# Write message to stdout and exit
function exit_error {
  echo "[`date`] ${*}"
  exit 1
}

# Write message to stdout
function console_msg {
  echo "[`date`] ${*}"
}

function startSPM {
  $SAG_HOME/profiles/SPM/bin/startup.sh
  iterCount=0
  while [ $iterCount -ne $maxDependWaitCount ]
  do
    retCount=`netstat -pantl |grep LISTEN |grep 8092 |wc -l`
    if [ $retCount -eq 1 ]
    then
      retCode=`curl --silent --show-error --write-out "%{http_code}" \
          --output $respFile -u "$user:$passwd" "http://localhost:8092/spm/monitoring/runtimestatus/OSGI-SPM"`
      if [ $retCode -eq 200 ]
      then
        grep "ONLINE" $respFile > /dev/null
        if [ $? -eq 0 ]
        then
          console_msg "Info: SPM Server is up"
          break
        fi
      fi
    fi
    iterCount=`expr $iterCount + 1`
    console_msg "Warning: Waiting for SPM Server to start #${iterCount}"
    sleep $sleepTime
  done
  if [ $retCount -ne 1 ]
  then
    exit_error "Error: SPM Server is not responding"
  fi
}

function startIS {
  $SAG_HOME/profiles/IS_default/bin/startup.sh
  iterCount=0
  while [ $iterCount -ne $maxDependWaitCount ]
  do
    retCode=`curl --silent --show-error --write-out "%{http_code}" \
          --output $respFile -u "$user:$passwd" "http://localhost:8092/spm/monitoring/runtimestatus/OSGI-IS_default"`
    if [ $retCode -eq 200 ]
    then
      grep "ONLINE" $respFile > /dev/null
      if [ $? -eq 0 ]
      then
        console_msg "Info: Integration Server is up"
        break
      fi
    fi
    iterCount=`expr $iterCount + 1`
    console_msg "Warning: Waiting for Integration Server to start #${iterCount}"
    sleep $sleepTime
  done
  if [ $retCode -ne 200 ]
  then
    exit_error "Error: Integration Server is not responding"
  fi
}

function getISFixes {
    retCode=`curl --silent --show-error --write-out "%{http_code}" \
          --output $respFile -u "$user:$passwd" "http://localhost:8092/spm/inventory/fixes"`
    if [ $retCode -ne 200 ]
    then
      exit_error "Error: Unable to get Fix information, SPM Server is not responding"
    fi
    awk '{
      if ( $1 ~ /ID/ ) {
        print ("FIXID_VERSION" );
      } else {
        print ( $1 "_" $NF );
      }
    }' $respFile
}

###############################################################################
# Main Declaration
###############################################################################

ls -l ${pkgDir}
for pkg in `cat ${DIRNAME}/$pkgFile |grep -v "#"`
do
  if [ -d ${pkgDir}/${pkg} ]
  then
    console_msg "Info: Integration Server package ${pkg} exists"
  else
    console_msg "Error: Integration Server package ${pkg} does not exists"
    errCount=`expr $errCount + 1`
  fi
done

console_msg "Info: List of fix readme"
ls -l $SAG_HOME/install/fix/readme/

opProc=`netstat -pantl |grep LISTEN |grep 8092 |wc -l`
if [ $opProc -gt 0 ]
then
  console_msg "Info: SPM Process is already running"
else
  startSPM
fi

netstat -pantl
opProc=`netstat -pantl |grep LISTEN |grep 5555 |wc -l`
if [ $opProc -gt 0 ]
then
  console_msg "Info: Integration Server Process is already running"
else
  startIS
fi
getISFixes

if [ $errCount ne 0 ]
then
  exit_error "Error: Unhandled errors, please check the log"
fi
