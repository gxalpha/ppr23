<html lang="de">
<head>
    <title>Login</title>
    <script src="/bcrypt.js" charset="utf-8"></script>
    <script src="/password.js" charset="utf-8"></script>
    <!-- This is hacky af, I'm sure there's a better way. -->
    <script>let redirectPage = "${redirect_page}"</script>
    <script src="/login.js"></script>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">

</head>

<body>
${header}


<div style="text-align: center;">
    <h1 class="box">Login</h1>
</div>

<div style="padding: 20px;">
    <form onsubmit="return false;">
        <label for="input_username">Benutzername</label>
        <input type="text" name="username" id="input_username">
        <br/>

        <label for="input_password">Passwort</label>
        <input type="password" name="password" id="input_password">
        <br>

        <button onclick="login()">Login</button>
    </form>
</div>

</body>

</html>
