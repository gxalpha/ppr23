<html lang="de">
<head>
    <title>NLP-Management</title>
    <script src="/nlp.js" defer></script>
</head>

<body>
<div>
    <h1>NLP-Management</h1>
</div>
<#if isDeployment>
    <div>
        <em>
            NLP analysis is disabled on the server.<br/>
            You can still press start/stop, but this will not actually analyze anything.<br/><br/>
        </em>
    </div>
</#if>
<div id="startup_progress_bar_container">
    <label for="startup_progress_bar">NLP backend is still starting up! Please stand by...</label>
    <br/>
    <progress id="startup_progress_bar"></progress>
</div>

<div id="controls_container">
    <button id="start" onclick="startNlp()">Start</button>
    <button id="stop" onclick="stopNlp()">Stop</button>
</div>

<div id="progress_bar_container">
    <label for="progress_bar">Fortschritt der NLP-Analyse:</label>
    <progress id="progress_bar" value="0" max="100"></progress>
    <span id="progress_bar_text">0%</span>
</div>

</body>

</html>
