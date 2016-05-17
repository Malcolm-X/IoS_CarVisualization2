#  Docker environment

:with_toc_data

## Structure

|-main.yml docker compose file defining whole setup 
|--frontend frontend folder
|--frontend/Dockerfile frontend Dockerfile
|--frontend/files files belonging to frontend container(copied into)
|--mongodb mongodb folder 
|--mongodb/Dockerfile mongodb Dockerfile
|--R/Dockerfile R-middleware Dockerfile

Above structure shows how folder structure is designed. 
All services should run in port-space 8???. So e.g. port 80 is 8080 etc. Further all containers end up on the same linux bridge so they got connected to each other. For setting up a new container copy existing folder and edit start script and Dockerfile. Also provide needed files inside the container.

## Install packets

### install docker

Because all of us, kind of know how a docker container works. There is install guide here

To install docker just follow the guide you can find on the official docker website:

[docker install guide](https://docs.docker.com/engine/installation/)

### docker compose

To deploy multi-service environment using docker containers its best to have a single deployment-tool building the whole stack. Therefore [docker compose](https://docs.docker.com/compose/overview/) is it good way of doing this. In this project each service will run in a seperate container, but build all together with docker compose.

Further you can find the current version of the compose script here. This file defines all the service and container and further there configuration, like port bindings, bridges, images, names etc. The defined workflow inside the containers will be defined by the Dockerfile which has do be provided for each service(container).

## install docker compose

With docker compose we all can run the whole stack on our machines and further test new components easily without trying to understand other people implementation in to much detail. the install guide for docker compose you can find here:
[docker compose install guide](https://docs.docker.com/compose/install/)
