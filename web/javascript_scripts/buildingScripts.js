/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

function changeBuilding() {
    var buildings = $("#buildings :selected").val();
    $("#name").val("");
    $("#campus").val("");
    $("#sqft").val("");
    $("#occ").val("");
    $("#visible")[0].checked = false;
    $.post("/Lucid/BuildingServlet", {"buildings": buildings, "action": "getBuilding"}, function (jsonString) {
        $("#name").val(jsonString.building.buildingName);
        $("#campus").val(jsonString.building.campusName);
        if (jsonString.building.sqFt === "-1") {
            $("#sqft").val("");
        } else {
            $("#sqft").val(jsonString.building.sqFt);
        }
        if (jsonString.building.occupancy === "-1") {
            $("#occ").val("");
        } else {
            $("#occ").val(jsonString.building.occupancy);
        }
        if (jsonString.building.visible === "true") {
            $("#visible")[0].checked = true;
        } else {
            $("#visible")[0].checked = false;
        }
    });

    $.post("/Lucid/MeterServlet", {"building": buildings, "action": "getBuildingMeters"}, function (jsonString) {
        var htmlString = "";
        $("#buildingMeters").html("");
        for (i = 0; i < jsonString.buildingMeters.length; i++) {
            htmlString += "<option value = \"" + jsonString.buildingMeters[i].meterId + "\">" + jsonString.buildingMeters[i].meterName + "</option>";
        }
        $("#buildingMeters").html(htmlString);
    });
}

function removeMeter() {
    var meterId = $("#buildingMeters :selected").val();
    $.post("/Lucid/MeterServlet", {"meter": meterId, "action": "removeMeter"}, function (successString) {
        if (successString === "true") {
            alert("Meter was removed successfully.");
        } else {
            alert("Meter was not removed successfully.")
        }
    });
    changeBuilding();
    $("#otherMeters").html("");
    htmlString = "";
    $.post("/Lucid/MeterServlet", {"action": "getOtherMeters"}, function (jsonString) {
        for (i = 0; i < jsonString.otherMeters.length; i++) {
            htmlString += "<option value = \"" + jsonString.otherMeters[i].meterId + "\">" + jsonString.otherMeters[i].meterName + "</option>";
        }
        $("#otherMeters").html(htmlString);
    });
}

function addMeter() {
    var meterId = $("#otherMeters :selected").val();
    var buildings = $("#buildings :selected").val();
    $.post("/Lucid/MeterServlet", {"meter": meterId, "buildings": buildings, "action": "addMeter"}, function (successString) {
        if (successString === "true") {
            alert("Meter was removed successfully.");
        } else if(buildings==="-1"){
            alert("Please save building before adding meters.");
        } else {
            alert("Meter was not removed successfully.");
        }
    });
    changeBuilding();
    $("#otherMeters").html("");
    htmlString = "";
    $.post("/Lucid/MeterServlet", {"action": "getOtherMeters"}, function (jsonString) {
        for (i = 0; i < jsonString.otherMeters.length; i++) {
            htmlString += "<option value = \"" + jsonString.otherMeters[i].meterId + "\">" + jsonString.otherMeters[i].meterName + "</option>";
        }
        $("#otherMeters").html(htmlString);
    });
}

function confirmAddModify(){
    var building = $("#buildings :selected").val();
    if(building === "-1"){
        return confirm("Are you sure you want to add "+ $("#name").val() +"?");
    }else{
        return confirm("Are you sure you want to edit " +$("#name").val() +"?");
    }
}

function confirmDelete(){
    return confirm("Are you sure you want to delete "+$("#name").val()+"?");
}