<html lang="de">
<head>
    <title>Account</title>
    <script src="/bcrypt.js" charset="utf-8"></script>
    <script src="/password.js" charset="utf-8"></script>
    <!-- Mega hack, used for a second time now. -->
    <script>let username = "${user.getUsername()}"</script>
    <script src="/profile.js"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">
</head>

<body>
${header}


<div style="text-align: center;">
    <h1 class="box">Profil: ${user.getUsername()}</h1>
</div>

<div style="padding: 20px;">
    <h2>Gruppen</h2>
    <#list user.getGroups()>
        <ul>
            <#items as group>
                <li>${group}</li>
            </#items>
        </ul>
    <#else>
        <em>Dieser Account ist in keinen Gruppen.</em>
    </#list>
</div>

<div style="padding: 20px;">
    <h2>Passwort ändern</h2>
    <form onsubmit="return false;">
        <label for="input_changepassword_old">Altes Passwort</label>
        <input type="password" id="input_changepassword_old">
        <br>

        <label for="input_changepassword_new">Neues Passwort</label>
        <input type="password" id="input_changepassword_new">
        <br>

        <button onclick="changePassword()">Passwort ändern</button>
    </form>
</div>

</body>

</html>
