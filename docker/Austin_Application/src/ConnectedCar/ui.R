library(markdown)
#load("label.RData")
library(leaflet)
library(dygraphs)

shinyUI(
  navbarPage(
    title="",
    windowTitle="Connected Car Visualization", collapsible=TRUE, fluid=FALSE,
    id='page',
    tabPanel(
      title = "Graphic Explorer",
      id='graphic_tabs',
      fluidRow(
        column(width = 9, includeMarkdown("intro_graphic.md")),
        column(width = 3, includeHTML("intro_logo.html"))
      ),
      hr(),
      fluidRow(
        column(width=6,"Select vehicle device ID and Trip ID. The data for each selection will be updated in the map and plots below."),
        column(width=3, selectizeInput("car_ind", "Car ID", 
                                       choices = c(as.character(sort(uniqueIDs,decreasing=FALSE))), 
                                       selected=as.character(sort(uniqueIDs,decreasing=FALSE)[1]), width="80%")),
        column(width = 3, selectizeInput("trip_id", "Trip ID",
                                         choices =  as.character(sort(unique(cars_weather[cars_weather$DeviceID==sort(uniqueIDs,decreasing=FALSE)[1],'Trip'],decreasing=FALSE),decreasing=FALSE)),
                                         width="80%"))
      ),
      hr(),
      br(),
      tabsetPanel(
        type = "tabs", 
        tabPanel("Map", br(), source("map_select.R", local=TRUE)$value),
        tabPanel("Plots",br(),source("plots_select.R",local=TRUE)$value)
      )
    ),
    tabPanel(
      title = "About",
      id='about_tabs',
      fluidRow(
        column(width = 8, includeMarkdown("intro_about.md")),
        column(width = 4, includeHTML("intro_logo.html"))
      ),
      br(),
      tabsetPanel(
        type = "tabs", 
        tabPanel("Details", includeMarkdown("about_details.md")),
        tabPanel("FAQ", br())
      )
    ),
    includeCSS("style.css"),
    tags$script(includeHTML("sm-share.html"))
  )
)

