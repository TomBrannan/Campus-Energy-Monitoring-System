var percentage = 0.00;
var average = 0;
var title = "";
var currentPower = 0;

//Move to properties file  
var DECIMAL_PLACES = 1;
var GREEN_COLOR = '#55BF3B';
var YELLOW_COLOR = '#DDDF0D';
var ORANGE_COLOR = '#FFA500';
var RED_COLOR = '#AA1122';
var useCommas = true;
var roundAverageToNearestHundred = false;
var showAverageTick = true;
var percent = 0.20; //Percentage "plus and minus" on either extreme of the gauge

var widget;

var ticks = [];

$(function () {

    /* //Make the Tooltip never disappear.  Forces the other graph as well...
     (function (H) {
     H.wrap(H.Tooltip.prototype, 'hide', function (defaultCallback) {
     alert(H.Tooltip);
     if(H.Tooltip.backgroundColor === '#FFFFFF')
     {
     
     }
     else
     {
     defaultCallback.apply(this);
     }
     });
     }(Highcharts));
     */

    var container = document.getElementById("widgetContainer");

    $(document).ready(function () {

        $.post("/Lucid/WidgetServlet", function (jsonString) {

            average = Math.floor(jsonString.average);
            percentage = jsonString.percentage;
            title = jsonString.title;
            currentPower = jsonString.currentPower;
            
            var boundBelow = average - average * percent;
            var boundAbove = average + average * percent;

            if (roundAverageToNearestHundred)
                average = 100 * Math.floor((average + 50) / 100);

            if (showAverageTick)
                ticks = [boundBelow, average, boundAbove];
            else
                ticks = [boundBelow, boundAbove];
            createGraph();
            widget.yAxis[0].setExtremes(boundBelow, boundAbove);
        });

    });

    function createGraph()
    {
        widget = new Highcharts.Chart({
            chart: {
                type: 'solidgauge',
                renderTo: container,
                spacingTop: 0,
                marginTop: 45,
                spacingBottom: 0,
                backgroundColor: null,
                events: {
                    load: function () {
                        //Make the tooltip load when the graph loads
                        var p = this.series[0].points[0];
                        this.tooltip.refresh(p);
                    }
                }
            },
            title: {
                text: title,
                y: 25
            },
            subtitle: {
                text: 'Bloomsburg University, Lower Campus'
            },
            pane: {
                center: ['50%', '63%'],
                size: '100%',
                startAngle: -90,
                endAngle: 90,
                background: {
                    backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || '#EEE',
                    innerRadius: '60%',
                    outerRadius: '100%',
                    shape: 'arc'
                }
            },
            tooltip: {
                enabled: true,
                backgroundColor: '#FFFFFF',
                positioner: function (labelWidth, labelHeight, point)
                {
                    tooltipX = labelWidth / 2 + labelWidth / 2.35;
                    tooltipY = labelHeight / 2;

                    return {
                        x: tooltipY,
                        y: tooltipX
                    };
                },
                formatter: function ()
                {
                    var str = "Currently using ";
                    if (percentage < 0)
                        str += (-percentage * 100).toFixed(DECIMAL_PLACES) + "% less than";
                    else if (percentage > 0)
                        str += (percentage * 100).toFixed(DECIMAL_PLACES) + "% more than";
                    else
                        str += "the same amount as";
                    str += " average.";
                    return str;
                }
            },
            // the value axis
            yAxis: {
                stops: [
                    [0.4, GREEN_COLOR], // green
                    [0.5, YELLOW_COLOR], // yellow
                    [0.6, ORANGE_COLOR],
                    [1, RED_COLOR] // red
                ],
                lineWidth: 0, /*
                 minorTickInterval: null,*/
                tickInterval: 1000,
                tickPositions: ticks,
                tickWidth: 0,
                labels: {
                    y: 16,
                    formatter: function () {
                        if (useCommas)
                            return Highcharts.numberFormat(this.value, 0, '', ',');
                        else
                            return this.value;
                    }
                }
            },
            plotOptions: {
                solidgauge: {
                    dataLabels: {
                        borderWidth: 0,
                        useHTML: true,
                        y: -45
                    }
                }
            },
            credits: {
                enabled: false
            },
            series: [{
                    data: [currentPower],
                    name: 'Power',
                    dataLabels: {
                        format: '<div style="text-align:center"><span style="font-size:25px;color:' +
                                ((Highcharts.theme && Highcharts.theme.contrastTextColor) || 'black') + '">' + (useCommas ? addCommas(currentPower) : currentPower) + '</span><br/>' +
                                '<span style="font-size:12px;color:gray">kW</span></div>'
                    },
                    tooltip: {
                        valueSuffix: ' kW'
                    }
                }]

        });
        
        /* //Example showing colors
        setInterval(function () {
        // Speed
        var chart = $('#widgetContainer').highcharts(),
            point,
            newVal,
            inc;

        if (chart) {
            point = chart.series[0].points[0];
            inc = 80;
            newVal = point.y + inc;


            point.update(newVal);
        }

    }, 300);
    */
   
   
    }

});