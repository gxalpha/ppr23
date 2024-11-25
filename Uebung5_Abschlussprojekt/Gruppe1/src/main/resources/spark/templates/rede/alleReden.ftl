<html>
<head>
    <link rel="stylesheet" href="/css/reden.css">
    <link rel="stylesheet" href="/css/header.css">
</head>
<body>
${header}
<div style="padding: 20px;">
    <div style="text-align: center;"><h3 class="box">Liste aller erfassten Bundestagsreden (Anzahl: ${anzahlErgebnisse})</h3>
    </div>
    <br>
    <#list reden as rede>
        <div>
            <h3>
            <a href="/rede?id=${rede[0]}">
            Rede: ${rede[0]}, </a>
            Redner: <a href="/abgeordneter?id=${rede[1]}"> ${rede[1]}, </a>
            Datum: ${rede[2]}
            </h3>
        </div>
    </#list>
</div>
</body>
</html>