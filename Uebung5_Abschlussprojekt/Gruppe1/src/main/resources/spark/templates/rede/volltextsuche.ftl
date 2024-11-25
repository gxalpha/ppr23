<html lang="de">
<head>
    <title>Volltextsuche nach Rede</title>
    <link rel="stylesheet" href="/css/reden.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <div class="box">
        <h1>Volltextsuche</h1>
        <p>Text nach dem gesucht wird: "${suchtext}"</p>
    </div>
</div>

<div style="padding: 20px;">
    <h2>Ergebnisse</h2>
    <div>
        <#list redenTuple as rede>
            <div>
                <h3><a href="/rede?id=${rede.getValue0().getID()}">Rede ${rede.getValue0().getID()}</h3>
                ID: ${rede.getValue0().getID()}<br/>
                Redner: ${rede.getValue1().getNameFormatted()}<br/>
            </div>
        <#else>
            <em>Keine Reden mit diesem Inhalt bekannt.</em>
        </#list>
    </div>
</div>
</body>

</html>
