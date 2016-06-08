<!DOCTYPE html>
<!--
This is the Building Settings Page
-->
<html>
    <head>
        <title>Green Power: Edit Building</title>
        <script src="http://code.jquery.com/jquery-latest.min.js"></script>
        <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js" type="text/javascript"></script>
        <script src="javascript_scripts/buildingScripts.js"></script>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="GreenStyle.css">
        <script src="javascript_scripts/datePicker.js"></script>
        <script type="text/javascript">
            $.post("/Lucid/BuildingServlet", {"action": "getAllBuildings"}, function (jsonString) {
                var htmlString = "<option value = \"-1\">Add New Building...</option>";
                for (i = 0; i < jsonString.buildings.length; i++) {
                    htmlString += "<option value = \"" + jsonString.buildings[i].buildingId + "\">" + jsonString.buildings[i].buildingName + "</option>";
                }
                $("#buildings").html(htmlString);
                if (jsonString.status === "ADDED") {
                    alert("Building has been added.");
                } else if (jsonString.status === "DELETED") {
                    alert("Building has been deleted.");
                } else if (jsonString.status === "MODIFIED") {
                    alert("Building has been modified.");
                } else if (jsonString.status === "FAILED") {
                    alert("BUILDING MODIFICATION HAS FAILED");
                }
            });
            $("#otherMeters").html("");
            htmlString = "";
            $.post("/Lucid/MeterServlet", {"action": "getOtherMeters"}, function (jsonString) {
                for (i = 0; i < jsonString.otherMeters.length; i++) {
                    htmlString += "<option value = \"" + jsonString.otherMeters[i].meterId + "\">" + jsonString.otherMeters[i].meterName + "</option>";
                }
                $("#otherMeters").html(htmlString);
            });
        </script>
        <script>
            $.get('Footer.html', {}, function (response) {
                $('div#footer').append(response);
            });
        </script>


    </head>
    <body class="background">
        <div class="wrapper">
            <audio src="Images/bark2.wav" id="sound" preload="auto"></audio>
            <br>
            <center>
                <table class="padMe">
                    <tr id="buffer"></tr>
                    <tr id="buffer"></tr>
                    <tr>
                        <td class="tables2">
                            <div class="boxxyLogo">
                                <img src="Images/HuskiesLogo2-2.png" alt="Image not found." ondrag="playMe()" class="logo">
                            </div><br><br><br>
                            <div class="basic">
                                <a class="buttons" href="graphs.jsp">Graphs Page</a><br><br>
                                <a class="buttons" href="reports.jsp">Download Data</a><br><br>
                                <a class="buttons" href="tip.jsp">Tip Settings</a><br><br>
                                <a class='buttons' href='admin.jsp'>Admin Options</a><br><br>
                            </div>
                        </td>
                        <td id="buffer"></td>
                        <td class="tables">
                            <div class="titles">Choose Building: </div>
                            <form class="basic" method="post" action="BuildingServlet">
                                <select class="basic" id="buildings" name="buildings" onchange="changeBuilding();">
                                    <!--pulling buildings from database-->
                                </select>
                                <br><br>         
                                <div class="titles2">
                                    <input type="hidden" name="action" value="modifyBuildings"/>
                                    Name<input type="text" class="basic3" id="name" name="name" value=""><br>
                                    Campus<select class="basic6" id="campus" name="campus" value="">
                                        <option value ="1">Lower Campus</option>
                                        <option value ="2">Upper Campus</option>
                                    </select><br>
                                    Square Footage<input type="text" class="basic3" id="sqft" name="sqft" value=""><br>
                                    Occupancy<input type="text" class="basic3" id="occ" name="occ" value=""><br>
                                </div>
                                <div class=titles>Visible<input type="checkbox" id="visible" name="visible" value="visible"></div>
                                <br><br>
                                <table>
                                    <tr>
                                        <td>
                                            <span class="titles2">Building's Meters</span><br>
                                            <select class="selector2" id="buildingMeters" name="meters2" size="5">
                                                <!--puling meters from DB-->
                                            </select>
                                        </td>
                                        <td>
                                            <span class="titles2">Available Meters</span><br>
                                            <select class="selector2" id="otherMeters" name="meters3" size="5">
                                                <!--puling meters from DB-->
                                            </select><br>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td>
                                            <div class="basic"><button type="button" class="buttons2" onclick="removeMeter();">Remove ></button></div>
                                        </td>
                                        <td>
                                            <div class="basic"><button type="button" class="buttons2" onclick="addMeter();">< Add</button><br></div>
                                        </td>
                                    </tr>
                                </table>
                                <br>
                                <div class="basic">
                                    <br>
                                    <input type="submit" class="buttons2" name="buttonName" value="Add/Modify Building" onclick="return confirmAddModify();">
                                    <input type="submit" class="buttons2" name="buttonName" value="Delete Building" onclick="return confirmDelete();"></div><br>
                                </td>
                                <td class="boxxy3"></td>
                    </tr>
                    <tr>
                        <td></td>
                        <td></td>
                        <td>

                            </form>
                        </td>
                    </tr>
                </table><br><br><br></div>
        <div class="footer" id="footer"></div>
    </center>
</body>
</html>
