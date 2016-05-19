library(reshape2)
library(dplyr)
library(magrittr)
library(saves)
library(shiny)
library(googleVis)
library(leaflet)
library(dplyr)
library(curl) # make the jsonlite suggested dependency explicit
library(dygraphs)
library(xts)

#cars_weather <- read.csv("/Users/austintodd/Documents/Work/Projects/AIL/Code/cars_weather.csv",header=TRUE,sep=",")
#uniqueIDs <- unique(cars_weather$DeviceID)

shinyServer(function(input, output, session) {

  observe({
    updateSelectizeInput(session, 'trip_id', 
                       choices = as.character(sort(unique(cars_weather[cars_weather$DeviceID==input$car_ind,'Trip']),decreasing=FALSE)),
                       selected= as.character(sort(unique(cars_weather[cars_weather$DeviceID==input$car_ind,'Trip']),decreasing=FALSE)[1]))
  })
  
  tripdata <- reactive({
    if ( is.null(input$trip_id))
      return()
    cars_weather[(cars_weather$DeviceID==input$car_ind) & (cars_weather$Trip == input$trip_id), ]
  })
  
  output$busmap <- renderLeaflet({
    colors <- tripdata()$GPS_Speed/max(cars_weather$GPS_Speed)
    lons <- tripdata()$GPS_Longitude
    lats <- tripdata()$GPS_Latitude

    startIcon <- makeIcon(
      iconUrl = "carGreen.svg", iconWidth = 18, iconHeight =24
     )
    stopIcon <- makeIcon(
      iconUrl = "carRed.svg", iconWidth = 18, iconHeight =24
    )
    
    leaflet() %>%
    addTiles('http://{s}.tile.thunderforest.com/transport/{z}/{x}/{y}.png') %>% 
    setView(lng = -83.75, lat = 42.28, zoom = 12) %>%
    addPolylines(lons[seq(1,length(lons),10)],
                     lats[seq(1,length(lats),10)],
                     color= "blue",
                     weight=8,
                     opacity = 0.6) %>%
    addMarkers(lons[1],lats[1],
               icon = startIcon,
               popup = paste('<b>Start Time</b><br>',as.character(as.POSIXct(as.POSIXlt(tripdata()$TimeUTC[1],tz="EST5EDT"),tz="GMT")))) %>%
    addMarkers(lons[length(lons)],lats[length(lats)],
               icon = stopIcon,
               popup = paste('<b>End Time</b><br>',as.character(as.POSIXct(as.POSIXlt(tripdata()$TimeUTC[length(lons)],tz="EST5EDT"),tz="GMT"))))
  })
  
  #---------------------------------------------------
  # Add functionality for plotting data using dygraph
  #---------------------------------------------------

  # Reactive data for updated time series
  pltdata <- eventReactive(input$makeplot,{
    t <- tripdata()[c('TimeUTC',input$plotvars)]
    t[,1] <- as.POSIXct(as.POSIXlt(t[,1],tz="EST5EDT"))
    xts(t[,-1],order.by=t[,1])
  })
  
  output$dygraph <- renderDygraph({
    dygraph(pltdata()) %>%
      dyHighlight(highlightCircleSize = 5) %>%
      dyRangeSelector() %>%
      dyLegend(width = 600) %>%
      dyOptions(colors = RColorBrewer::brewer.pal(3, "Set2"))
  })
  
  output$from <- renderText({
    if (!is.null(input$dygraph_date_window))
      strftime(input$dygraph_date_window[[1]], "%H:%M:%S")      
  })
  
  output$to <- renderText({
    if (!is.null(input$dygraph_date_window))
      strftime(input$dygraph_date_window[[2]], "%H:%M:%S")
  })  
  
  output$plot_hoverinfo <- renderPrint({
    cat("input$plot_hover:\n")
    str(input$plot_hover)
  })
})


