<html lang="de">
<head>
    <title>Übersicht zu ${abgeordneter.getNameFormatted()}</title>
</head>

<body>
<div>
    <h1>${abgeordneter.getNameFormatted()}, ${abgeordneter.getPartei()}</h1>
</div>

<div>
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

<div>
    <h2>Mandate</h2>
    <#list abgeordneter.getMandate() as mandat>
        ${mandat}<br/>
    </#list>
</div>

<div>
    <h2>Mitgliedschaften</h2>
    <#list abgeordneter.getMitgliedschaften() as mitgliedschaft>

        <#list mitgliedschaft as item>
            <#if item?index == 0>
                <strong>${item}</strong>
            <#else>
                &nbsp&nbsp&nbsp&nbsp${item}
            </#if>
            <br/>
        <#else>
            <em>Keine Mitgliedschaften bekannt.</em>
        </#list>
    </#list>
</div>

<div>
    <h2>Reden</h2>
    <#list abgeordneter.getRedeIDs() as redeID>
        <a href="/rede?id=${redeID}">${redeID}</a>
    <#else>
        <em>Keine Reden aufgezeichnet.</em>
    </#list>
</div>

</body>

</html>
