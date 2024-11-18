<html lang="de">
<head>
    <title>Reden</title>
    <script>
        let reden_sentiment = [<#list sentimentInfos as sentimentInfo>${sentimentInfo}, </#list>]
    </script>
    <script src="https://d3js.org/d3.v4.js"></script>
    <script src="/reden_overview_chart.js" defer></script>
    <script src="/reden_overview_stats.js" defer></script>
</head>

<body>
<div>
    <h1>Reden-Übersicht</h1>
</div>

<div>
    <em></em>Zeitraum: ${timespan}</em><br>
    Insgesamt analysiert: <span id="total_speeches"></span><br>
    Davon:
    <ul>
        <li>
            Positives Sentiment: <span id="sentiment_pos"></span><br/>
        </li>
        <li>
            Neutrales Sentiment: <span id="sentiment_neu"></span><br/>
        </li>
        <li>
            Negatives Sentiment: <span id="sentiment_neg"></span><br/>
        </li>
    </ul>
</div>

<div id="chart_container">
</div>

<div>
    <h3>Legende der Abkürzungen</h3>
    <#list abkuerzungen>
        <ul>
            <#items as abkuerzung>
                <li>${abkuerzung}</li>
            </#items>
        </ul>
    <#else>
        <em>Keine Abkürzungen verwendet</em>
    </#list>
</div>

</body>

</html>
