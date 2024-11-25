<html lang="de">
<head>
    <title>Bearbeite ${abgeordneter.getNameFormatted()} (ID: ${abgeordneter.getID()})</title>
    <!-- Third time's the charm. -->
    <script>let abgeordneterID = "${abgeordneter.getID()}"</script>
    <script src="/abgeordneter-scripts/edit.js"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>
${header}

<div style="text-align: center;">
    <h1 class="box">Bearbeite Daten von ${abgeordneter.getNameFormatted()}</h1>
</div>

<div style="padding: 20px;">
    <h2>Stammdaten</h2>
    <form onsubmit="return false;">
        <label for="input_nachname">Nachname</label>
        <input type="text" id="input_nachname" value="${abgeordneter.getNachname()}">
        <br/>

        <label for="input_vorname">Vorname</label>
        <input type="text" id="input_vorname" value="${abgeordneter.getVorname()}">
        <br/>

        <label for="input_namenspraefix">Namenspraefix</label>
        <input type="text" id="input_namenspraefix" value="${abgeordneter.getNamenspraefix()}">
        <br/>

        <label for="input_adelssuffix">Adelssuffix</label>
        <input type="text" id="input_adelssuffix" value="${abgeordneter.getAdelssuffix()}">
        <br/>

        <label for="input_anrede">Anrede</label>
        <input type="text" id="input_anrede" value="${abgeordneter.getAnrede()}">
        <br/>

        <label for="input_geburtsdatum">Geburtsdatum</label>
        <input type="date" id="input_geburtsdatum" value="${abgeordneterGeburtsdatumString}">
        <br/>

        <label for="input_geburtsort">Geburtsort</label>
        <input type="text" id="input_geburtsort" value="${abgeordneter.getGeburtsort()}">
        <br/>

        <label for="input_geschlecht">Geschlecht</label>
        <input type="text" id="input_geschlecht" value="${abgeordneter.getGeschlecht()}">
        <br/>

        <label for="input_religion">Religion</label>
        <input type="text" id="input_religion" value="${abgeordneter.getReligion()}">
        <br/>

        <label for="input_beruf">Beruf</label>
        <input type="text" id="input_beruf" value="${abgeordneter.getBeruf()}">
        <br/>

        <label for="input_vita">Kurzvita</label>
        <input type="text" id="input_vita" value="${abgeordneter.getVita()}">
        <br/>
        <br/>

        <label for="input_partei">Partei</label>
        <input type="text" id="input_partei" value="${abgeordneter.getPartei()}">
        <br/>

        <label for="input_fraktion">Fraktion</label>
        <input type="text" id="input_fraktion" value="${abgeordneter.getFraktion()}">
        <br/>

        <br/>

        <button onclick="sendStammdaten()">Speichern</button>
    </form>
</div>

<!-- TODO: Mandate, Mitgliedschaften -->

</body>

</html>
