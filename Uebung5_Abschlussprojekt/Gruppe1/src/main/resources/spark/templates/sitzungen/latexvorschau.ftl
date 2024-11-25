<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>PDF-Vorschau</title>
    <link rel="stylesheet" href="/css/protocol.css">
    <link rel="stylesheet" href="/css/header.css">

</head>
<body>
${header}

<div style="text-align: center;">
    <h1 class="box">Protokoll-Downloader</h1>
</div>

<div style="padding: 20px;">
    <a href="/sitzung/${sitzung_id}.pdf" download>
        <button>Datei herunterladen</button>
    </a>
</div>

<!-- iframe für die PDF-Vorschau -->
<object data="/sitzung/${sitzung_id}.pdf" type="application/pdf" width="100%" height="600px">
    <p>Link zum PDF: <a href="/sitzung/${sitzung_id}.pdf">to the PDF!</a></p>
</object>

</body>
</html>
