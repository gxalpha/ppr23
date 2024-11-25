<html lang="de">
<head>
    <title>Suchergebnisse für ${searchQuery}</title>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <h1 class="box">Suchergebnisse für ${searchQuery}</h1>
</div>

<div style="padding: 20px;">
    <#list abgeordnete>
        <ul>
            <#items as abgeordneter>
                <li><a href="/abgeordneter?id=${abgeordneter.getID()}">${abgeordneter.getNameFormatted()}</a></li>
            </#items>
        </ul>
    <#else>
        <em>Keine Abgeordneten für diese Suche gefunden.</em>
    </#list>
</div>

</body>

</html>
