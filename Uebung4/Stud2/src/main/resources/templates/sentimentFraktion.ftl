<html lang="de">

<head>
    <script src="https://d3js.org/d3.v5.min.js"></script>
    <script>
        function pieChart() {

            const data = [${avgNegative}, ${avgNeutral}, ${avgPositive}];

            const colors = ["#FFFA19", "#f44336", "#9ACD32"];

            // Größe des Tortendiagramms einstellen
            const width = 400;
            const height = 400;
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
                    .innerRadius(25)
                    .outerRadius(radius)
                    .padAngle(.03)
                    .padRadius(100)
                    .cornerRadius(5)
                )
                .attr("fill", (d, i) => colorScale(i));
        }
    </script>
    <link rel="stylesheet" href="/css/sentimentFraktion.css">
</head>

<body>

<div>
    <h2>${fraktion}</h2>
    <div style="text-align: center;"><h3 class="box">Durchschnittliche Sentiment-Werte</h3></div>
    <p></p>
    <table>
        <tr>
            <td><b>Zeitraum</b></td>
            <td style="text-align:center">${von} - ${bis}</td>
        </tr>
        <tr>
            <td><b>Anzahl eingegangener Reden</b></td>
            <td style="text-align:center">${anzahlReden}</td>
        </tr>
        <tr>
            <td><b>Durchschnittliches Sentiment</b>&nbsp&nbsp&nbsp&nbsp</td>
            <td style="text-align:center">${avgSentiment!"k. A."}</td>
        </tr>
        <tr>
            <td><b>Durchschnitt positiver Anteil</b>&nbsp&nbsp&nbsp&nbsp</td>
            <td style="text-align:center; background-color: #9ACD32; border-radius: 5px">${avgPositive!"k. A."}</td>
        </tr>
        <tr>
            <td><b>Durchschnitt neutraler Anteil</b>&nbsp&nbsp&nbsp&nbsp</td>
            <td style="text-align:center; background-color: #FFFA19; border-bottom-right-radius: 5px">${avgNeutral!"k. A."}</td>
        </tr>
        <tr>
            <td><b>Durchschnitt negativer Anteil</b>&nbsp&nbsp&nbsp&nbsp</td>
            <td style="text-align:center; background-color: #f44336; border-radius: 5px">${avgNegative!"k. A."}</td>
        </tr>
    </table>
    <br>
    <div id="chart-container">
        <script>
            pieChart()
        </script>
    </div>
</div>
</body>

</html>