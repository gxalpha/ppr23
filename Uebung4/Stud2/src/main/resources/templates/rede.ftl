<html lang="de">

<head>
    <script src="https://d3js.org/d3.v5.min.js"></script>
    <link rel="stylesheet" href="/css/rede.css">
    <script>
        function pieChart() {

            const data = [${negative}, ${neutral}, ${positive}];

            const colors = ["#FFFA19", "#f44336", "#9ACD32"];

            // Größe des Tortendiagramms einstellen
            const width = 200;
            const height = 200;
            const radius = height / 2;

            // SVG-Element erstellen (SVG = Scalable Vector Graphics)
            const svg = d3.select("#chart-container")
                .append("svg")
                .attr("width", width)
                .attr("height", height)
                .append("g")
                .attr("transform", "translate(" + width / 2 + "," + height / 2 + ")");

            // Tortendiagramm erstellen
            const pie = d3.pie();

            // Die Kreisbögen erstellen
            const arcs = pie(data);

            // Farbskala einstellen
            const colorScale = d3.scaleOrdinal()
                .domain(d3.range(data.length))
                .range(colors);

            // Tortendiagramm einfärben
            svg.selectAll("path")
                .data(arcs)
                .enter()
                .append("path")
                .attr("d", d3.arc()
                    .innerRadius(20)
                    .outerRadius(radius)
                    .padAngle(.02)
                    .padRadius(100)
                    .cornerRadius(5)
                )
                .attr("fill", (d, i) => colorScale(i));
        }
    </script>
</head>


<body>

<h2>Bundestagsrede von ${rednerVorname} ${rednerNachname} (${rednerPartei})</h2>
<h4>vom ${datum}, ID${id}</h4>
<br>
<div>
    <div style="text-align: center;"><h3 class="box">Sentiment der gesamten Rede</h3></div>

    <div style="width: 60%; float: left">
        <br>
        <table>
            <tr>
                <td><b>Sentiment&nbsp&nbsp&nbsp&nbsp</b></td>
                <td style="text-align:center">${overallSentiment}</td>
            </tr>
            <tr>
                <td><b>Positiver Anteil&nbsp&nbsp&nbsp&nbsp</b></td>
                <td style="text-align:center; background-color: #9ACD32; border-radius: 5px">${positive}</td>
            </tr>
            <tr>
                <td><b>Neutraler Anteil&nbsp&nbsp&nbsp&nbsp</b></td>
                <td style="text-align:center; background-color: #FFFA19; border-radius: 5px; width: 80%">${neutral}</td>
            </tr>
            <tr>
                <td><b>Negativer Anteil&nbsp&nbsp&nbsp&nbsp</b></td>
                <td style="text-align:center; background-color: #f44336; border-radius: 5px">${negative}</td>
            </tr>
        </table>
    </div>

    <div style="width: 40%; float: right">
        <div id="chart-container">
            <br>
            <script>
                pieChart();
            </script>
        </div>
    </div>
</div>
<br><br><br><br><br><br><br><br><br><br>
<div style="text-align: center">
    <h3 class="box">Inhalt</h3>
</div>
<br>
<div style="width: 80%; text-align: justify; margin: auto">
    <#list sentimentSentences as sentence>
        <p class="tooltip" style="display: inline">${sentence.getCoveredText()} <span
                    class="tooltiptext"> pos: ${sentence.getPos()}; neu: ${sentence.getNeu()}; neg: ${sentence.getNeg()} </span>
        </p>
    </#list>
</div>
<br>
<p><i>Hinweis: Für das Sentiment der einzelnen Sätze einfach den Mauszeiger über den entsprechenden Satz halten!</i></p>
<br>
<div style="text-align: center">
    <h3 class="box">Die Wortarten unter der Lupe &#128269</h3>
</div>
<br>
<div style="width: 80%; text-align: justify; margin: auto">
    <#list posText as pos>
        <#if pos.getSecond() == "NN">
            <p class="tooltip" style="border-radius:4px; background-color: #94E3FE;display: inline">${pos.getFirst()}
                <span class="tooltiptext"> Wortart: ${pos.getSecond()} </span></p>

        <#elseIf pos.getSecond() == "NE">
            <p class="tooltip" style="border-radius:4px; background-color: #EDAEFE;display: inline">${pos.getFirst()}
                <span class="tooltiptext"> Wortart: ${pos.getSecond()} </span></p>
        <#else > <p class="tooltip" style="border-radius:4px; display: inline">${pos.getFirst()} <span
                    class="tooltiptext"> Wortart: ${pos.getSecond()} </span></p>
        </#if>
    </#list>
</div>
<br>
<p><i>Hinweis: Die Nomen sind in Blau und die Named Entities in Pink hervorgehoben.</i></p>

<div>

</div>


</body>

</html>