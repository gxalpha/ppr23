<html lang="de">
<head>
    <title>Sitzungsübersicht</title>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <h1 class="box">Alle Sitzungen</h1>
</div>

<div style="padding: 20px;">
    <h2>Sitzungen aufsteigend sortiert nach Datum</h2>
    <div>
        <#list sitzungen as sitzung>
            <div>
                <h3><a href="/sitzung?id=${sitzung[0]}">Sitzung ${sitzung[1]}</h3>
                <p>
                ID: ${sitzung[0]}<br/>
                Sitzungsnummer: ${sitzung[1]}<br/>
                </p>
            </div>
        <#else>
            <em>Keine Sitzungen bekannt.</em>
        </#list>
    </div>
</div>

</body>

</html>
