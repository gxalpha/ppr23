<html lang="de">
<head>
    <title>Suchergebnisse für ${searchQuery}</title>
</head>

<body>
<div>
    <h1>Suchergebnisse für ${searchQuery}</h1>
</div>

<div>
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
