<html lang="de">
<head>
    <title>Log</title>
</head>

<body>
<div>
    <h1>Log</h1>
</div>

<#list logEntries>
    <ul>
        <#items as logEntry>
            <li>${logEntry.getTimestamp()}: ${logEntry.getRoute()} - ${logEntry.getParamsFormatted()}
            </li>
        </#items>
    </ul>
<#else>
    <em>Keine Log-Items vorhanden.</em>
</#list>

</body>

</html>
