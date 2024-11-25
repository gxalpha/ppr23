<html lang="de">
<head>
    <title>Übersicht zu ${abgeordneter.getNameFormatted()}</title>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>
${header}

<div style="text-align: center;">
    <h1 class="box">${abgeordneter.getNameFormatted()}, ${abgeordneter.getPartei()}</h1>
</div>

<div class="fotoContainer">
    <img src="${foto}" alt="Foto des Abgeordneten">
</div>

<div style="padding: 20px;">
    <h2>Biografische Angaben</h2>
    <div>
        Geburtsdatum: ${abgeordneter.getGeburtsdatum()?date}<br/>
        Geburtsort: ${abgeordneter.getGeburtsort()}<br/>
        Geschlecht: ${abgeordneter.getGeschlecht()}<br/>
        Religion: ${abgeordneter.getReligion()}<br/>
        Beruf: ${abgeordneter.getBeruf()}<br/>
        Kurzvita: ${abgeordneter.getVita()}
    </div>
</div>

<div style="padding: 20px;">
    <h2>Mandate</h2>
    <#list abgeordneter.getMandate() as mandat>
        ${mandat}<br/>
    <#else>
        <em>Keine Mandate bekannt.</em>
    </#list>
</div>

<div style="padding: 20px;">
    <h2>Mitgliedschaften</h2>
    <#list abgeordneter.getMitgliedschaften() as mitgliedschaft>

        <#list mitgliedschaft as item>
            <#if item?index == 0>
                <strong>${item}</strong>
            <#else>
                &nbsp&nbsp&nbsp&nbsp${item}
            </#if>
            <br/>
        </#list>
    <#else>
        <em>Keine Mitgliedschaften bekannt.</em>
    </#list>
</div>

<div style="padding: 20px;">
    <h2>Reden</h2>
    <#list abgeordneter.getRedeIDs() as redeID>
        <a href="/rede?id=${redeID}">${redeID}</a>
    <#else>
        <em>Keine Reden aufgezeichnet.</em>
    </#list>
</div>

</body>

</html>
