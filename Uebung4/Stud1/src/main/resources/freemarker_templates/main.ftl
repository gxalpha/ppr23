<html lang="de">
<head>
    <title>${app_name}</title>
</head>

<body>
<div>
    <h1>${app_name}</h1>
</div>

<div>
    <h2>Suche nach Abgeordneten</h2>
    <form action="/abgeordneter/search">
        <h3>Nach Name</h3>
        <label for="vorname">Vorname</label>
        <input type="text" name="vorname" id="vorname"/>
        <br/>
        <label for="nachname">Nachname</label>
        <input type="text" name="nachname" id="nachname"/>
        <br/>
        <button type="submit">Suche</button>
    </form>
    <form action="/abgeordneter">
        <h3>Nach ID</h3>
        <label for="rede_id">ID</label>
        <input type="text" name="id" id="rede_id"/>
        <button type="submit">Suche</button>
    </form>
</div>

<div>
    <h2>Reden</h2>
    <h3>Übersicht</h3>
    <form action="/reden">
        <em>Optionaler Filter für Zeitraum:</em><br>
        <label for="reden_from">Von: </label>
        <input type="date" name="from" id="reden_from"/><br/>
        <label for="reden_from">Bis: </label>
        <input type="date" name="until" id="reden_until"/><br/>
        <button type="submit">Gehe zur Übersicht</button>
    </form>
    <h3>Suche nach Reden</h3>
    <form action="/rede">
        <label for="rede_id">Rede-ID</label>
        <input type="text" name="id" id="rede_id"/>
        <button type="submit">Suche</button>
    </form>
</div>

<div>
    <h2>NLP-Management</h2>
    <form action="/nlp">
        <button type="submit">Gehe zum NLP-Management</button>
    </form>
</div>

<div>
    <h2>Log</h2>
    <form action="/log">
        <button type="submit">Gehe zum Log</button>
    </form>
</div>
</body>

</html>
