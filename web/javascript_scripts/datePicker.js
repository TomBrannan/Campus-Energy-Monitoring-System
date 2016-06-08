

function showDays()
{
    var m = document.getElementById("month1").value;
    if (m == 2)
    {
        document.getElementById("day1_3").style.display = "none";
        document.getElementById("day1_2").style.display = "none";
        document.getElementById("day1").style.display = "inline";
    }
    else if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
    {
        document.getElementById("day1").style.display = "none";
        document.getElementById("day1_2").style.display = "none";
        document.getElementById("day1_3").style.display = "inline";
    }
    else
    {
        document.getElementById("day1").style.display = "none";
        document.getElementById("day1_3").style.display = "none";
        document.getElementById("day1_2").style.display = "inline";
    }
}
function showDays2()
{
    var m = document.getElementById("month1").value;
    if (m == 2)
    {
        document.getElementById("day2_3").style.display = "none";
        document.getElementById("day2_2").style.display = "none";
        document.getElementById("day2").style.display = "inline";
    }
    else if (m == 1 || m == 3 || m == 5 || m == 7 || m == 8 || m == 10 || m == 12)
    {
        document.getElementById("day2").style.display = "none";
        document.getElementById("day2_2").style.display = "none";
        document.getElementById("day2_3").style.display = "inline";
    }
    else
    {
        document.getElementById("day2").style.display = "none";
        document.getElementById("day2_3").style.display = "none";
        document.getElementById("day2_2").style.display = "inline";
    }
}

function playMe()
{
    document.getElementById('sound').play();
}

function clearContents(element)
{
    element.value = '';
}

function getFullText(e)
{
    document.getElementById("tipText").innerHTML = e.innerHTML;
}

function checkLogin(user, page)
{
    var userString = user;

    if (userString !== null && userString !== "") {
        if (page === "graphs") {
            $("#links").html("<a class='buttons' href='reports.jsp'>Download Data</a><br><br>"
                    + "<a class='buttons' href='building.jsp'>Building Settings</a><br><br>"
                    + "<a class='buttons' href='tip.jsp'>Tip Settings</a><br><br>"
                    + "<a class='buttons' href='admin.jsp'>Admin Options</a><br><br>");
        }
        else if (page === "reports") {
            $("#links").html("<a class='buttons' href='graphs.jsp'>Graphs Page</a><br><br>"
                    + "<a class='buttons' href='building.jsp'>Building Settings</a><br><br>"
                    + "<a class='buttons' href='tip.jsp'>Tip Settings</a><br><br>"
                    + "<a class='buttons' href='admin.jsp'>Admin Options</a><br><br>");
        }
    }
}

function power()
{
    document.getElementById("powerContent").style.display = "inline";
    document.getElementById("energyContent").style.display = "none";

    document.getElementById("powerLink").className = "selected";
    document.getElementById("energyLink").className = "unselected";
}
function energy()
{
    document.getElementById("powerContent").style.display = "none";
    document.getElementById("energyContent").style.display = "inline";

    document.getElementById("powerLink").className = "unselected";
    document.getElementById("energyLink").className = "selected";
}
