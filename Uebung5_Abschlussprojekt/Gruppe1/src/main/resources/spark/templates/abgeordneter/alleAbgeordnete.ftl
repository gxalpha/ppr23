<html>
<head>
    <link rel="stylesheet" href="/css/reden.css">
    <link rel="stylesheet" href="/css/header.css">
</head>
<body>
${header}
<div style="padding: 20px;">
    <div style="text-align: center;"><h3 class="box">Liste aller erfassten Abgeordneten (Anzahl: ${anzahlErgebnisse})</h3>
    </div>
    <br>
    <#list abgeordnete as a>
        <div>
            <h3>
                <a href="/abgeordneter?id=${a[0]}"> ID${a[0]} - ${a[1]} ${a[2]} </a>
            </h3>
        </div>
    </#list>
</div>
</body>
</html>