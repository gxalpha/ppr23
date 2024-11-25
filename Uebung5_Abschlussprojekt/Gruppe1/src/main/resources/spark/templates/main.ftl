<html lang="de">
<head>
    <title>Parliament Browser</title>
    <link rel="stylesheet" href="/css/main.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div class="box">
    <div>
        <h2>Suche nach Abgeordneten</h2>
        <form action="/abgeordneter/search">
            <h4>Nach Name des Abgeordneten</h4>
            <label for="vorname">Vorname</label>
            <input type="text" name="vorname" id="vorname"/>
            <br/>
            <label for="nachname">Nachname</label>
            <input type="text" name="nachname" id="nachname"/>
            <br/>
            <button type="submit">Suche</button>
        </form>
        <form action="/abgeordneter">
            <h4>Nach Abgeordneten ID</h4>
            <label for="abgeordneter_id">ID</label>
            <input type="text" name="id" id="abgeordneter_id"/>
            <button type="submit">Suche</button>
        </form>
    </div>

    <h2>Volltextsuche nach einer Rede</h2>
    <p>Bitte Text nach dem gesucht werden soll eingeben:</p>
    <form method="GET" action="/volltextsuche">
        <label>
            <input type="text" name="suchtext" placeholder="Text der Rede"/>
        </label>
        <button type=submit class="button"><b>Suchen</b></button>
    </form>
    <div>
        <h2>Visualisierung der Bundestagsreden</h2>
        <form action="/reden">
            <p>Über welchen Zeitraum möchtest du die Reden visualisiert haben?</p>
            <label for="reden_from"> von:  </label>
            <input type="date" name="von" id="reden_from" value=""/>
            <label for="reden_from"> bis: </label>
            <input type="date" name="bis" id="reden_until" value=""/>
            <button type="submit"><b>Los!</b></button>
        </form>

    </div>
</div>

</body>

</html>
