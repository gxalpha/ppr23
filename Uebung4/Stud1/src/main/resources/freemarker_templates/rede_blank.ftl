<html lang="de">
<head>
    <title>Rede ${rede.getID()} von ${redner.getNameFormatted()}</title>
</head>

<body>
<div>
    <h1><a href="abgeordneter?id=${redner.getID()}">${redner.getNameFormatted()}, ${redner.getPartei()}</a>:
        Rede ${rede.getID()}</h1>
</div>

<div>
    <em>Diese Rede wurde noch nicht NLP-analysiert.</em>
</div>

<div>
    <h2>Text</h2>
    ${rede.getText()}
</div>

</body>

</html>
