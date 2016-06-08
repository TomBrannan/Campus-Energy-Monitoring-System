var chart;
var TOOLTIP_MAX_BUILDINGS = 2; //Maximum number of buildings to show on tooltip hover
var CHART_HEIGHT = 600;
var CHART_WIDTH = 800;
var DECIMAL_PLACES = 2;
var CHART_SPACING_RIGHT = 85; //Should be changed to be calculated before rendering the graph
var HOUR_INTERVAL = 2; //hours between tick marks on x-axis

var resolution = 1; //Multiples of 15 minutes; 
// 1 = 15 mins
// 4 = 1 hour
// 24 = 6 hours
// 96 = 1 day

function getTimeInterval(numHours)
{
    return 900000 * 4 * numHours;
}

$(function () {
    var container = document.getElementById("graphContainer");

    chart = new Highcharts.Chart({
        chart: {
            renderTo: container,
            spacingRight: CHART_SPACING_RIGHT,
            height: CHART_HEIGHT,
            backgroundColor: null
        },
        tooltip: {
            shared: true,
            useHTML: true,
            crosshairs: true,
            positioner: function (labelWidth, labelHeight, point)
            {
                var tooltipX, tooltipY;

                if (point.plotX + labelWidth > chart.plotWidth) {
                    tooltipX = point.plotX + chart.plotLeft - labelWidth - 20;
                } else {
                    tooltipX = point.plotX + chart.plotLeft + 20;
                }
                tooltipY = point.plotY + chart.plotTop - 20;

                return {
                    x: tooltipX,
                    y: tooltipY
                };
            },
            formatter: function ()
            {
                var tooltip_html = "";
                var date = new Date(this.x);

                tooltip_html += '<table><tr><td>';
                tooltip_html += monthNames[date.getMonth()] + ' ' + date.getDate() + '' + get_nth_suffix(date.getDate()) + ', ' + date.getFullYear();
                tooltip_html += '</td><td>';
                tooltip_html += formatAMPM(date);
                tooltip_html += '</td></tr>';

                this.points.forEach(function (entry)
                {
                    tooltip_html += '<tr><td style="font-weight:bold; color:' + entry.series.color + '">' + entry.series.name + ':</td><td style="text-align: right"> ' + entry.y.toFixed(DECIMAL_PLACES) + '</td></tr>';
                });

                tooltip_html += "</table>";

                return tooltip_html;
            }
        }, title: {
            text: '' //default is empty string
        },
        yAxis: [{// left y axis

                title: {
                    text: 'Power (kilowatts)'
                },
                labels: {
                    format: '{value:.,0f}'
                },
                showFirstLabel: false

            }],
        legend: {
            title: {
                text: '<span style="font-size: 16px; color: #010101; font-weight: normal">Buildings</span>'
                        + '<br/><span style="font-size: 11px; color: #777777; font-weight: normal; font-style: italic;">(Click to show/hide)</span>'
            },
            align: 'right',
            verticalAlign: 'top',
            layout: 'vertical', x: 60, y: 50,
            padding: 3,
            itemMarginTop: 5,
            itemMarginBottom: 5,
            itemStyle: {
                lineHeight: '14px'
            }
        }, xAxis: {
            type: 'datetime',
            title: {
            },
            tickInterval: getTimeInterval(HOUR_INTERVAL),
            dateTimeLabelFormats: {
                month: '%b %e, %Y'
            },
            labels: {
                formatter: function () {
                    var date = new Date(this.value);
                    return formatAMPM(date);
                },
                align: 'center'
            }

        },
        plotOptions: {//Uncomment this (and imports) if clickable points are desired

            series: {
                cursor: 'pointer',
                point: {
                    events: {
                        click: function (e) {
                            hs.htmlExpand(null, {
                                pageOrigin: {
                                    x: e.pageX || e.clientX,
                                    y: e.pageY || e.clientY
                                },
                                headingText: this.series.name,/*%A, %b %e, %Y*/
                                maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', new Date(this.x)) + '<br/> ' + // + HighCharts.dateFormat('%l:%M %p', this.x) + 
                                        addCommas(this.y) + ' kilowatts',
                                width: 200
                            });
                        }
                    }
                },
                marker: {
                    lineWidth: 1
                }
            }
        }
    });

    //Loads the graph with default data as soon as the document has finished loading
    $(document).ready(function () {
        var buildings = '[';

        //var selection = document.getElementById("selected_buildings");
        /*
         for (var i = 0; i < selection.options.length; i++) {
         if (selection.options[i].selected === true) {
         buildings += selection.options[i].value + ', ';
         }
         }
         if (buildings.length > 1)
         {
         buildings = buildings.substring(0, buildings.length - 2);
         }
         buildings += ']';
         */
        //var startDate = document.getElementById("dateTimeFrom").value;
        //var endDate = document.getElementById("dateTimeTo").value;
        var selection = "";
        var startDate = "";
        var endDate = "";

        $.post("/Lucid/fetchDataServlet", {"buildings": buildings, "startDate": startDate, "endDate": endDate}, function (jsonString) {

            var title = "";     //The title of the graph
            var kilowatts = false;
            var names = [];     //The names for each series
            var newData = [];   //Multi-array for all points

            var firstDate = Date.parse(jsonString.firstDate);

            title = jsonString.title;
            chart.setTitle({text: title});
            var res = jsonString.resolution;
            kilowatts = jsonString.kilowatts;

            resolution = res;

            //Load all series names
            for (i = 0; i < jsonString.series.length; i++)
            {
                names.push(jsonString.series[i].name);
            }

            //Load all series points
            for (i = 0; i < jsonString.series.length; i++) {
                newData[i] = [];
                for (j = 0; j < jsonString.series[i].points.length; j++)
                {
                    newData[i].push(jsonString.series[i].points[j]);
                }
            }

            //Load all series data (& names) into the chart, update
            for (i = 0; i < newData.length; i++)
            {
                chart.addSeries({
                    name: names[i],
                    type: 'spline',
                    pointInterval: 900000 * resolution,
                    pointStart: firstDate,
                    data: newData[i],
                    visible: true
                });
            }

        });


    });
});