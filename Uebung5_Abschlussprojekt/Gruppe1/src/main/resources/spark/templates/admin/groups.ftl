<html lang="de">
<head>
    <title>Gruppenadministration</title>
    <script src="/groups.js" charset="utf-8"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <h1 class="box">Gruppenadministration</h1>
</div>

<div style="padding: 20px;">
    <h2>Alle Gruppen</h2>
    <ul>
        <#list existingGroups as group>
            <li>${group.getName()}
                <#list group.getPermissions()>
                    <br>
                    <ul>
                        <#items as permission>
                            <li>${permission}</li>
                        </#items>
                    </ul>
                </#list>
            </li>
        </#list>
        <li>webmaster <em>(all permissions, special)</em></li>
    </ul>
</div>

<div style="padding: 20px;">
    <h2>Gruppe erstellen</h2>
    <form onsubmit="return false;">
        <label for="input_create">Name der Gruppe</label>
        <input type="text" name="group_name" id="input_create">
        <br/>

        <button onclick="createGroup()">Erstellen</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Gruppe löschen</h2>
    <form onsubmit="return false;">
        <label for="input_delete">Name der Gruppe</label>
        <input type="text" name="group_name" id="input_delete">
        <br/>

        <button onclick="deleteGroup()">Löschen</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Gruppenberechtigungen vergeben</h2>
    <form onsubmit="return false;">
        <label for="input_grantperm_name">Gruppe</label>
        <select id="input_grantperm_name">
            <#list existingGroups as group>
                <option>${group.getName()}</option>
            </#list>
        </select>
        <br/>

        <label for="input_grantperm_permission">Berechtigung</label>
        <select id="input_grantperm_permission">
            <#list allPermissions as permission>
                <option>${permission}</option>
            </#list>
        </select>
        <br/>

        <button onclick="grantPermission()">Berechtigung vergeben</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Gruppenberechtigungen entfernen</h2>
    <form onsubmit="return false;">
        <label for="input_revokeperm_name">Grant</label>
        <select id="input_revokeperm_name">
            <#list existingGroups as group>
                <option>${group.getName()}</option>
            </#list>
        </select>
        <br/>

        <label for="input_revokeperm_permission">Berechtigung</label>
        <select id="input_revokeperm_permission">
            <#list allPermissions as permission>
                <option>${permission}</option>
            </#list>
        </select>
        <br/>

        <button onclick="revokePermission()">Berechtigung entfernen</button>
    </form>
</div>


</body>

</html>
