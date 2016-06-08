<!DOCTYPE html>
<html>
    <head>
        <title>Dynamic graph data test</title>

        <!-- JSP needs this to ACTUALLY use utf-8 -->
        <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

        <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        
        <script src="javascript_scripts/graphImports.js"></script>

        <!-- DateTimePicker stuff -->
        <link href=" http://cdn.syncfusion.com/js/web/flat-azure/ej.web.all-latest.min.css" rel="stylesheet" />
        <script src="http://ajax.aspnetcdn.com/ajax/globalize/0.1.1/globalize.min.js"></script>
        <script src="http://cdnjs.cloudflare.com/ajax/libs/jquery-easing/1.3/jquery.easing.min.js"></script>
        <script src="http://cdn.syncfusion.com/js/web/ej.web.all-latest.min.js"></script>

        <script src="javascript_scripts/mainGraph.js"></script>
        
        <script>
            $(function () {
                $('#dateTimeFrom').ejDateTimePicker({width: '180px', value: new Date()});
                $('#dateTimeTo').ejDateTimePicker({width: '180px', value: new Date()});
            });  
        </script>

    </head>
    <body>
        <br/>
        Select a building: <select id="selected_buildings" name="building_selection" multiple="multiple">
            <option value="Hartline">Hartline</option>
            <option value="Bakeless">Bakeless</option>
            <option value="McCormick">McCormick</option>
            <option value="Sutliff">Sutliff</option>
        </select>

        <br/><br/><br/>
        From: <input type="text" id="dateTimeFrom" />
        To: <input type="text" id="dateTimeTo" />

        <br/>

        <button id="updateGraphButton">Fetch data</button>

        <br/><br/><br/>

        <div id="graphContainer"></div>



    </body>
</html>