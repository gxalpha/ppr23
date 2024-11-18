<html lang="de">

<head>
    <link rel="stylesheet" href="/css/startseite.css">
    <!--Font for title -->
    <link href="https://db.onlinewebfonts.com/c/fd3936af66a016c1bc4b3980639fcac2?family=Melior+Bold" rel="stylesheet">
</head>

<body>

<div class="box">
    <div style="text-align: center; font-family: Melior Bold, sans-serif"><h1>Willkommen bei Insight Bundestag!</h1>
    </div>
    <br>

    <button class="collapsible"><b>Suche nach Bundestagsabgeordneten</b></button>
    <div class="content">
        <p>Über welchen Abgeordneten möchtest du weitere Informationen erhalten?</p>
        <form method="GET" action="/InsightBundestag/sucheAbgeordneterNachName">

            <label>
                <input type="text" name="vorname" placeholder="Vorname"/>
            </label>

            <label>
                <input type="text" name="nachname" placeholder="Nachname"/>
            </label>

            <button type="mySubmit"><b>Suchen</b></button>
        </form>
        <p>Alternativ kannst du den Abgeordneten über dessen ID finden.</p>
        <form method="GET" action="/InsightBundestag/sucheAbgeordneterNachId">
            <label>
                <input type="text" name="id" placeholder="ID"/>
            </label>
            <button type="mySubmit"><b>Suchen</b></button>
        </form>
        <p><i>Hinweis: Falls es keinen Treffer gibt, wirst du wieder auf diese Seite zurückgeleitet.</i></p>
    </div>
    <p></p>
    <button class="collapsible"><b>Suche nach Bundestagsreden</b></button>
    <div class="content">
        <p>Über welche Bundestagsrede möchtest du detaillierte Informationen erhalten?</p>
        <form method="GET" action="/InsightBundestag/sucheRedeNachId">

            <label>
                <input type="text" name="id" placeholder="ID"/>
            </label>

            <button type="mySubmit"><b>Suchen</b></button>
        </form>
        <p>Alternativ kannst du nach Bundestagsreden innerhalb eines gewissen Zeitraums suchen.</p>
        <form method="GET" action="/InsightBundestag/sucheRedenNachZeitraum">

            <label>
                <input type="text" name="start" placeholder="von dd.MM.YYYY"/>
            </label>

            <label>
                <input type="text" name="ende" placeholder="bis dd.MM.YYYY"/>
            </label>

            <button type="mySubmit"><b>Suchen</b></button>
        </form>

        <p><i>Hinweis: Das Startdatum muss vor dem Enddatum liegen und darf kein zukünftiges Datum sein. Bei einer
                Fehleingabe wirst du wieder auf diese Seite zurückgeleitet.</i></p>
    </div>
    <p></p>
    <button class="collapsible"><b>Berechnung der durchschnittlichen Sentiment-Werte einer Fraktion</b></button>
    <div class="content">
        <p>Für welche Fraktion und welchen Zeitraum möchtest du die durchschnittlichen Sentment-Werte der gehaltenen
            Bundestagsreden berechnen lassen?</p>
        <form method="GET" action="/InsightBundestag/fraktionen">
            <label>
                <select class="mySelect" name="fraktionsname" placeholder="Name der Fraktion">
                    <option disabled selected>-- Bitte eine Fraktion auswählen --</option>
                    <option value="LINKE">Fraktion DIE LINKE.</option>
                    <option value="SPD">Fraktion der Sozialdemokratischen Partei Deutschlands</option>
                    <option value="GRUENE">Fraktion BÜNDNIS 90/DIE GRÜNEN</option>
                    <option value="FDP">Fraktion der Freien Demokratischen Partei</option>
                    <option value="CDU-CSU">Fraktion der Christlich Demokratischen Union/Christlich - Sozialen Union
                    </option>
                    <option value="AFD">Alternative für Deutschland</option>
                    <option value="FLOS">Fraktionslos</option>
                </select>
            </label>
            <br>
            <label>
                <input type="text" name="start" placeholder="von dd.MM.YYYY"/>
            </label>

            <label>
                <input type="text" name="ende" placeholder="bis dd.MM.YYYY"/>
            </label>

            <button type="mySubmit"><b>Suchen</b></button>
        </form>
        <p><i>Hinweis: Die Berechnung kann ein paar Sekunden in Anspruch nehmen. Das Startdatum muss vor dem Enddatum
                liegen und darf kein zukünftiges Datum sein. Bei einer Fehleingabe wirst du wieder auf diese Seite
                zurückgeleitet.</i></p>
    </div>
    <p></p>
    <button class="collapsible" id="nlp"><b>Natural Language Processing</b></button>
    <div class="content">
        <p>Der aktuelle Fortschritt des NLP der Bundestagsreden beträgt 100%. Nach dem Starten werden alle erfassten
            Bundestagsreden mittels des DUUI <b>nachanalysiert</b>. Dies kann erforderlich sein, falls z.B. eine
            Topic-Analyse oder sonstige zusätzliche Daten zur Rede hinzugefügt werden sollen. Die NLP-Ergebnisse
            der einzelnen Reden werden dann in einer MongoDB abgespeichert.
        </p>
        <p id="hinweis" style="display: none"><i>Hinweis: Falls die Fortschrittsanzeige noch nicht sichtbar ist, das
                Collapsible bitte zu machen und dann wieder aufklappen.</i></p>
        <p id="abbruch" style="display: none"><i>Achtung: Bei Abbruch bleiben die alten NLP-Ergebnisse aller
                nicht-analysierten Reden in der MongoDB erhalten. Allerdings wird bei einem Neustart wieder bei der
                ersten Rede angefangen. Abbrechen auf eigene Gefahr!</i></p>

        <div class="progressBar" id="myProgressBar" style="display: none">
            <div id="currProgress" class="progressBar" style="height: 24px;width:50%;background-color: #9ACD32">
            </div>
        </div>

        <div id="progr" style="display: none; float: left; padding-top: 10px; padding-bottom: 10px">0.00%</div>
        <div id="time" style="display: none; float: right; padding-top: 10px; padding-bottom: 10px">00:00:00</div>

        <button class="startbtn" id="start" onclick="moveNLPBar()"><b>Starten</b></button>
        <button class="stopbtn" id="stop" onclick="stopNLP()"><b>Abbrechen</b></button>
    </div>
</div>
</body>

<script>
    /**
     * Schließt das gerade geöffnete Fenster, das so hoch- und heruntergehen kann
     *
     * Source: https://stackoverflow.com/questions/61216973/how-to-close-collapsible-div-when-opening-new-one
     */
    function toggleContent(content) {
        if (content.style.maxHeight) {
            content.style.maxHeight = null;
        } else {
            content.style.maxHeight = content.scrollHeight + 'px';
        }
    }

    /**
     * Schließt alle geöffneten Fenster, die so hoch- und heruntergehen können
     *
     * Source: https://stackoverflow.com/questions/61216973/how-to-close-collapsible-div-when-opening-new-one
     */
    function collapseAllOpenContent() {
        const colls = document.getElementsByClassName('collapsible');
        for (const coll of colls) {
            if (coll.classList.contains('active')) {
                coll.classList.remove('active');
                toggleContent(coll.nextElementSibling);
            }
        }
    }

    /**
     * Bringt die ausklappbaren Teile zum Laufen
     *
     * Source: https://stackoverflow.com/questions/61216973/how-to-close-collapsible-div-when-opening-new-one
     */
    function makeCollapsingButtonsWork() {
        const colls = document.getElementsByClassName('collapsible');
        for (const coll of colls) {
            coll.addEventListener('click', function () {
                if (!this.classList.contains('active')) {
                    collapseAllOpenContent();
                }
                this.classList.toggle('active');
                toggleContent(this.nextElementSibling);
            });
        }
    }

    makeCollapsingButtonsWork();

    /**
     * Ladebalken des NLP bewegen
     *
     */
    async function moveNLPBar() {
        await fetch("/InsightBundestag/nlp/start");
        await continueMovingNLPBar();
    }

    async function continueMovingNLPBar() {
        hideStartButton();
        var elem = document.getElementById("currProgress");
        var id = setInterval(frame, 500);

        async function frame() {
            var progress = (await (fetch("/InsightBundestag/nlp/progress")));
            var time = (await (fetch("./nlp/progress/passedTime")));
            var progressPerCent = await progress.text();
            var timeString = await time.text();
            if (progressPerCent === "100%") {
                elem.style.width = progressPerCent;
                document.getElementById("progr").innerHTML = progressPerCent;
                document.getElementById("time").innerHTML = "Vergangene Zeit: " + timeString;
                hideStopButton();
                clearInterval(id);

            } else {
                elem.style.width = progressPerCent.replace(",", ".");
                document.getElementById("progr").innerHTML = progressPerCent;
                document.getElementById("time").innerHTML = "Vergangene Zeit: " + timeString;
            }
        }
    }

    if ("${nlpProgress}" != "0.00%") {
        continueMovingNLPBar();
    }

    function hideStartButton() {
        document.getElementById("myProgressBar").style.display = "inline-block";
        document.getElementById("progr").innerHTML = "0.00%";
        document.getElementById("time").innerHTML = "00:00:00";
        document.getElementById("currProgress").style.width = "0%";
        document.getElementById("abbruch").style.display = "inline-block";
        document.getElementById("hinweis").style.display = "inline-block";
        document.getElementById("progr").style.display = "inline-block";
        document.getElementById("time").style.display = "inline-block";
        document.getElementById("start").style.display = "none";
        document.getElementById("stop").style.display = "inline-block";
    }

    function hideStopButton() {
        document.getElementById("myProgressBar").style.display = "none";
        document.getElementById("abbruch").style.display = "none";
        document.getElementById("hinweis").style.display = "none";
        document.getElementById("progr").style.display = "none";
        document.getElementById("time").style.display = "none";
        document.getElementById("start").style.display = "inline-block";
        document.getElementById("stop").style.display = "none";
    }

    function stopNLP() {
        fetch("/InsightBundestag/nlp/stop");
        hideStopButton();
    }
</script>

</html>