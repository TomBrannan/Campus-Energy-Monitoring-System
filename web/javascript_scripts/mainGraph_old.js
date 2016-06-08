var chart;
var numBuildings = 2;
var MAX_BUILDINGS = 8; //Maximum allowed buildings to be shown at once
var TOOLTIP_MAX_BUILDINGS = 2; //Maximum number of buildings to show on tooltip hover
var LEGEND_ITEM_HEIGHT = 28;
var LEGEND_TITLE_HEIGHT = 44;
var TOOLTIP_ITEM_HEIGHT = 22;
var TOOLTIP_TITLE_HEIGHT = 41;
var CHART_HEIGHT = 600;
var CHART_WIDTH = 800;
var CHART_RIGHT_SPACING = 120;
var CHART_BUFFER = 40;
var DECIMAL_PLACES = 2;
var resolution = 4; //Multiples of 15 minutes; 
// 1 = 15 mins
// 4 = 1 hour
// 24 = 6 hours
// 96 = 1 day
$(function () {
    var container = document.getElementById("graphContainer");

    chart = new Highcharts.Chart({
        chart: {
            renderTo: container,
            height: CHART_HEIGHT,
            spacingRight: CHART_RIGHT_SPACING
        },
        tooltip: {
            shared: true,
            useHTML: true,
            crosshairs: true,
            positioner: function (labelWidth, labelHeight, point)
            {
                var tooltipX, tooltipY;

                //Tooltip for mouse hover
                if (numBuildings <= TOOLTIP_MAX_BUILDINGS)
                {
                    if (point.plotX + labelWidth > chart.plotWidth) {
                        tooltipX = point.plotX + chart.plotLeft - labelWidth - 20;
                    } else {
                        tooltipX = point.plotX + chart.plotLeft + 20;
                    }
                    tooltipY = point.plotY + chart.plotTop - 20;
                }

                //Tooltip for fixed box:
                else
                {
                    /*
                     * 300: -135
                     400: -35
                     500: 65
                     600: 165 ??
                     700: 165
                     800: 165
                     */
                    tooltipX = CHART_WIDTH - 165;

                    var chartHeight = CHART_HEIGHT + CHART_BUFFER;
                    var legendHeight = LEGEND_TITLE_HEIGHT + LEGEND_ITEM_HEIGHT * numBuildings;
                    var toolTipHeight = TOOLTIP_TITLE_HEIGHT + TOOLTIP_ITEM_HEIGHT * numBuildings;
                    var spacing = 25;
                    tooltipY = (chartHeight - (legendHeight + spacing + toolTipHeight)) / 2 + legendHeight + spacing;
                }

                return {
                    x: tooltipX,
                    y: tooltipY
                };
            },
            formatter: function ()
            {
                var tooltip_html = "";
                var date = new Date(this.x);
                function get_nth_suffix(date) {
                    switch (date) {
                        case 1:
                        case 21:
                        case 31:
                            return 'st';
                        case 2:
                        case 22:
                            return 'nd';
                        case 3:
                        case 23:
                            return 'rd';
                        default:
                            return 'th';
                    }
                }
                var monthNames = ["January", "February", "March", "April", "May", "June",
                    "July", "August", "September", "October", "November", "December"];

                function formatAMPM(date) {
                    var hours = date.getHours();
                    var minutes = date.getMinutes();
                    var ampm = hours >= 12 ? 'PM' : 'AM';
                    hours = hours % 12;
                    hours = hours ? hours : 12; // the hour '0' should be '12'
                    minutes = minutes < 10 ? '0' + minutes : minutes;
                    var strTime = hours + ':' + minutes + ' ' + ampm;
                    return strTime;
                }

                date.setHours(date.getHours() + 4); //Correct for EST time zone!
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
            text: 'Power Usage Over Time (per building)'
        },
        subtitle: {
            //text: 'This will be real data soon!'
        }, yAxis: [{// left y axis

                title: {
                    text: 'Power (kilowatts)'
                },
                labels: {
                    format: '{value:.,0f}'
                },
                showFirstLabel: false
            }, {// right y axis
                linkedTo: 0,
                gridLineWidth: 0,
                opposite: true,
                title: {
                    text: null
                },
                labels: {
                    format: '{value:.,0f}'
                },
                showFirstLabel: false
            }],
        legend: {
            visible: false,
            title: {
                text: '<span style="font-size: 16px; color: #010101; font-weight: normal">Buildings</span>'
                        + '<br/><span style="font-size: 11px; color: #777777; font-weight: normal; font-style: italic;">(Click to hide)</span>'
            },
            align: 'right',
            verticalAlign: 'top',
            layout: 'vertical', x: 60, y: ((CHART_HEIGHT + CHART_BUFFER) - ((LEGEND_TITLE_HEIGHT + LEGEND_ITEM_HEIGHT * numBuildings)
                    + 25 + (TOOLTIP_TITLE_HEIGHT + TOOLTIP_ITEM_HEIGHT * numBuildings))) / 2,
            padding: 3,
            itemMarginTop: 5,
            itemMarginBottom: 5,
            itemStyle: {
                lineHeight: '14px'
            }
        }, xAxis: {
            title: {
                /* text: 'Time' //Unnecessary?  (It's implied) */
            },
            type: 'datetime',
            tickInterval: 900000 * resolution,
            dateTimeLabelFormats: {
                month: '%b %e, %Y'
            },
            labels: {
                formatter: function () {

                    var date = new Date(this.value);
                    function formatAMPM(date) {
                        var hours = date.getHours();
                        var minutes = date.getMinutes();
                        var ampm = hours >= 12 ? 'PM' : 'AM';
                        hours = hours % 12;
                        hours = hours ? hours : 12; // the hour '0' should be '12'
                        minutes = minutes < 10 ? '0' + minutes : minutes;
                        var strTime = hours + ':' + minutes + ' ' + ampm;
                        return strTime;
                    }

                    date.setHours(date.getHours() + 4); //Correct for EST time zone!
                    return formatAMPM(date);
                },
                align: 'center'
            }
        },
        plotOptions: {//Uncomment this (and imports) if clickable points are desired
            line: {
                events: {
                    legendItemClick: function () {
                        numBuildings += this.visible ? -1 : 1;
                    }
                }
            },
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
                                headingText: this.series.name,
                                maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) + ':<br/> ' +
                                        this.y + ' visits',
                                width: 200
                            });
                        }
                    }
                },
                marker: {
                    lineWidth: 1
                }
            }
        },
        series: [
            {
                name: 'Columbia',
                pointInterval: 900000 * resolution,
                pointStart: Date.UTC(2015, 2, 18, 0, 0, 0, 0)
            },
            {
                name: 'Elwell',
                pointInterval: 900000 * resolution,
                pointStart: Date.UTC(2015, 2, 18, 0, 0, 0, 0)
            }
        ]
                /*},
                 function(){
                 var legWidth = this.legend.maxItemWidth;
                 $.each(this.legend.title.element.children[0].children, function(i,j){
                 j = $(j);
                 var spanWidth = j.width();
                 j.attr('dx', (legWidth / 2) - (spanWidth / 2));
                 });*/
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
            var names = [];     //The names for each series
            var newData = [];   //Multi-array for all points

           title = jsonString.title;

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
                chart.series[i].update({name: names[i]}, false);
                chart.series[i].setData(newData[i], true);
            }
            
            
            /*Title field may be deleted at some point!*/
            //chart.setTitle({text: title});
            //chart.xAxis[0].setExtremes(new Date().getTime(), new Date().setHours(new Date().getHours()+1));
        });
    });
});