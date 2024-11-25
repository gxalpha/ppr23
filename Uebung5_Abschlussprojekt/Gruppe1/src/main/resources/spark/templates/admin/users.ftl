<html lang="de">
<head>
    <title>Benutzeradministration</title>
    <script src="/bcrypt.js" charset="utf-8"></script>
    <script src="/password.js" charset="utf-8"></script>
    <script src="/users.js" charset="utf-8"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>

${header}

<div style="text-align: center;">
    <h1 class="box">Benutzeradministration</h1>
</div>

<div style="padding: 20px;">
    <h2>Benutzerliste</h2>
    <#list existingUsers>
        <ul>
            <#items as user>
                <li>${user.getUsername()}
                    <#list user.getGroups()>
                        <br>
                        <ul>
                            <#items as group>
                                <li>${group}</li>
                            </#items>
                        </ul>
                    </#list>
                </li>
            </#items>
        </ul>
    <#else>
        <em>Es gibt anscheinend keine Nutzer...</em>
    </#list>
</div>

<div style="padding: 20px;">
    <h2>Benutzer erstellen</h2>
    <form onsubmit="return false;">
        <label for="input_username">Benutzername</label>
        <input type="text" name="username" id="input_create_username">
        <br/>

        <label for="input_password">Passwort</label>
        <input type="password" name="password" id="input_create_password">
        <br>

        <button onclick="createUser()">Erstellen</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Benutzer löschen</h2>
    <form onsubmit="return false;">
        <label for="input_username">Benutzername</label>
        <input type="text" name="username" id="input_delete_username">
        <br/>

        <button onclick="deleteUser()">Löschen</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Benutzer zu Gruppe hinzufügen</h2>
    <form onsubmit="return false;">
        <label for="input_addgroup_user">Benutzer</label>
        <select id="input_addgroup_user">
            <#list existingUsers as user>
                <option>${user.getUsername()}</option>
            </#list>
        </select>
        <br/>

        <label for="input_addgroup_group">Gruppe</label>
        <select id="input_addgroup_group">
            <#list existingGroups as group>
                <option>${group.getName()}</option>
            </#list>
        </select>
        <br/>

        <button onclick="addUserToGroup()">Zu Gruppe hinzufügen</button>
    </form>
</div>

<div style="padding: 20px;">
    <h2>Benutzer aus Gruppe entfernen</h2>
    <form onsubmit="return false;">
        <label for="input_removegroup_user">Benutzer</label>
        <select id="input_removegroup_user">
            <#list existingUsers as user>
                <option>${user.getUsername()}</option>
            </#list>
        </select>
        <br/>

        <label for="input_removegroup_group">Gruppe</label>
        <select id="input_removegroup_group">
            <#list existingGroups as group>
                <option>${group.getName()}</option>
            </#list>
        </select>
        <br/>

        <button onclick="removeUserFromGroup()">Aus Gruppe entfernen</button>
    </form>
</div>

</body>

</html>
