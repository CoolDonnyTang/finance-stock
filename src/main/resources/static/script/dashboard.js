function checkAddrInfo() {
    $.ajax({
        type:"get",
        url:appName+"addr/status" ,
        error:function() {
            alert("连接服务器失败");
        },
        success:function(result) {
            if(result.status == 200) {
                if(result.data == true) {
                    $("#addrContainer").hide();
                } else {
                    $("#addrContainer").show();
                }
            } else {
                alert(result.message);
            }
        }
    });
}

function submitAndCalculate(paramInfo) {
    if(!checkStockCode(paramInfo.code)) {
        alert("股票代码格式错误");
        return;
    }
    $.ajax({
        data:paramInfo,
        dataType:"json",
        type:"post",
        url:appName+"rangebreak/calculate" ,
        error:function(error) {
            alert(error);
        },
        success:function(result) {
            if(result.status == 200) {
                var data = result.data;
                $("#dataDisplayBody").empty();
                var $tr = $("<tr></tr>");
                $("#dataDisplayBody").append($tr);
                var profitTotal = 0;
                var actualTotal = 0;
                var diffTotal = 0;
                for(var index in data){
                    var line = data[index];

                    profitTotal = profitTotal + line.profit;
                    actualTotal = actualTotal + line.realPriceChange;
                    diffTotal = diffTotal + (line.profit - line.realPriceChange);

                    if(index==0) {
                        $("#stockName").text(line.stockName + "(" + paramInfo.code + ")");
                    }

                    var $tr = $("<tr></tr>");
                    $("#dataDisplayBody").append($tr);
                    $tr.append($("<td>"+index+"</td>"));
                    $tr.append($("<td>"+line.startDate+"</td>"));
                    $tr.append($("<td>"+line.endDate+"</td>"));
                    $tr.append($("<td>"+line.useCoefficient+"</td>"));
                    $tr.append($("<td>"+line.coefficientStarDate + " : " + line.coefficientEndDate +"</td>"));
                    $tr.append($("<td>"+line.profit	+"</td>"));
                    $tr.append($("<td>"+line.realPriceChange+"</td>"));
                    $tr.append($("<td>"+(line.profit - line.realPriceChange).toFixed(2)+"</td>"));
                }
                var $tr = $("<tr></tr>");
                $("#dataDisplayBody").append($tr);
                $tr.append($("<td> </td>"));
                $tr.append($("<td> </td>"));
                $tr.append($("<td> </td>"));
                $tr.append($("<td> </td>"));
                $tr.append($("<td>合 计：</td>"));
                $tr.append($("<td>"+profitTotal.toFixed(2)+"</td>"));
                $tr.append($("<td>"+actualTotal.toFixed(2)+"</td>"));
                $tr.append($("<td>"+diffTotal.toFixed(2)+"</td>"));

                var param = new Object();
                param.stockName = data[data.length -1].stockName;
                drawLineChart(data, param);
            } else {
                alert(result.message);
            }
        }
    });
    
    function drawLineChart(data, param) {
        var drawData = new Array();

        var rangebreak = new Object();
        rangebreak.data = new Array();
        drawData.push(rangebreak);

        var realData = new Object();
        realData.data = new Array();
        drawData.push(realData);

        for(var index in data) {
            var line = data[index];
            var range = new Array();
            range.push(getStringByReg(line.endDate, /(\d{4})/)/1);
            if(index-1>=0) {
                range.push(rangebreak.data[index-1][1] + line.profit);
            } else {
                range.push(line.profit);
            }
            rangebreak.data.push(range);

            var real = new Array();
            real.push(getStringByReg(line.endDate, /(\d{4})/)/1);
            if(index-1>=0) {
                real.push(realData.data[index-1][1] + line.realPriceChange);
            } else {
                real.push(line.realPriceChange);
            }
            realData.data.push(real);
        }
        console.log(drawData);

        //////////////////////////////////////////////
        var width=800;
        var height=500;

        // var dataset=[
        //     {
        //         country:"china",
        //         gdp:[[2000,11920],[2001,13170],[2002,14550],
        //             [2003,16500],[2004,19440],[2005,22870],
        //             [2006,27930],[2007,35040],[2008,45470],
        //             [2009,51050],[2010,59490],[2011,73140],
        //             [2012,83860],[2013,103550]]
        //     },
        //     {
        //         country:"japan",
        //         gdp:[[2000,47310],[2001,41590],[2002,39800],
        //             [2003,43020],[2004,46550],[2005,45710],
        //             [2006,43560],[2007,43560],[2008,48490],
        //             [2009,50350],[2010,54950],[2011,59050],
        //             [2012,59370],[2013,48980]]
        //     }
        // ];

        var padding={top:70, right:70, bottom: 70, left:70};
        var priceMin = null;
        var priceMax = null;
        var yearMin = null;
        var yearMax = null;
        for(var i=0;i<drawData.length;i++){
            //price min
            var currentPrice = 0;
            currentPrice = d3.min(drawData[i].data,function(d){
                return d[1];
            });
            console.log(currentPrice + "--------");
            if(priceMin===null ||currentPrice < priceMin) {
                priceMin = currentPrice;
            }

            //price max
            currentPrice = d3.max(drawData[i].data,function(d){
                return d[1];
            });
            if(priceMax===null || currentPrice > priceMax) {
                priceMax = currentPrice;
            }

            //year min
            var currentYear = 0;
            currentYear = d3.min(drawData[i].data,function(d){
                return d[0];
            });
            if(yearMin===null || currentYear < yearMin) {
                yearMin = currentYear;
            }

            //price max
            currentYear = d3.max(drawData[i].data,function(d){
                return d[0];
            });
            if(yearMax===null || currentYear > yearMax) {
                yearMax = currentYear;
            }

        }
        console.log(priceMin);
        console.log(priceMax);
        console.log(yearMin);
        console.log(yearMax);

        var xScale=d3.scale.linear()
            .domain([yearMin,yearMax])
            .range([0,width-padding.left-padding.right]);

        var yScale=d3.scale.linear()
            .domain([priceMin,priceMax])
            .range([height-padding.bottom-padding.top,0]);

        var linePath=d3.svg.line()//创建一个直线生成器
            .x(function(d){
                return xScale(d[0]);
            })
            .y(function(d){
                return yScale(d[1]);
            })
            .interpolate("basis")//插值模式
        ;

        //定义两个颜色
        var colors=[d3.rgb(0,0,255),d3.rgb(0,255,0)];

        var svg=d3.select("#lineChart")
            .append("svg")
            .attr("width",width)
            .attr("height",height);

        svg.selectAll("path")
            .data(drawData)
            .enter()
            .append("path")
            .attr("transform","translate("+padding.left+","+padding.top+")")
            .attr("d",function(d){
                return linePath(d.data);
                //返回线段生成器得到的路径
            })
            .attr("fill","none")
            .attr("stroke-width",3)
            .attr("stroke",function(d,i){
                return colors[i];
            });

        var xAxis=d3.svg.axis()
            .scale(xScale)
            .ticks(5)
            .tickFormat(d3.format("d"))
            .orient("bottom");

        var yAxis=d3.svg.axis()
            .scale(yScale)
            .orient("left");

        //添加一个g用于放x轴
        svg.append("g")
            .attr("class","axis")
            .attr("transform","translate("+padding.left+","+(height-padding.top)+")")
            .call(xAxis);

        svg.append("g")
            .attr("class","axis")
            .attr("transform","translate("+padding.left+","+padding.top+")")
            .call(yAxis);

        var legend = svg.append("g");
        var lineNames = ["Range Break",param.stockName];
        var lineColor = colors;
        addLegend();
        function addLegend()
        {
            var textGroup=legend.selectAll("text")
                .data(lineNames);textGroup.exit().remove();legend.selectAll("text")
            .data(lineNames)
            .enter()
            .append("text")
            .text(function(d){return d;})
            .attr("class","legend")
            .attr("x", function(d,i) {return i*100;})
            .attr("y",0)
            .attr("fill",function(d,i){ return lineColor[i];});

            var rectGroup=legend.selectAll("rect")
                .data(lineNames);

            rectGroup.exit().remove();

            legend.selectAll("rect")
                .data(lineNames)
                .enter()
                .append("rect")
                .attr("x", function(d,i) {return i*120-20;})
                .attr("y",-10)
                .attr("width",12)
                .attr("height",12)
                .attr("fill",function(d,i){ return lineColor[i];});

            legend.attr("transform","translate("+((width-lineNames.length*100)/2)+","+(height-10)+")");
        }

    }
}