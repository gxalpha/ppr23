<!-- author: Stud -->

<html lang="de">
<head>
    <title>Rede ${rede.getID()}</title>
    <link rel="stylesheet" href="/css/reden.css">
    <link rel="stylesheet" href="/css/visualizeReden.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>
${header}

<div style="text-align: center;">
    <h1 class="box">Rede mit ID ${rede.getID()} vom ${rede.getDatum()?date}</h1>
</div>

<div style="padding: 20px;">
    <div>
        <h2>Redner</h2>
        <div class="flex-container">
            <div>
                Name: <a href="/abgeordneter?id=${rede.getRednerID()}">${redner.getNameFormatted()}</a> <br/>
                Partei: ${redner.getPartei()}<br/>
                Geschlecht: ${redner.getGeschlecht()}<br/>
            </div>
            <div class="fotoContainer">
                 <img src="${foto}" alt="Foto des Abgeordneten">
            </div>
        </div>
    </div>

    <div class="big-box-text">
        <h2>Inhalt</h2>

        <div class="legendBox">
            <span class="red-dot"></span> <span>Personen</span> <br/>
            <span class="blue-dot"></span> <span>Organisationen</span> <br/>
            <span class="green-dot"></span> <span>Orte</span> <br/>
        </div>

        <div id="text">
            <p>
                <#list helper.getFullText(rede) as line>
                    ${line} <br>
                </#list>
            </p>
        </div>

    </div>
</div>
</body>

</html>
