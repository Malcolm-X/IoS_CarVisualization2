library(shiny)

# Define UI for application that draws a histogram
shinyUI(fluidPage(

# Application title
titlePanel("Hello Shhhhiny!"),

# Sidebar with a slider input for the number of bins
sidebarLayout(
sidebarPanel(
sliderInput("bins",
"Number of bins:",
min = 1,
max = 60,
value = 30)
),

# Show a plot of the generated distribution
mainPanel(
plotOutput("distPlot")
)
)
))