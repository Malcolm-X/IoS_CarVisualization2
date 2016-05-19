##
## AUTHOR MWALL
##
## Docker deploying a docker container running ubuntu image with apache installed
## with a R application together with R-shiny as webfrontend
##
#FROM ubuntu:14.04
#MAINTAINER mwall
#WORKDIR /tmp
#LABEL version="1.0"
#LABEL description="car Visualization Project at IoS Project TU-Berlin 2016. R-shiny Service"
#
#RUN sudo apt-get update && apt-get install -y --force-yes r-base \
#    gdebi-core \
#    wget
#
#RUN wget https://download3.rstudio.org/ubuntu-12.04/x86_64/shiny-server-1.4.2.786-amd64.deb
#
#
#RUN R -e "install.packages(c('shiny', 'rmarkdown'), repos='https://cran.rstudio.com/')"
#
##RUN apt-get install -y --force-yes
##RUN R -e "install.packages('shiny', repos='https://cran.rstudio.com/')"
##RUN wget https://download3.rstudio.org/ubuntu-12.04/x86_64/shiny-server-1.4.2.786-amd64.deb
#
#
#
#
#RUN yes | gdebi shiny-server-1.4.2.786-amd64.deb
#
## ADD Project sources
#
#EXPOSE 3838
#ENTRYPOINT ["/usr/bin/shiny-server"]
#
#
FROM r-base:latest

MAINTAINER Daniel

RUN apt-get update && apt-get install -y -t unstable \
    sudo \
    gdebi-core \
    pandoc \
    pandoc-citeproc \
    libcurl4-gnutls-dev \
    libcairo2-dev/unstable \
    libxt-dev \
    git

# Download and install shiny server
RUN wget --no-verbose https://s3.amazonaws.com/rstudio-shiny-server-os-build/ubuntu-12.04/x86_64/VERSION -O "version.txt" && \
    VERSION=$(cat version.txt)  && \
    wget --no-verbose "https://s3.amazonaws.com/rstudio-shiny-server-os-build/ubuntu-12.04/x86_64/shiny-server-$VERSION-amd64.deb" -O ss-latest.deb && \
    gdebi -n ss-latest.deb && \
    rm -f version.txt ss-latest.deb && \
    R -e "install.packages(c('shiny', 'rmarkdown', 'leaflet', 'dygraphs', 'reshape2', 'dplyr', 'magrittr', 'saves', 'googleVis', 'curl', 'xts', 'markdown'), repos='https://cran.rstudio.com/')" && \
    cp -R /usr/local/lib/R/site-library/shiny/examples/* /srv/shiny-server/

# clone Austins Repo
#RUN git clone https://github.com/austinctodd/ConnectedCar.git /home/shiny/ConnectedCar

#WORKDIR /home/shiny

#RUN R -e "shiny::runApp('ConnectedCar', port = '3838', host = '0.0.0.0')"

#WORKDIR /

ENTRYPOINT ./usr/bin/shiny-server

EXPOSE 3838

#RUN bash
#COPY shiny-server.sh /usr/bin/shiny-server.sh

#CMD ["./usr/bin/shiny-server"]

#R -e "shiny::runApp('ui.R')"