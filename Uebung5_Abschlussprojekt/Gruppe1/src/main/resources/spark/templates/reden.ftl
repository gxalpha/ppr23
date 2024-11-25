<html lang="de">
<!-- @author Stud -->
<head>
    <title>Visualisierung Reden</title>
    <script src="https://d3js.org/d3.v5.min.js" charset="utf-8"></script>
    <script src="/barchart.js"></script>
    <script src="/radarchart.js"></script>
    <script src="/sunburst.js"></script>
    <script src="/reden_boxplot.js" defer></script>
    <link rel="stylesheet" href="/css/reden.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div class="box" >
    <h1>Analyse von ${gesamt} Bundestagsreden</h1>
    <h3>Zeitraum: ${zeitraum}</h3>
</div>
<br>
<div class="box2" style="text-align: center"><h3><b>Verteilung der POS</b></h3></div>

<div>
    <br>
    <div style="text-align: center;" id="pos"></div>

    <script>

        // Bedeutung der Abkürzungen der Legende aus Foliensatz vom 12.06.2023, Folie 41 entnommen
        var data = [
            {name: "attributive Adjektive", value: ${ADJA}},
            {name: "adverbiale / prädikative Adjektive", value: ${ADJD}},
            {name: "Adverbien", value: ${ADV}},
            {name: "Präpositionen", value: ${APPR}},
            {name: "Präpositionen mit Artikel", value: ${APPRART}},
            {name: "bestimmter / unbestimmter Artikel", value: ${ART}},
            {name: "Kardinalitäten", value: ${CARD}},
            {name: "Eigennamen", value: ${NE}},
            {name: "Konjunktionen", value: ${KON}},
            {name: "Nomina Appelativa", value: ${NN}},
            {name: "Personalpronomen", value: ${PPER}},
            {name: "finite Verben", value: ${VVFIN}}
        ];
        barChart("#pos", data);
    </script>
</div>
<br><br>
<div class="box2" style="text-align: center"><h3><b>Sentiment der Reden</b></h3></div>
<div>

    <div class="radar-chart" style="text-align: center;" id="sentiment"></div>

    <script>
        // Dummy Daten.
        var data = [
            {name: "Positive", value: ${avgPos}},
            {name: "Negative", value: ${avgNeg}},
            {name: "Neutral", value: ${avgNeu}}
        ];

        radarChart("#sentiment", data);
    </script>

    <div class="boxplot" style="text-align: center;" id="sentiment_boxplot"></div>

    <script>
        let reden_sentiment = [<#list sentimentInfos as sentimentInfo>${sentimentInfo}, </#list>]
    </script>

</div>
<br>
<div class="box2" style="text-align: center"><h3><b>Verteilung der Redner</b></h3></div><br>
<div>
    <div style="text-align: center;" id="reden_verteilung"></div>

    <script>
        var dataX = "${anzahlRedenX}".split("#");
        var dataY = "${anzahlRedenY}".split("#");

        var i = 0;
        var data = dataX.map(function() {
            i ++
            return {
                name: dataX[i-1],
                value: dataY[i-1]
            };
        });

        barChart("#reden_verteilung", data);

    </script>
</div>

</body>

</html>