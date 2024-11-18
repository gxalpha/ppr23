<html lang="de">

<head>
    <link rel="stylesheet" href="/css/stammdatenblatt.css">
</head>

<body>

<div><h2>Stammdatenblatt von ${vorname} ${nachname} (${partei})</h2></div>

<div>
    <div style="text-align: center;"><h3 class="box">Personenbezogene Daten</h3></div>
    <p></p>
    <table>
        <tr>
            <td><b>Anrede</b></td>
            <td>${anrede}</td>
        </tr>
        <tbody>
        <tr>
            <td><b>Akademischer Titel&nbsp&nbsp&nbsp&nbsp&nbsp </b></td>
            <td>${akadTitel}</td>
        </tr>
        <tr>
            <td><b>Geburtsdatum</b></td>
            <td>${geburtsdatum}</td>
        </tr>
        <tr>
            <td><b>Geburtsort</b></td>
            <td>${geburtsort}</td>
        </tr>
        <tr>
            <td><b>Sterbedatum</b></td>
            <td>${sterbedatum!""}</td>
        </tr>
        <tr>
            <td><b>Geschlecht</b></td>
            <td>${geschlecht}</td>
        </tr>
        <tr>
            <td><b>Religion</b></td>
            <td>${religion}</td>
        </tr>
        <tr>
            <td><b>Beruf</b></td>
            <td>${beruf}</td>
        </tr>

        </tbody>
    </table>
</div>

<div>
    <#list mandate as mandat>
        <p></p>
        <div style="text-align: center;"><h3 class="box">Wahlperiode ${mandat.getWahlperiode().getNumber()}</h3></div>
        <p></p>
        <table>
            <#if mandat.getTyp().toString() == "LANDESLISTE">
                <tr>
                    <td><b>Mandatsart</b></td>
                    <td>Listenwahl</td>
                </tr>
                <tr>
                    <td><b>Liste</b></td>
                    <td>${mandat.getListe()}</td>
                </tr>

            <#elseif mandat.getTyp().toString() == "VOLKSKAMMER">
                <tr>
                    <td><b>Mandatsart</b></td>
                    <td>Volkskammer</td>
                </tr>
            <#else>
                <tr>
                    <td><b>Mandatsart</b></td>
                    <td>Direktwahl</td>
                </tr>
                <tr>
                    <td><b>Wahlkreis</b></td>
                    <td>${mandat.getWahlkreis().getNumber()}</td>
                </tr>
            </#if>
            <tr>
                <td><b>Zeitraum</b></td>
                <td>${mandat.fromDate()} - ${mandat.toDate()!"heute"}</td>
            </tr>
            <tr>
                <td><b>Gruppenzugehörigkeiten&nbsp&nbsp&nbsp&nbsp&nbsp</b></td>
                <td>${partei} (Partei)</td>
            </tr>
            <#list mandat.listGruppen() as g>
                <#if g != partei>
                    <tr>
                        <td></td>
                        <td>${g}</td>
                    </tr>
                </#if>
            </#list>
        </table>
    </#list>
</div>
<div>&nbsp</div>

<div>
    <div style="text-align: center;"><h3 class="box">Alle erfassten Bundestagsreden von ${vorname} ${nachname}</h3>
    </div>
    <p><i>Hinweis: Um zu den NLP-Ergebnissen der jeweiligen Rede zu gelangen, einfach den entsprechenden Button
            anklicken!</i></p>
    <br>
    <#list reden as rede>
        <form method="GET"
              action="/InsightBundestag/reden/<#setting number_format="0"/>${rede.getID()}<#setting number_format=""/>">
            <div style="text-align: center">
                <button type="submit" class="button" style="font-family: monospace, sans-serif; font-size: 11pt">
                    Bundestagsrede vom ${rede.getDate()} mit
                    ID<#setting number_format="0"/>${rede.getID()}<#setting number_format=""/></button>
            </div>
        </form>
    </#list>
</div>


</body>

</html>