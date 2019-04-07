/**
 * Created with IntelliJ IDEA.
 * User: b_raeisifard
 * Date: 11/30/13
 * Time: 2:47 PM
 * To change this template use File | Settings | File Templates.
 */
function updateCont2(xhr, status, args, chartDivName) {
    var seriesOptions = [],
        yAxisOptions = [],
        seriesCounter = 0,
        names = ['MSFT', 'AAPL', 'GOOG'],
        colors = Highcharts.getOptions().colors;
    PrimeFaces.info("نمودار DailyNumSndRcv دوباره سازی شد");
    var dlv = eval(args.dlv);
    var snd = eval(args.snd);
    var prc = eval(args.prc);
    var chartDiv = chartDivName;
    seriesOptions = [
        {
            name: "dlv",
            data: dlv
        },
        {
            name: "snd",
            data: snd
        },
        {
            name: "prc",
            data: prc
        }
    ];
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    });
    createChart(seriesOptions);


    // create the chart when all data is loaded
    function createChart(data) {

        $('#container').highcharts('StockChart', {
            chart: {
            },
            legend: {
                enabled: true
            },
            rangeSelector: {
                enabled: true,
                selected: 0,
                buttons: [{
                    type: 'minute',
                    count: 1,
                    text: '1m'
                },{
                    type: 'minute',
                    count: 60,
                    text: '1h'
                }, {
                    type: 'all',
                    text: 'All'
                }]
            },

            yAxis: {
                labels: {
//                    formatter: function () {
//                        return (this.value > 0 ? '+' : '') + this.value + '%';
//                    }
                },
                plotLines: [
                    {
                        value: 0,
                        width: 2,
                        color: 'silver'
                    }
                ]
            },

            plotOptions: {
                series: {
//                    compare: 'percent'
                }
            },

            tooltip: {
//                pointFormat: '&lt;span style="color:{series.color}">{series.name}&lt;/span>: &lt;b>{point.y}&lt;/b> ({point.change}%)&lt;br/>',
//                valueDecimals: 2
            },

            series: seriesOptions
//            series:[{
//                name: "dlv",
//                data: dlv
//            }]
        });
    }

};

//$(document).ready(function () {
//    remoteUpdate();
//});