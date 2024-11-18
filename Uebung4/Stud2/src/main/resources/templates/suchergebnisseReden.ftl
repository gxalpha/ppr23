<html>
<head>
    <link rel="stylesheet" href="/css/stammdatenblatt.css">
</head>
<body>
<div>
    <div style="text-align: center;"><h3 class="box">Die Suche nach Bundestagsreden vom ${start} bis zum ${ende}
            ergab ${anzahlErgebnisse} Treffer.</h3>
    </div>
    <p><i>Hinweis: Um zu den NLP-Ergebnissen der jeweiligen Rede zu gelangen, einfach den entsprechenden Button
            anklicken!</i></p>
    <br>
    <#list reden as rede>
        <form method="GET"
              action="/InsightBundestag/reden/<#setting number_format="0"/>${rede.getID()}<#setting number_format=""/>">
            <div style="text-align: center">
                <button type="submit" class="button" style="font-family: monospace, sans-serif">Bundestagsrede
                    vom ${rede.getDate()} mit
                    ID<#setting number_format="0"/>${rede.getID()}<#setting number_format=""/></button>
            </div>
        </form>
    </#list>
</div>
</body>
</html>