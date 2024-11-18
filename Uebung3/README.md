# Aufgabenstellung

*Die Aufgabenstellungen wurden paraphrasiert. Sie sind eine Kurzzusammenfassung und definitiv nicht vollständig. Das originale Dokument kann aus rechtlichen Gründen nicht geteilt werden.*

Die in Übung 2 erstellte Lösung soll um Bundestagsreden (und Kommentare in diesen) erweitert werden. Die Interfaces aus Übung 2 können, müssen aber nicht weitergenutzt werden.

Es soll eine Anbindung an eine MongoDB-Datenbank erstellt werden.
Die zugehörige Klasse soll `MongoDBConnectionHandler` heißen und alle Datenbankoperationen (Hinzufügen, Auslesen, Updaten, Löschen, etc) unterstützen.
Dazu soll eine Konfigurationsdatei mit einer Hilfsklasse (die von `java.util.Properties` erbt) eingelesen und verwendet werden, um die Zugangsdaten zu bekommen.

Es soll Interfaces für Abgeordnete, Reden, Abgeordnete, Tagesordnungen und Sitzungen geben.
Diese sollen jeweils einzeln für Objekte aus Dateien und der Datenbank erstellt werden.
Die Stammdaten (von Blatt 2), sowie die gegebenen Protokolle sollen eingelesen und in die Datenbank geladen werden.

Am Ende sollen einige Fragen mithilfe der Datenbank beantwortet werden, z.B. wer wie viele Reden gehalten hat, was für Begriffe auftauchen, wie lang Reden und Sitzungen sind, etc.
Das alles soll es auch nach Datum gefiltert geben.

Wie immer braucht es auch Use-Case, Paket- und Klassendiagramm, README, bla bla bla.
