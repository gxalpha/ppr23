<html lang="de">
<head>
    <title>Rede ${rede.getID()} von ${redner.getNameFormatted()}</title>
    <link rel="stylesheet" href="/tooltip.css">
</head>

<body>
<div>
    <h1><a href="abgeordneter?id=${redner.getID()}">${redner.getNameFormatted()}, ${redner.getPartei()}</a>:
        Rede ${rede.getID()}</h1>
</div>

<div>
    Gesamt-Sentiment: ${nlp.getSentiment()}
</div>

<div>
    <h2>Text</h2>
    <#list nlp.getSentences() as sentence>
        <div class="tooltip-background">
            ${sentence.getText()}
            <span class="tooltip">Sentiment:&nbsp${sentence.getSentiment()}</span>
        </div>
        <br/>
    </#list>
</div>

<div>
    <h2>Nomen</h2>
    <#list nlp.getNouns()>
        <ul>
            <#items as noun>
                <li>${noun}</li>
            </#items>
        </ul>
    <#else>
        <em>Keine Nomen erkannt</em>
    </#list>
</div>

<div>
    <h2>Named Entities</h2>
    <#list nlp.getNamedEntities()>
        <ul>
            <#items as entity>
                <li>${entity}</li>
            </#items>
        </ul>
    <#else>
        <em>Keine Named Entities erkannt</em>
    </#list>
</div>


</body>

</html>
