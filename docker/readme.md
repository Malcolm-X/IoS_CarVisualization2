# Setting Up Docker environment

## install docker

Because all of us, kind of know how a docker container works. There is install guide here

To install docker just follow the guide you can find on the official docker website:

[docker install guide](https://docs.docker.com/engine/installation/)

## docker compose

To deploy multi-service environment using docker containers its best to have a single deployment-tool building the whole stack. Therefore [docker compose](https://docs.docker.com/compose/overview/) is it good way of doing this. In this project each service will run in a seperate container, but build all together with docker compose.

Further you can find the current version of the compose script here. This file defines all the service and container and further there configuration, like port bindings, bridges, images, names etc. The defined workflow inside the containers will be defined by the Dockerfile which has do be provided for each service(container).

## install docker compose

With docker compose we all can run the whole stack on our machines and further test new components easily without trying to understand other people implementation in to much detail. the install guide for docker compose you can find here:
[docker compose install guide](https://docs.docker.com/compose/install/)
