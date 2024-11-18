# PPR Übung 4

Dieses Projekt implementiert Übung 4 des PPR 2023/24.

Der Webserver wird über die Main-Methode der Klasse `Main` ausgeführt.
Es wird dabei vorausgesetzt, dass Docker am Laufen ist.
Diese Voraussetzung kann dadurch umgangen werden, ein beliebiges Programm-Argument (aber keine Zahl!) zu übergeben.
Der Port des Webservers ist 8001, die Hauptseite ist dann also http://localhost:8001.

Das Projekt wurde auch auf `[REDACTED]` deployed und kann dort verwendet werden, sollte es
lokal nicht funktionieren.
(Einziger Unterschied: Es läuft mit einer anderen MongoDB-Instanz und die NLP-Analyse kann nicht gestartet werden, um
den Server zu schonen).

## Aufgabe 1

a) Ein Use-Case-Diagramm sowie die Klassenstruktur des Projekts ist im UML-Diagramm in der
Datei [UseCase-Klassendiagramm.png](UseCase-Klassendiagramm.png) zu finden.
Aufgrund der Größe des Diagramms lässt sich dieses nicht sinnvoll in die README.md selbst einbinden.
*Hinweis im Nachhinein: Auf dieses hochqualitative (und sich zu dem in Übung 3 wirklich stark verschiedene) Diagramm hat es bei der Bewertung Abzug gegeben. Ich kann gar nicht verstehen warum :P*

b) Die Mockups für die Website befinden sich in der Datei [Mockups.pdf](Mockups.pdf). *Hinweis im Nachhinein: Auch hier gab es unerklärlicherweise bei der Bewertung Abzug :P*

## Aufgabe 2

a) Die Daten von Übungsblatt 3 wurden fast unverändert weiterverwendet.
Es wurde aber darauf geachtet, dass das Projekt alleinstehend ist:
Die `data.BundestagFactory` kann weiterhin alle Dateien einlesen.
Dies geschieht in der Main-Methode der Klasse `core.DatabaseReset`, welche dann die Stammdaten und Reden füllt.
Dies ist aber nicht zu empfehlen, da dadurch Teile der Redeanalysen verloren gehen - alle Daten sind bereits in der
Datenbank abgelegt.

Die CAS-Repräsentation von einer Rede wird mit `XmiCasSerializer` serialisiert und in der Datenbank abgelegt.

b) `Rede` wurde um `toCAS()` erweitert.

c) Die Verarbeitung wird lokal durchgeführt, relevant ist hier die `nlp.DUUIHelper`-Klasse.
Die Docker-Images müssen lokal zunächst gepulled werden, falls noch nicht geschehen (die genauen Versionen sind
relevant, die in der Aufgabenstellung gegebenen sind teils sehr viel langsamer):

```sh
docker pull docker.texttechnologylab.org/textimager-duui-spacy-single-de_core_news_sm:0.1.4
docker pull docker.texttechnologylab.org/gervader_duui:1.0.2
```

Die angegebenen Daten werden in `nlp.NLPAnalyzer` extrahiert und - sofern benötigt - gespeichert.

d) Siehe Datenbank und Website.

e) Wie in a) erwähnt wird die CAS-Repräsentation mit allen Annotationen serialisiert und gespeichert.

f,g) Die extrahierten NLP-Daten sind zusätzlich in Form des `RedeNLP`-Interfaces gespeichert und in der Datenbank
hochgeladen.
Damit lassen sich die Merkmale einfach auslesen.

## Aufgabe 3

a) Die Routen sind in `website.SparkHelper` implementiert. Einige statische Dateien (CSS, JavaScript) sind
in `resources/spark_static` und als statische Routen eingebaut.

b) Die FreeMarker-Templates sind in `resources/freemarker_template` zu finden.

c) Die gekapselten MongoDB-Abfragen sind in `MongoDBConnectionHandler`.

d-i) In der `/nlp`-Route ist für die Anzeige sowie Start und Stop zuständig.

d-ii) Das Log-Panel ist in `/log`.
Start- und Stop-Aktionen des Nutzers werden geloggt und dort angezeigt.

e) In `/reden` gibt es die Statistik der Sentiment-Werte.
In Absprache mit Herrn `[REDACTED]` (im Tutorium) wird hier ein Boxplot anstatt von Tortendiagrammen verwendet.
Intervalskalierte Datensätze lassen sich mit Tortendiagrammen schlecht darstellen - in Boxplots ist dies viel besser
möglich, und die Fraktionen lassen sich einfacher miteinander vergleichen.

f) Der Zeitfilter ist optionalerweise auf der Startseite einstellbar.

## Dokumentation

Alle Klassen und Methoden sind mit JavaDoc dokumentiert.
Die Dokumentation kann mit IntelliJ generiert werden.
