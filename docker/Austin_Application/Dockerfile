#
# AUTHOR MWALL
# 
# Docker deploying a docker container running ubuntu image with apache installed
# with a R application together with R-shiny as webfrontend
#
FROM ubuntu:14.04
MAINTAINER mwall
WORKDIR /tmp
LABEL version="1.0"
LABEL description="car Visualization Project at IoS Project TU-Berlin 2016. R-shiny Service"
RUN sudo apt-get update && apt-get install -y --force-yes r-base
RUN R -e "install.packages('shiny', repos='https://cran.rstudio.com/')"
RUN apt-get install -y --force-yes gdebi-core wget
RUN wget https://download3.rstudio.org/ubuntu-12.04/x86_64/shiny-server-1.4.2.786-amd64.deb
RUN yes | gdebi shiny-server-1.4.2.786-amd64.deb

# ADD Project sources 

EXPOSE 3838
ENTRYPOINT ["/usr/bin/shiny-server"]


