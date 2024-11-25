<html lang="de">
<head>
    <title>Sitzung ${sitzung.getSitzungsnummer()}</title>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">

</head>

<body>

${header}

<div style="padding: 20px;">
    <div style="text-align: center;">
        <h1 class="box">Sitzung ${sitzung.getSitzungsnummer()}</h1>
    </div>

    <div style="padding: 20px;">
        <a href="/sitzung/vorschau?id=${id}">
            <button>Zu PDF</button>
        </a>
    </div>

    <div>
        <h2>Tagesordnungspunkte</h2>
        <div>
            <#list tagesordnungspunkte as tagesordnungspunkt>
                <div>
                    <h3>${tagesordnungspunkt.getThema()}</h3>
                    <#list tagesordnungspunkt.getRedenIDs() as rede>
                        <div>
                            <a href="/rede?id=${rede}">${rede}</a>
                        </div>
                    <#else>
                        <em>Keine Reden in diesem Tagesordnungspunkt bekannt.</em>
                    </#list>
                </div>
            <#else>
                <em>Keine Tagesordnungspunkte bekannt.</em>
            </#list>
        </div>
    </div>
</div>

</body>

</html>
