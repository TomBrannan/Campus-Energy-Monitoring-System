<%-- 
    Document   : graphs
    Created on : Feb 16, 2015, 2:16:52 PM
    Author     : sjf14670
--%>
<%--<map name="testmap">
  <area shape="rect" coords="0,0,82,126" alt="">
</map>--%>


<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- Used if the JSTL 1.2 JAR is used, rather than the JSTL 1.2.2 impl & api libraries --%>
        <title>Green Power: Graphs</title>
        <link rel="stylesheet" type="text/css" href="GreenStyle.css">
        <%@ page isELIgnored ="false" %>
        <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script src="javascript_scripts/graphImports.js"></script>
        <script src="javascript_scripts/mainGraph.js">//Loads the graph with default values</script>
        <script src="javascript_scripts/widget.js">//Loads the graph with default values</script>
        <script src="javascript_scripts/datePicker.js"></script>

        <%-- date time picker will go here --%>

        <!--<script type="text/javascript">
                    $.post("/Lucid/GreenTipServlet", {"page":"graphs.jsp"}, function (tip) {
                    $("#greenTipDiv").html(tip);
                    });</script>-->

        <script type="text/javascript">
            function fade() {
                $("#greenTipDiv").fadeOut(500, function () {
                    var tipId = $("#greenTipDiv").val();
                    $.post("/Lucid/GreenTipServlet", {"action": "getRandTip", "currentTip": tipId}, function (jsonString) {
                        var htmlString = "<img src=\"ImageServlet?currentTip="+jsonString.tipId+"\" alt=\"Image Not Found.\"><br>";
                        htmlString += jsonString.tipMsg;
                        htmlString += "<br>";
                        $("#greenTipDiv").html(htmlString);
                        $("#greenTipDiv").val(jsonString.tipId);
                    });
                    $("#greenTipDiv").fadeIn(500, fade()).delay(15000);
                });
            }
            $(document).ready(function () {
                fade();
            });
        </script>
        <script>
            $.get('Footer.html', {}, function (response) {
                $('div#footer').append(response);
            });
        </script>
        
    </head>

    <body class="background" onload="checkLogin($('#user').val(), 'graphs');">
        <input type="hidden" id="user" value="${user}"/>
        <div class="wrapper"><audio src="Images/bark2.wav" id="sound" preload="auto"></audio>
            <br>
            <center>
                <table class="padMe">
                    <tr id="buffer"></tr>
                    <tr id="buffer"></tr>
                    <tr>
                        <td class="tables2">
                            <div class="boxxyLogo">
                                <img src="Images/HuskiesLogo2-2.png" alt="Image not found." ondrag="playMe()" class="logo">
                                <br>
                            </div><br><br><br>
                            <div class="basic" id="links">
                                <a class="buttons" href="reports.jsp">Download Data</a><br><br>
                                <a class="buttons" href="loginScreen.jsp">Admin Login</a><br><br>
                            </div>
                        </td>
                        <td id="buffer"></td>
                        <td colspan="3" id="tabs">
                            <div>
                                <ul>
                                    <li id="powerLink" class="selected">
                                        <a href="#power" onclick="power();" value="power" class="tabs">Power</a>
                                    </li>
                                    <li id="energyLink" class="unselected">
                                        <a href="#energy" onclick="energy();" value="energy" class="tabs">Energy</a>
                                    </li>
                                </ul>
                            </div>
                            <div id="content">
                                <div id="powerContent" style="display: inline">
                                    <div class="boxxyGraph">
                                        <div id="graphContainer" class="graph"></div>
                                    </div>
                                </div>
                                <div id="energyContent" style="display: none">
                                    <div class="boxxyGraph">
                                        <div id="barGraphContainer" class="graph">Energy Bar Graph Goes Here</div>
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td class="boxxy3"></td>
                    </tr>
                    <tr id="buffer"></tr>
                    <tr> 
                        <td></td>
                        <td id="buffer"></td>
                        <td class="tables6">
                    <center><div id="widgetContainer"></div></center>
                    </td>
                    <td id="buffer"></td>
                    <td class="tables4">
                        <div class="greene">Greene Tips</div>
                        <div class="basic2" id="greenTipDiv" name="greenTipDiv" value="10">
                        </div>
                    </td>
                    </tr>
                </table></center>
            <br><br><br></div>
        <div id="footer" id="footer"></div>
    </body>
</html>
