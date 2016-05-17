#
# AUTHOR MWALL
# 
# Docker deploying a docker container running ubuntu image with apache installed
# with a R application together with R-shiny as webfrontend
#
FROM ubuntu:14.04
MAINTAINER mwall
LABEL version="1.0"
LABEL description="car Visualization Project at IoS Project TU-Berlin 2016. Frontend Service"
RUN apt-get update && apt-get install -y --force-yes apache2
# copy APACHE 2 conf?


# ADD Project sources 

EXPOSE 80 443
ENTRYPOINT ["/usr/sbin/apache2ctl", "-D", "FOREGROUND"]


