<html lang="de">
<head>
    <title>Session-Management</title>
    <script src="/session-admin.js" defer></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <h1 class="box">Session-Management</h1>
</div>

<div style="padding: 20px;">
    <div id="startup_progress_bar_container">
        <label for="startup_progress_bar">Session parser backend (including NLP) is still starting up! Please stand
            by...</label>
        <br/>
        <progress id="startup_progress_bar"></progress>
    </div>

    <div id="controls_container">
        <button id="start" onclick="startProtocols()">Start</button>
        <button id="stop" onclick="stopProtocols()">Stop</button>
    </div>

    <div id="progress_bar_container">
        <label for="progress_bar">Gesamtfortschritt der Protokoll-Analyse:</label>
        <progress id="progress_bar" value="0" max="100"></progress>
        <span id="progress_bar_text">0%</span>
    </div>

    <div id="detailed_progress_container">
        Derzeit wird analysiert: <span id="detailed_progress">Unbekannt</span>
    </div>
</div>

</body>

</html>
