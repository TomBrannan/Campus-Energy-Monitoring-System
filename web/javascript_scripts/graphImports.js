var scripts = [ //Javascript scripts to be loaded
    "javascript_scripts/charts/highcharts.js",         //For normal graphs
    "javascript_scripts/charts/highcharts-more.js",    //For angular gauge
    "javascript_scripts/charts/data.js",
    "javascript_scripts/chartsexporting.js",
    "http://www.highcharts.com/media/com_demo/highslide-full.min.js", //For point click popups
    "http://www.highcharts.com/media/com_demo/highslide.config.js",
    "javascript_scripts/charts/solid-gauge.js", //For widget
    "javascript_scripts/graphUtilities.js" //For useful functions for all graphs
];
scripts.forEach(function(script){ //Load them all
    document.write('<script type="text/javascript" src="' + script + '"></script>');
});

var css = [ //CSS files to be loaded
    "http://www.highcharts.com/media/com_demo/highslide.css" //point click popup style
];
css.forEach(function(sheet){
    document.write('<link rel="stylesheet" type="text/css" href="' + sheet + '"/>');
});