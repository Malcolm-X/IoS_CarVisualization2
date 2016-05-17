#
# AUTHOR mwall
#
# this is a starter script for a docker container running the IoS Project: "car Visualization" frontend
#

#!/bin/bash

# local webserver port
NAME="IoT_Frontend"
LOCAL_HTTP_PORT=8080
LOCAL_HTTPS_PORT=8430


git pull

mkdir -p logs
LOG_PATH='logs/'`date +%H:%m_%d%M%Y`'-frontend.log'

echo "start building docker container"

docker build  .  | tee $LOG_PATH
Container_ID=`tail -1 $LOG_PATH | awk '{ print $3}'`
echo 
echo "starting container with iD= $Container_ID "
echo 

#run container with binding ports
docker run -it --rm -p $LOCAL_HTTP_PORT:80 -p $LOCAL_HTTPS_PORT:443 --name $NAME $Container_ID
