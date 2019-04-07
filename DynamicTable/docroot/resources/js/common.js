var myApp = 'http://localhost:8090/IFMS/';
var projectName = "/BSMProject";

function addCommas(component)
{
    var value = document.getElementById(component).value;
    value = removCommas(value.toString());
    var sRegExp = new RegExp('(-?[0-9]+)([0-9]{3})');
    while (sRegExp.test(value))
        value = value.replace(sRegExp, '$1,$2');
    document.getElementById(component).value = value;
    checkDigitComma(component);
}

function removCommas(value)
{
    var realVal = "";
    for (i = 0; i < value.length; i++)
    {
        if (value.substr(i, 1) != ',')
            realVal = realVal + value.substr(i, 1);
    }
    return realVal;
}

function checkDigit(component)
{
    var value = document.getElementById(component).value;
    var validValue = "";
    for (i = 0; i < value.length; i++)
    {
        if (isDigit(value.charAt(i)))
            validValue += value.charAt(i);
    }
    document.getElementById(component).value = validValue;
}

function checkDigitComma(component)
{
    var value = document.getElementById(component).value;
    var validValue = "";
    for (i = 0; i < value.length; i++)
    {
        if (isDigitComma(value.charAt(i)))
            validValue += value.charAt(i);
    }
    document.getElementById(component).value = validValue;
}

function isDigitComma(val)
{
    if (val == ',') return true;
    return isDigit(val);
}

function isDigit(val)
{
    if (val == '0')
        return true;
    if (val == '1')
        return true;
    if (val == '2')
        return true;
    if (val == '3')
        return true;
    if (val == '4')
        return true;
    if (val == '5')
        return true;
    if (val == '6')
        return true;
    if (val == '7')
        return true;
    if (val == '8')
        return true;
    if (val == '9')
        return true;
    return false;
}

function checkLen(formName, component, val)
{
    if (component.maxLength == val.length)
    {
        if (component.id == formName + ':acc1')
            document.getElementById(formName + ":acc2").focus();
        else
        if (component.id == formName + ':acc2')
            document.getElementById(formName + ":acc3").focus();
        else
        if (component.id == formName + ':acc3')
            document.getElementById(formName + ":acc4").focus();
    }
}

//function checkLen(form, id)
//{
//    alert(form.id)  ;
//    if (id == 'acc1')
//        document.getElementById(form + ".acc2").focus();
//    else
//    if (id == 'acc2')
//        document.getElementById(form + ".acc3").focus();
//    else
//    if (id == 'acc3')
//        document.getElementById(form + ".acc4").focus();
//}

function shortcut(shortcut, callback, opt)
{
    //Provide a set of default options
    var default_options =
    {
        'type':'keydown',
        'propagate':false,
        'target':document
    }
    if (!opt) opt = default_options;
    else
    {
        for (var dfo in default_options)
        {
            if (typeof opt[dfo] == 'undefined') opt[dfo] = default_options[dfo];
        }
    }

    var ele = opt.target
    if (typeof opt.target == 'string') ele = document.getElementById(opt.target);
    var ths = this;

    //The function to be called at keypress
    var func = function (e)
    {
        e = e || window.event;

        //Find Which key is pressed
        if (e.keyCode) code = e.keyCode;
        else if (e.which) code = e.which;
        var character = String.fromCharCode(code).toLowerCase();

        var keys = shortcut.toLowerCase().split("+");
        //Key Pressed - counts the number of valid keypresses - if it is same as the number of keys, the shortcut function is invoked
        var kp = 0;

        //Work around for stupid Shift key bug created by using lowercase - as a result the shift+num combination was broken
        var shift_nums =
        {
            "`":"~",
            "1":"!",
            "2":"@",
            "3":"#",
            "4":"$",
            "5":"%",
            "6":"^",
            "7":"&",
            "8":"*",
            "9":"(",
            "0":")",
            "-":"_",
            "=":"+",
            ";":":",
            "'":"\"",
            ",":"<",
            ".":">",
            "/":"?",
            "\\":"|"
        }

        //Special Keys - and their codes
        var special_keys =
        {
            'esc':27,
            'escape':27,
            'tab':9,
            'space':32,
            'return':13,
            'enter':13,
            'backspace':8,

            'scrolllock':145,
            'scroll_lock':145,
            'scroll':145,
            'capslock':20,
            'caps_lock':20,
            'caps':20,
            'numlock':144,
            'num_lock':144,
            'num':144,

            'pause':19,
            'break':19,

            'insert':45,
            'home':36,
            'delete':46,
            'end':35,

            'pageup':33,
            'page_up':33,
            'pu':33,

            'pagedown':34,
            'page_down':34,
            'pd':34,

            'left':37,
            'up':38,
            'right':39,
            'down':40,

            'f1':112,
            'f2':113,
            'f3':114,
            'f4':115,
            'f5':116,
            'f6':117,
            'f7':118,
            'f8':119,
            'f9':120,
            'f10':121,
            'f11':122,
            'f12':123
        }


        for (var i = 0; k = keys[i], i < keys.length; i++)
        {
            //Modifiers
            if (k == 'ctrl' || k == 'control')
            {
                if (e.ctrlKey) kp++;

            }
            else if (k == 'shift')
            {
                if (e.shiftKey) kp++;

            }
            else if (k == 'alt')
            {
                if (e.altKey) kp++;

            }
            else if (k.length > 1)
            { //If it is a special key
                if (special_keys[k] == code) kp++;

            }
            else
            { //The special keys did not match
                if (character == k) kp++;
                else
                {
                    if (shift_nums[character] && e.shiftKey)
                    { //Stupid Shift key bug created by using lowercase
                        character = shift_nums[character];
                        if (character == k) kp++;
                    }
                }
            }
        }

        if (kp == keys.length)
        {
            callback(e);

            if (!opt['propagate'])
            { //Stop the event
                //e.cancelBubble is supported by IE - this will kill the bubbling process.
                e.cancelBubble = true;
                e.returnValue = false;

                //e.stopPropagation works only in Firefox.
                if (e.stopPropagation)
                {
                    e.stopPropagation();
                    e.preventDefault();
                }
                return false;
            }
        }
    }

    //Attach the function with the event
    if (ele.addEventListener) ele.addEventListener(opt['type'], func, false);
    else if (ele.attachEvent) ele.attachEvent('on' + opt['type'], func);
    else ele['on' + opt['type']] = func;
}

var count = 1;
var maxCount;
var secs;
var timerID = null;
var timerRunning = false;
var delay = 1000;
var CICSrow = 0;
var IMSrow = 0;
var CICSDB2row = 0;
var DB2row = 0;
var BATCHrow = 0;


var net1, net2, net3, net4, net5, net6, net7, net8, net9, net10;

function selectColor(color)
{
    if (color == 'W')
        return "ffffff";
    if (color == 'Y')
        return "ffff00";
    if (color == 'O')
        return "ff7700";
    if (color == 'R')
        return "ff0000";
    return "cccccc";
}

function setNetData(comps)
{
    if (document.getElementById('intgratedIFMSBankForm:vsat') == null)
        return;
    var vsat = document.getElementById('intgratedIFMSBankForm:vsat').rows[1].cells[2];
    if (count == 1)
    {
        vsat.innerText = (net1 == null ? '' : net1);
        vsat.style.backgroundColor = selectColor(comps[0].split("-")[2].toString());
    }
    else if (count == 2)
    {
        vsat.innerText = (net2 == null ? '' : net2);
        vsat.style.backgroundColor = selectColor(comps[1].split("-")[2].toString());
    }
    else if (count == 3)
    {
        vsat.innerText = (net3 == null ? '' : net3);
        vsat.style.backgroundColor = selectColor(comps[2].split("-")[2].toString());
    }
    else if (count == 4)
    {
        vsat.innerText = (net4 == null ? '' : net4);
        vsat.style.backgroundColor = selectColor(comps[3].split("-")[2].toString());
    }
    else if (count == 5)
    {
        vsat.innerText = (net5 == null ? '' : net5);
        vsat.style.backgroundColor = selectColor(comps[4].split("-")[2].toString());
    }
    else if (count == 6)
    {
        vsat.innerText = (net6 == null ? '' : net6);
        vsat.style.backgroundColor = selectColor(comps[5].split("-")[2].toString());
    }
    else if (count == 7)
    {
        vsat.innerText = (net7 == null ? '' : net7);
        vsat.style.backgroundColor = selectColor(comps[6].split("-")[2].toString());
    }
    else if (count == 8)
    {
        vsat.innerText = (net8 == null ? '' : net8);
        vsat.style.backgroundColor = selectColor(comps[7].split("-")[2].toString());
    }
    else if (count == 9)
    {
        vsat.innerText = (net9 == null ? '' : net9);
        vsat.style.backgroundColor = selectColor(comps[8].split("-")[2].toString());
    }
    else if (count == 10)
    {
        vsat.innerText = (net10 == null ? '' : net10);
        vsat.style.backgroundColor = selectColor(comps[9].split("-")[2].toString());
    }
}

function sortNetsPercents(comps)
{
    var n1 = comps[0].split("-")[1].toString();
    var n2 = comps[1].split("-")[1].toString();
    var n3 = comps[2].split("-")[1].toString();
    var n4 = comps[3].split("-")[1].toString();
    var n5 = comps[4].split("-")[1].toString();
    var n6 = comps[5].split("-")[1].toString();
    var n7 = comps[6].split("-")[1].toString();
    var n8 = comps[7].split("-")[1].toString();
    var n9 = comps[8].split("-")[1].toString();
    var n10 = comps[9].split("-")[1].toString();


    net1 = "Net 1 : " + (n1 == "null" ? '' : n1 + '%');
    net2 = "Net 2 : " + (n2 == "null" ? '   ' : n2 + '%');
    net3 = "Net 3 : " + (n3 == "null" ? '   ' : n3 + '%');
    net4 = "Net 4 : " + (n4 == "null" ? '   ' : n4 + '%');
    net5 = "Net 5 : " + (n5 == "null" ? '   ' : n5 + '%');
    net6 = "Net 6 : " + (n6 == "null" ? '' : n6 + '%');
    net7 = "Net 7 : " + (n7 == "null" ? '   ' : n7 + '%');
    net8 = "Net 8 : " + (n8 == "null" ? '   ' : n8 + '%');
    net9 = "Net 9 : " + (n9 == "null" ? '   ' : n9 + '%');
    net10 = "Net 10 : " + (n10 == "null" ? '   ' : n10 + '%');


//    net1 = "Net 1 : " + comps[0].split("-")[1].toString() + '%';
//    net2 = "Net 2 : " + comps[1].split("-")[1].toString() + '%';
//    net3 = "Net 3 : " + comps[2].split("-")[1].toString() + '%';
//    net4 = "Net 4 : " + comps[3].split("-")[1].toString() + '%';
//    net5 = "Net 5 : " + comps[4].split("-")[1].toString() + '%';
//    net6 = "Net 6 : " + comps[5].split("-")[1].toString() + '%';
//    net7 = "Net 7 : " + comps[6].split("-")[1].toString() + '%';
//    net8 = "Net 8 : " + comps[7].split("-")[1].toString() + '%';
//    net9 = "Net 9 : " + comps[8].split("-")[1].toString() + '%';
//    net10 = "Net 10 : " + comps[9].split("-")[1].toString() + '%';
    net1.fontcolor("red");
    setNetData(comps);
}

function playAlarm()
{
//    embed = document.createElement("embed");
//embed.setAttribute("src", "beep.wav");
//embed.setAttribute("hidden", true);
//embed.setAttribute("autostart", true);
//document.body.appendChild(embed);

//    var alarmValue = document.getElementById('shetabIFMSForm:alarmValue').value;
//    if(alarmValue)
//    {
//        alert("test.value");
//        var test = document.getElementById('shetabIFMSForm:alarm');
//        alert(test.value);
//        document.getElementById('shetabIFMSForm:alarm').play();
//    }
//    initializeTimer();
}

function initializeTimerForAlarm()
{
    secs = 5;
    stopTheClock();
    startTheTimer();
}

function stopTheClockForAlarm()
{
    if (timerRunning)
        clearTimeout(timerID);
    timerRunning = false;
}

function startTheTimerForAlarm()
{
    if (secs == 0)
    {
        count++;
        if (count > maxCount)
            count = 1;
        playAlarm();
    }
    else
    {
        self.status = secs;
        secs = secs - 1;
        timerRunning = true;
        timerID = self.setTimeout("playAlarm()", delay);
    }
}

function selectItemToShow()
{
    initializeTimer();
    showMainFrameLastEvents("intgratedIFMSBankForm:mainFramLastEvents");
    selectItems("intgratedIFMSBankForm:vsatNetData");
}
function showMainFrameLastEvents(component)
{
    var CICS = document.getElementById(component).value.split("***")[0];
    setCICSData(CICS);
    var IMS = document.getElementById(component).value.split("***")[1];
    setIMSData(IMS);
    var CICSDB2 = document.getElementById(component).value.split("***")[2];
    setCICSDB2Data(CICSDB2);
    var DB2 = document.getElementById(component).value.split("***")[3];
    setDB2Data(DB2);
    var BATCH = document.getElementById(component).value.split("***")[4];
//    setBATCHData(BATCH);
}

function setCICSData(comp)
{
    if (comp == "NOTHING")
        return;
    var row = comp.split(";;;");
    if (document.getElementById('intgratedIFMSBankForm:mainFrame') == null)
        return;
    var mainFrame = document.getElementById('intgratedIFMSBankForm:mainFrame').rows[2].cells[1];
    if (row[CICSrow] == "")
        CICSrow = 0;
    mainFrame.innerText = row[CICSrow].split(":::")[0];
    if (row[CICSrow].split(":::")[1] == "Start" || row[CICSrow].split(":::")[1] == "Up")
        mainFrame.style.backgroundColor = selectColor("W");
    else
    if (row[CICSrow].split(":::")[1] == "Stop" || row[CICSrow].split(":::")[1] == "Down")
        mainFrame.style.backgroundColor = selectColor("R");
    CICSrow++;
}

function setIMSData(comp)
{
    if (comp == "NOTHING")
        return;
    var row = comp.split(";;;");
    if (document.getElementById('intgratedIFMSBankForm:mainFrame') == null)
        return;
    var mainFrame = document.getElementById('intgratedIFMSBankForm:mainFrame').rows[2].cells[2];
    if (row[IMSrow] == "")
        IMSrow = 0;
    mainFrame.innerText = row[IMSrow].split(":::")[0];
    if (row[IMSrow].split(":::")[1] == "Start" || row[IMSrow].split(":::")[1] == "Up")
        mainFrame.style.backgroundColor = selectColor("W");
    else
    if (row[IMSrow].split(":::")[1] == "Stop" || row[IMSrow].split(":::")[1] == "Down")
        mainFrame.style.backgroundColor = selectColor("R");
    IMSrow++;
}

function setCICSDB2Data(comp)
{
    if (comp == "NOTHING")
        return;
    var row = comp.split(";;;");
    if (document.getElementById('intgratedIFMSBankForm:mainFrame') == null)
        return;
    var mainFrame = document.getElementById('intgratedIFMSBankForm:mainFrame').rows[2].cells[3];
    if (row[CICSDB2row] == "")
        CICSDB2row = 0;
    mainFrame.innerText = row[CICSDB2row].split(":::")[0];
    if (row[CICSDB2row].split(":::")[1] == "Start" || row[CICSDB2row].split(":::")[1] == "Up")
        mainFrame.style.backgroundColor = selectColor("W");
    else
    if (row[CICSDB2row].split(":::")[1] == "Stop" || row[CICSDB2row].split(":::")[1] == "Down")
        mainFrame.style.backgroundColor = selectColor("R");
    CICSDB2row++;
}

function setDB2Data(comp)
{
    if (comp == "NOTHING")
        return;
    var row = comp.split(";;;");
    if (document.getElementById('intgratedIFMSBankForm:mainFrame') == null)
        return;
    var mainFrame = document.getElementById('intgratedIFMSBankForm:mainFrame').rows[2].cells[4];
    if (row[DB2row] == "")
        DB2row = 0;
    mainFrame.innerText = row[DB2row].split(":::")[0];
    if (row[DB2row].split(":::")[1] == "Start" || row[DB2row].split(":::")[1] == "Up")
        mainFrame.style.backgroundColor = selectColor("W");
    else
    if (row[DB2row].split(":::")[1] == "Stop" || row[DB2row].split(":::")[1] == "Down")
        mainFrame.style.backgroundColor = selectColor("R");
    DB2row++;
}

function setBATCHSData(comp)
{
    if (comp == "NOTHING")
        return;
    var row = comp.split(";;;");
    if (document.getElementById('intgratedIFMSBankForm:mainFrame') == null)
        return;
    var mainFrame = document.getElementById('intgratedIFMSBankForm:mainFrame').rows[2].cells[3];
    if (row[BATCHrow] == "")
        BATCHrow = 0;
    mainFrame.innerText = row[BATCHrow].split(":::")[0];
    if (row[BATCHrow].split(":::")[1] == "Start" || row[BATCHrow].split(":::")[1] == "Up")
        mainFrame.style.backgroundColor = selectColor("W");
    else
    if (row[BATCHrow].split(":::")[1] == "Stop" || row[BATCHrow].split(":::")[1] == "Down")
        mainFrame.style.backgroundColor = selectColor("R");
    BATCHrow++;
}

function selectItems(component)
{
    var comps = document.getElementById(component).value.split(":");
    if (comps == "")
        return;
    if (component.split(":")[1] == "vsatNetData")
    {
        maxCount = 10;
        sortNetsPercents(comps);
    }
    //    initializeTimer();
}

function initializeTimer()
{
    secs = 5;
    stopTheClock();
    startTheTimer();
}

function stopTheClock()
{
    if (timerRunning)
        clearTimeout(timerID);
    timerRunning = false;
}

function startTheTimer()
{
    if (secs == 0)
    {
        count++;
        if (count > maxCount)
            count = 1;
        selectItemToShow();
    }
    else
    {
        self.status = secs;
        secs = secs - 1;
        timerRunning = true;
        timerID = self.setTimeout("startTheTimer()", delay);
    }
}


function screenResolution()
{
    var resolution = screen.width + "x" + screen.height;
    document.getElementById("loginForm:resolution").value = resolution;
}

var path;
function refreshPage(formName)
{
    if (formName == "shetabIFMSForm")
        path = window.location.pathname;
    else
        path = window.location.pathname.toString() + "?bank=" + document.getElementById(formName + ":bankName").value.toString();
    setTimeout("location.reload(path)", 1000 * 60 * 60 * 24);

}

function setScale()
{
    this.cfg.axes = {
        xaxis:{
            renderer:$.jqplot.DateAxisRenderer,
            labelRenderer:$.jqplot.CanvasAxisLabelRenderer,
//                    tickRenderer:$.jqplot.CanvasAxisTickRenderer,
            tickOptions:{
                angle:-30
            }
        }
    };
}

function imageHover(component, mainImage, hoverImage)
{
    if ($(component).attr('src') == projectName + '/resources/images/' + mainImage + '.png')
        $(component).attr('src', projectName + '/resources/images/' + hoverImage + '.png');

    $(component).hover(
        function ()
        {
            $(component).attr('src', projectName + '/resources/images/' + hoverImage + '.png');
        }
        ,
        function ()
        {
            $(component).attr('src', projectName + '/resources/images/' + mainImage + '.png');
        }
    );
}





































