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


WORKDIR /home

#ENTRYPOINT R -e "shiny::runApp('TestApplication' , port = 3838, host = '0.0.0.0')"

ENTRYPOINT R -e "shiny::runApp('ConnectedCar' , port = 3838, host = '0.0.0.0')"

EXPOSE 3838
