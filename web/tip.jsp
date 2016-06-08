<!DOCTYPE html>
<!--
This is the tip settings page.
-->
<html>
    <head>
        <title>Green Power: Green-Tip Settings</title>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <link rel="stylesheet" type="text/css" href="GreenStyle.css">

        <script src="http://code.jquery.com/jquery-latest.min.js"></script>

        <script src="javascript_scripts/datePicker.js"></script>

        <script type="text/javascript">
            $.post("/Lucid/GreenTipServlet", {"action": "getAllTips"}, function (jsonString) {
                var htmlString = "";
                for (i = 0; i < jsonString.tips.length; i++) {
                    htmlString += "<option value = \"" + jsonString.tips[i].tipId + "\" onclick=\"getFullText(this)\">" + jsonString.tips[i].tipMsg + "</option>";
                }
                $("#tips").html(htmlString);
                if (jsonString.status === "ADDED") {
                    alert("Tip has been added.");
                } else if (jsonString.status === "DELETED") {
                    alert("Tip has been deleted.");
                }else if (jsonString.status === "FAILED") {
                    alert("TIP CHANGE HAS FAILED");
                }
            });
        </script>
        
        <script>
            $.get('Footer.html', {}, function(response) { 
                $('div#footer').append(response);
            });
        </script>
    </head>
    <body class="background">
    <div class="wrapper"><center>
        <audio src="Images/bark2.wav" id="sound" preload="auto"></audio>
        <br>
            <table class="padMe">
            <tr id="buffer"></tr>
            <tr id="buffer"></tr>
            <tr>
                <td class="tables2"><center>
                    <div class="boxxyLogo">
                        <img src="Images/HuskiesLogo2-2.png" alt="Image not found." ondrag="playMe()" class="logo">
                    </div><br><br><br>
                    <div class="basic">
                        <a class="buttons" href="graphs.jsp">Graphs Page</a><br><br>
                        <a class="buttons" href="reports.jsp">Download Data</a><br><br>
                        <a class="buttons" href="building.jsp">Building Settings</a><br><br>
                        <a class='buttons' href='admin.jsp'>Admin Options</a><br><br>
                    </div>
            </center></td>
                <td id="buffer"></td>
                <form method="post" action="GreenTipServlet">
                <input type="hidden" name="action" value="addTip"/>
                <input type="hidden" name="added" value="false"/>
                 <td class="tables">
                    <div>
                        <div class="titles">Add Tips<br></div>
                        <div class="basic">
                            <textarea name="tipMessage"class="basic4" maxlength="160" onfocus="clearContents(this);">Enter your tip here!</textarea><br>Choose your tip's photo:<br>
                                <input type="radio" name="tippic" value="10"><img src="Images/GreeneTipsTree.png" class="resize" alt="Image not found.">
                                <input type="radio" name="tippic" value="7"><img src="Images/GreeneTipsBulb.png" class="resize" alt="Image not found.">
                                <br><input type="radio" name="tippic" value="9"><img src="Images/GreeneTipsWater.png" class="resize" alt="Image not found.">
                                <input type="radio" name="tippic" value="8"><img src="Images/GreeneTipsRecycle.png" class="resize" alt="Image not found."><br>
                        </div>
                        <br>
                        <div class="basic"><input type="submit" class="buttons2" value="Submit" ></div><br>
                    </div>
                    <br>
                </td>
                </form>
                <td class="buffer"></td><td class="buffer"></td>
                <form method="post" action="GreenTipServlet">
                <input type="hidden" name="action" value="deleteTip"/>
                <input type="hidden" name="deleted" value="false"/>
                    <td class="tables5">
                        <div>
                            <div class="titles">Delete Tips</div><br>
                            <select class="selector3" id="tips" name="tips" size="10">
                                <!--populated from database-->
                                <option value="test" id="test" onclick="getFullText(this)">TEST This is a very big test! Do you see all of this text here? There is a lot of it.</option>
                            </select>
                            <br><br>
                        <div class="basic">
                            <center><div class="basic7" id="tipText"></div></center>
                            <br><br><br><button class="buttons2" type="submit" onclick="return confirm('Are you sure?');">Delete</button></div><br>
                        </div>
                    </td>
                </form>
                <td class="boxxy3"></td>
            </tr>            
            </table><br><br><br></div>
            <div class="footer" id="footer"></div>
    </center>
</body>
</html>
