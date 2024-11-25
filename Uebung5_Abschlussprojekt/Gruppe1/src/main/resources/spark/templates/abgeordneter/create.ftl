<html lang="de">
<head>
    <title>Neuen Abgeordneten erstellen</title>
    <script src="/abgeordneter-scripts/create.js"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>
${header}

<div style="text-align: center;">
    <h1 class="box">Neuen Abgeordneten erstellen</h1>
</div>

<div style="padding: 20px;">
    <p>
        Es muss zunächst nur die ID erstellt werden.<br/>
        Alles Weitere folgt im Bearbeitungsfenster.
    </p>
    <form onsubmit="return false;">
        <label for="input_id">ID</label>
        <input type="text" id="input_id">
        <br/>

        <button onclick="createAbgeordneter()">Erstellen</button>
    </form>
</div>

</body>

</html>
