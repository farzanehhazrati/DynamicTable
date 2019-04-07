/// I could write a better code and even more less code .
/// but there was some problems :
///   1 . Date Object begin from 1970 , it means new Date(1970,0,1).GetTime() = 0 ;)
///   2 . JavaScript is very slow ( e.g JalaliCalendar.GetMonth(mydate) --> take 10 miliSec in my PC [1,500 MB Ram , P4 , 2 Meg Full Cache Intel]
///           So year range is restricted from 1290 to XXXX , because days calculation from first day of JalaliCalendar is very time consumer
///   3 . JavaScript does not support any Localization techniques
///   4 . Year Range is retricted from 1290 to XXXX for PersianCalendar
///       because calculation days are 



// ** I18N

// Calendar EN language
// Author: Mihai Bazon, <mishoo@infoiasi.ro>
// Encoding: any
// Distributed under the same terms as the calendar itself.

// For translators: please use UTF-8 if possible.  We strongly believe that
// Unicode is the answer to a real internationalized world.  Also please
// include your contact information in the header, as can be seen above.

// full day names
Calendar._DN = new Array
        ("يکشنبه",
                "دوشنبه",
                "‌سشنبه",
                "چهارشنبه",
                "پنجشنبه",
                "آدينه",
                "شنبه",
                "يکشنبه");

// Please note that the following array of short day names (and the same goes
// for short month names, _SMN) isn't absolutely necessary.  We give it here
// for exemplification on how one can customize the short day names, but if
// they are simply the first N letters of the full name you can simply say:
//
//   Calendar._SDN_len = N; // short day name length
//   Calendar._SMN_len = N; // short month name length
//
// If N = 3 then this is not needed either since we assume a value of 3 if not
// present, to be compatible with translation files that were written before
// this feature.

// short day names
Calendar._SDN = new Array
        ("ی",
                "د",
                "س",
                "چ",
                "پ",
                "آ",
                "ش",
                "ی");

// full month names
Calendar._MN = new Array
        ("فروردين",
                "ارديبهشت",
                "خرداد",
                "تير",
                "مرداد",
                "شهريور",
                "مهر",
                "آبان",
                "آذر",
                "دی",
                "بهمن",
                "اسفند");

// short month names
Calendar._SMN = new Array
        ("فروردين",
                "ارديبهشت",
                "خرداد",
                "تير",
                "مرداد",
                "شهريور",
                "مهر",
                "آبان",
                "آذر",
                "دی",
                "بهمن",
                "اسفند");

// tooltips
Calendar._TT = {};
Calendar._TT["INFO"] = "درباره گاه شمار";

Calendar._TT["ABOUT"] =
"DHTML Date/Time Selector\n" +
"(c) dynarch.com 2002-2003\n" + // don't translate this this ;-)
"For latest version visit: http://dynarch.com/mishoo/calendar.epl\n" +
"Distributed under GNU LGPL.  See http://gnu.org/licenses/lgpl.html for details." +
"\n\n" +
"Date selection:\n" +
"- Use the \xab, \xbb buttons to select year\n" +
"- Use the " + String.fromCharCode(0x2039) + ", " + String.fromCharCode(0x203a) + " buttons to select month\n" +
"- Hold mouse button on any of the above buttons for faster selection.";
Calendar._TT["ABOUT_TIME"] = "\n\n" +
                             "Time selection:\n" +
                             "- Click on any of the time parts to increase it\n" +
                             "- or Shift-click to decrease it\n" +
                             "- or click and drag for faster selection.";

Calendar._TT["PREV_YEAR"] = "سال قبلی - برای فهرست نگهدارید ";
Calendar._TT["PREV_MONTH"] = "ماه قبلی - برای فهرست نگهدارید ";
Calendar._TT["GO_TODAY"] = "امروز";
Calendar._TT["NEXT_MONTH"] = "ماه بعدی - برای فهرست نگهدارید ";
Calendar._TT["NEXT_YEAR"] = "سال بعدی - برای فهرست نگهدارید ";
Calendar._TT["SEL_DATE"] = "تاریخ را انتخاب کنید";
Calendar._TT["DRAG_TO_MOVE"] = "برای جابجایی پنجره را بکشید";
Calendar._TT["PART_TODAY"] = " ، امروز ";

// the following is to inform that "%s" is to be the first day of week
// %s will be replaced with the day name.
Calendar._TT["DAY_FIRST"] = "%s را ابتدا نشان بده ";

// This may be locale-dependent.  It specifies the week-end days, as an array
// of comma-separated numbers.  The numbers are from 0 to 6: 0 means Sunday, 1
// means Monday, etc.
Calendar._TT["WEEKEND"] = "5";

Calendar._TT["CLOSE"] = "بستن";
Calendar._TT["TODAY"] = "امروز";
Calendar._TT["TIME_PART"] = "(Shift-)برای تغییر کلیک یا جابجا نمایید.";

// date formats
Calendar._TT["DEF_DATE_FORMAT"] = "%Y-%m-%d";
Calendar._TT["TT_DATE_FORMAT"] = "%A ، %e %b";

Calendar._TT["WK"] = "هفته";
Calendar._TT["TIME"] = "زمان :";


//these entries are used for AM and PM in local date time formats
Calendar._TT["TIME_AM"] = "ق.ظ";
Calendar._TT["TIME_PM"] = "ب.ظ";

//this regular expression is used for date time parsing for persian peoples
// but this way for parsing date time is not good which u r using .
//because there are some weekdays name in persian or some theme cultures made of 2 words
// for example --> Tuesday == سه شنبه 
// when we parse this string we get سه and شنبه and this is not correct;
// so for this problem i changed سه شنبه to سشنبه but it is not correct;
Calendar.WordReg = /[^A-Za-z0-9_آ-ی]+/;

// Calendar Direction (Right_To_Left or Lef_To_Right)
// in persian and arabic culture must be right to left
Calendar._Direction = "rtl";

// This object has been created to increase speed of run 
// because we frequently use DateTime Convertion in the Calendar
// and JavaScript loops and expression are slow
// in fact this object in not necessary for calculations but it is suitbale for speed optimization
// this Object maintain local date time and has an addDays method 
LocalDateTime = function(year, month, day, hour, minute, second, millisecond)
{
    this.year = year;
    this.month = month;
    this.day = day;
    this.hour = hour;
    this.minute = minute;
    this.second = second;
    this.millisecond = millisecond;
}

LocalDateTime.prototype.addDays = function(days)
{
    days = days + this.day;
    while (days > (daysInMonth = JalaliCalendar.GetDaysInMonth(true, this.year, this.month, 0)))
    {
        days = days - daysInMonth;
        //months[this.month];
        if (this.month == 11) this.year++;
        this.month = ++this.month % 12;
    }
    this.day = days
}


RemainingYearDays = function(year, days)
{
    this.year = year;
    this.days = days;
}

// This is JalaliCalendar ( very famous calendar and accurate for persian people) 
JalaliCalendar = function ()
{
};

JalaliCalendar.PersianEra = 1;
JalaliCalendar.twoDigitYearMax = 1409;

// Today --> Gregorian Calendar -->  2005 - August    - 31   --> 2005 / 08 / 31
//           Jalali Calendar    -->  1384 - Shahrivar - 09   --> 1384 / 06 / 09
//                                          شهریور
// Min Year , [in jajali calendar ] , i choosed this year to increase speed of run 
JalaliCalendar.MinYear = 1290;
// Max Year in JalaliCalendar
JalaliCalendar.MaxYear = 1450;

JalaliCalendar.AddDays = function (time, days)
{
    return new Date(time.getTime() + days * 86400000);
}

JalaliCalendar.AddMonths = function (time, months)
{
    // Must be complete for extra usage , but it is better to implement
    /*
                if (Math.abs(months) > 120000){
                    //ArgumentOutOfRangeException("months", "Valid values are between -120000 and 120000, inclusive.");
                    return ;
                }
                year  = this.GetYear(true, time);
                month = this.GetMonth(false, time);
                day   = this.GetDayOfMonth(false, time);

                month += (year-1)*12 + months;
                year = (month-1)/12 + 1;
                month -= (year-1)*12;
                if (day > 29){
                    maxday = this.GetDaysInMonth(false, year, month, 0);
                    if (maxday < day) day = maxday;
                }

                dateTime = this.ToDateTime(year, month, day, 0, 0, 0, 0) + this.TimeOfDay(time);  //time.TimeOfDay;
                return dateTime;
    */
}

JalaliCalendar.AddYears = function (time, years)
{
    // Must be complete for extra usage , but it is better to implement
    /*
                year = this.GetYear(true, time);
                month = this.GetMonth(false, time);
                day = this.GetDayOfMonth(false, time);
                year += years;
                if (day == 30 && month == 12){
                    if (!this.IsLeapYear(false, year, 0))
                        day = 29;
                }
                dateTime = this.ToDateTime(year, month, day, 0, 0, 0, 0) + time.getTime(); //time.TimeOfDay;
                return dateTime;
    */
}

JalaliCalendar.GetLocalDateTime = function(time)
{
    year_RDays = this.GetYearAndRemainingDays(true, time);
    days = year_RDays.days;
    var month = -1;
    for (i = 0; i < 12; i++)
    {
        month = i;
        daysInMonth = JalaliCalendar.GetDaysInMonth(year_RDays.year, month, 0)
        if (days <= daysInMonth) break;
        days -= daysInMonth;
    }
    return new LocalDateTime(year_RDays.year, month, days, time.getHours(), time.getMinutes(), time.getSeconds(), time.getMilliseconds());
}

JalaliCalendar.GetDayOfMonth = function()
{ // (validate, time) | (time)
    var validate = true ;
    var time ;
    if (arguments.length == 1)
        time = arguments[0];
    else
        if (arguments.length == 2)
        {
            validate = arguments[0];
            time = arguments[1]
        }

    days = this.GetDayOfYear(validate, time);
    for (i = 0; i < 6; i++)
    {
        if (days <= 31) return days;
        days -= 31;
    }
    for (i = 0; i < 5; i++)
    {
        if (days <= 30) return days;
        days -= 30;
    }
    return days;
}

JalaliCalendar.GetDayOfWeek = function (time)
{
    return time.getDay();
}


JalaliCalendar.GetDayOfYear = function()
{ // (validate, time) | (time)
    var validate = true ;
    var time ;
    if (arguments.length == 1)
        time = arguments[0];
    else
        if (arguments.length == 2)
        {
            validate = arguments[0];
            time = arguments[1]
        }

    return this.GetYearAndRemainingDays(validate, time).days;
}


JalaliCalendar.GetDaysInMonth = function()
{ // (validate, year, month, era) | (year, month, era)
    var validate = true ;
    var year, month, era
    if (arguments.length == 3)
    {
        year = arguments[0];
        month = arguments[1];
        era = arguments[2];
    }
    else
        if (arguments.length == 4)
        {
            validate = arguments[0];
            year = arguments[1];
            month = arguments[2];
            era = arguments[3];
        }
        //else
        //new Error(12);

    this.CheckEraRange(validate, era);
    this.CheckYearRange(validate, year);
    this.CheckMonthRange(validate, month);
    if (month < 6) return 31;
    if (month < 11) return 30;
    if (this.IsLeapYear(false, year, 0)) return 30;
    else return 29;
}


JalaliCalendar.GetDaysInYear = function()
{ // validate, year, era |  year, era
    var validate = true ;
    var year , era ;

    if (arguments.length == 2)
    {
        year = arguments[0];
        era = arguments[1];
    }
    else
        if (arguments.length == 3)
        {
            validate = arguments[0];
            year = arguments[1];
            era = arguments[2];
        }

    if (this.IsLeapYear(validate, year, era)) return 366;
    return 365;
}

JalaliCalendar.GetEra = function (time)
{
    this.CheckTicksRange(true, time);
    return this.PersianEra;
}

JalaliCalendar.GetMonth = function()
{// time | validate , time
    var validate = true ;
    var time ;

    if (arguments.length == 1)
        time = arguments[0];
    else
        if (arguments.length == 2)
        {
            validate = arguments[0];
            time = arguments[1];
        }
    days = this.GetDayOfYear(validate, time);
    if (days <= 31) return 0;
    if (days <= 62) return 1;
    if (days <= 93) return 2;
    if (days <= 124) return 3;
    if (days <= 155) return 4;
    if (days <= 186) return 5;
    if (days <= 216) return 6;
    if (days <= 246) return 7;
    if (days <= 276) return 8;
    if (days <= 306) return 9;
    if (days <= 336) return 10;
    return 11;
}

JalaliCalendar.GetMonthsInYear = function (year, era)
{
    this.CheckEraRange(true, era);
    this.CheckYearRange(true, year);
    return 12;
}

JalaliCalendar.GetYear = function()
{ // time | validate , time
    var validate = true ;
    var time ;

    if (arguments.length == 1)
        time = arguments[0];
    else
        if (arguments.length == 2)
        {
            validate = arguments[0];
            time = arguments[1];
        }
    return this.GetYearAndRemainingDays(validate, time).year;
}

JalaliCalendar.IsLeapDay = function (year, month, day, era)
{
    this.CheckEraRange(true, era);
    this.CheckYearRange(true, year);
    this.CheckMonthRange(true, month);
    if (day == 30 && month == 12 && this.IsLeapYear(false, year, 0))
        return true;
    return false;
}

JalaliCalendar.IsLeapMonth = function(year, month, era)
{
    this.CheckEraRange(true, era);
    this.CheckYearRange(true, year);
    this.CheckMonthRange(true, month);
    return false;
}

JalaliCalendar.HasLeapFrac = function(year)
{
    a = 31 * (year + 38) / 128;
    if (a - Math.floor(a) < 0.31)
        return true;
    return false;
}


JalaliCalendar.IsLeapYear = function()
{ // validate,  year,  era || ,  year,  era
    var validate = true ;
    var year , era ;

    if (arguments.length == 2)
    {
        year = arguments[0];
        era = arguments[1];
    }
    else
        if (arguments.length == 3)
        {
            validate = arguments[0];
            year = arguments[1];
            era = arguments[2];
        }
        else
            return;

    this.CheckEraRange(validate, era);
    this.CheckYearRange(validate, year);
    if (this.HasLeapFrac(year) && !this.HasLeapFrac(year - 1))
        return true;
    return false;
}

JalaliCalendar.ParsDatetime = function (dateTime)
{
    var parsedDateTime = dateTime.toString().split(" ")
    var date = parsedDateTime[0].split("/");
    var time = parsedDateTime[1].split(":");
    return this.ToDateTime(parseInt(date[0], 10), parseInt(date[1], 10) - 1, parseInt(date[2], 10), parseInt(time[0], 10), parseInt(time[1], 10), parseInt(time[2], 10), 0, 0).toString().replace('+', '%2b');
}

JalaliCalendar.ToDateTime = function (year, month, day, hour, minute, second, millisecond, era)
{
    this.CheckEraRange(true, era);
    this.CheckDateRange(true, year, month, day);
    days = day;
    for (i = 0; i < month; i++)
    {
        if (i < 6) days += 31;
        else if (i < 11) days += 30;
    }
    days += 365 * year + this.NumberOfLeapYearsUntil(false, year);

    timePart = new Date(1970, 0, 1, hour, minute, second, millisecond);
    // Date(621,2,21,0,0,0) <---- First Day of JalaliCalendar in Gregorian Calendar
    ticks = days * 86400000 + this.TimeOfDate(timePart) + new Date(621, 2, 21, 0, 0, 0).getTime();
    dateTime = new Date(ticks);    
    return dateTime;
}


JalaliCalendar.ToFourDigitYear = function(year)
{
    if (year != 0)
    {
        this.CheckYearRange(true, year);
    }
    if (year > 99) return year;
    a = Math.floor(this.twoDigitYearMax / 100);
    if (year > this.twoDigitYearMax - a * 100) a--;
    return a * 100 + year;
}

JalaliCalendar.getEras = function()
{
    eras = new Array(1);
    return eras;
    //}
}

JalaliCalendar.getTwoDigitYearMax = function()
{
    return this.twoDigitYearMax;
}

JalaliCalendar.setTwoDigitYearMax = function (value)
{
    if (value < 100 || 9378 < value)
    //throw new System.ArgumentOutOfRangeException("value", "Valid values are between 100 and 9378, inclusive.");
        return;
    this.twoDigitYearMax = value;
}

JalaliCalendar.GetCentury = function(time)
{
    return (this.GetYear(true, time) - 1) / 100 + 1;
}

JalaliCalendar.NumberOfLeapYearsUntil = function()
{ // validate, year |  year
    var validate = true ;
    var year ;

    if (arguments.length == 1)
    {
        year = arguments[0];
    }
    else
        if (arguments.length == 2)
        {
            validate = arguments[0];
            year = arguments[1];
        }
        else
            return;

    this.CheckYearRange(validate, year);
    count = 311;
    for (i = 1288; i < year; i++)
    {
        if (this.IsLeapYear(false, i, 0))
        {
            count++;
            i += 3;
        }
    }
    return count;
}

JalaliCalendar.CheckEraRange = function(validate, era)
{
    if (validate)
    {
        if (era < 0 || 1 < era)
        //throw new System.ArgumentOutOfRangeException("era", "Era value was not valid.");
            new Error();
    }
    return;
}

JalaliCalendar.CheckYearRange = function(validate, year)
{
    if (validate)
    {
        if (year < 1 || 9378 < year)
        //throw new System.ArgumentOutOfRangeException("year", "Valid values for year are between 1 and 9378, inclusive.");
            new Error();
    }
    return;
}

JalaliCalendar.CheckMonthRange = function(validate, month)
{
    if (validate)
    {
        if (month < 1 || 12 < month)
        //	throw new System.ArgumentOutOfRangeException("month", "Values for month must be between 1 and 12.");
            new Error();
    }
    return;
}

JalaliCalendar.CheckDateRange = function(validate, year, month, day)
{
    if (validate)
    {
        maxday = this.GetDaysInMonth(true, year, month, 0);
        if (day < 1 || maxday < day)
        {
            if (day == 30 && month == 12)
            //throw new System.ArgumentOutOfRangeException("day", "Year "+year+" is not a leap year. Day must be at most 29 for month 12 of this year.");
                new Error();
            //throw new System.ArgumentOutOfRangeException("day", "Day must be between 1 and "+maxday+" for month "+month+".");
            new Error();
        }
    }
}

JalaliCalendar.CheckTicksRange = function(validate, time)
{
    // Valid ticks represent times between 12:00:00.000 AM, 22/03/0622 CE and 11:59:59.999 PM, 31/12/9999 CE.
    if (validate)
    {
        ticks = time.Ticks;
        if (ticks < 196037280000000000) // is not correct must be calculated
        //throw new System.ArgumentOutOfRangeException("time", "Specified time is not supported in this calendar. It should be between 22/03/0622 12:00:00 AM and 31/12/9999 11:59:59 PM, inclusive.");
            new Error();
    }
    return;
}

JalaliCalendar.GetYearAndRemainingDays = function(validate, time)
{
    ///return new RemainingYearDays(1384,40);

    this.CheckTicksRange(validate, time);
    days = Math.floor((time - new Date(622, 2, 21, 0, 0, 0)) / 86400000) - 470432;
    // 1289 Jalali Is Min Year to increase performance
    year = 1289;
    //0 ;
    daysInNextYear = 365;
    while (days > daysInNextYear)
    {
        days -= daysInNextYear;
        year++;
        daysInNextYear = this.GetDaysInYear(false, year, 0);
    }
    return new RemainingYearDays(year, days);
}

JalaliCalendar.TimeOfDate = function(time)
{
    return (
            time.getHours() * 3600000 +
            time.getMinutes() * 60000 +
            time.getSeconds() * 1000 +
            time.getMilliseconds());
}


JalaliCalendar.GetWeekNumber = function(date)
{
    var d = new Date(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
    var DoW = d.getDay();
    oldDate = new Date(d);
    //d.setDate(d.getDate() - (DoW + 6) % 7 + 3); // Nearest Thu
    d.setDate(d.getDate() - (DoW + 8) % 7 + 3);
    // Nearest Thu
    //alert(d + " : " + oldDate + " : dow = " + DoW + " : " + (-(DoW + 6) % 7 + 3));
    //alert(d.getDay() + " : " + d.getDate());
    var ms = d.valueOf();
    // GMT
    //	d.setMonth(0);
    //	d.setDate(4); // Thu in Week 1
    //	alert(d);
    ldt = this.GetLocalDateTime(d);
    d = this.ToDateTime(ldt.year, 0, 2, ldt.hour, ldt.minute, ldt.second, ldt.millisecond, 0);

    return Math.round((ms - d.valueOf()) / (7 * 864e5)) + 1;
};

///alert(JalaliCalendar.GetWeekNumber(JalaliCalendar.ToDateTime(1384,0,1,0,0,0,0,0)));
//alert(JalaliCalendar.GetWeekNumber (new Date(2024,0,1)));

LocalCalendar = JalaliCalendar;
