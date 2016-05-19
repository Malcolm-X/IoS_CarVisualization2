#
# AUTHOR mwall
#
# this is a starter script for a docker container running the IoS Project: "car Visualization" frontend
#

#!/bin/bash

# local webserver port
NAME="iot_r-shiny_app"
SHINY_PORT=8383


git pull

mkdir -p ../logs
LOG_PATH='../logs/'`date +%H:%m_%d%M%Y`'-frontend.log'

echo "start building R shiny docker container"

docker build  .  | tee $LOG_PATH
Container_ID=`tail -1 $LOG_PATH | awk '{ print $3}'`
echo 
echo "starting container with iD= $Container_ID "
echo

#run container with binding ports
docker run -it --rm -p $SHINY_PORT:3838 --name "$NAME" -v $(pwd)/src/ConnectedCar:/home/ConnectedCar -v $(pwd)/src/TestApplication:/home/TestApplication "$Container_ID"
