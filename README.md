# Mathe Dual Wettbewerb sponsored by DSA Daten- und Systemtechnik GmbH

## Dateien und Verzeichnisse in diesem Repo

- `Aufgabenstellung.pdf` (die Aufgabenstellung mit Erklaerungen dazu)
- `README.md` (diese Datei)
- `checker.jar` (compiliertes Hilfsprogramm zur Loesungspruefung)
- `input_files` (Verzeichnis mit den Eingabedateien zu den Testfaellen)
- `md-2022-example.zip` (Beispielcode in Java, mit dem man gueltige, aber schlechte Loesungen erzeugt)
- `result_files` (Die Loesungen die eingereicht wurden bzw. werden)
- `test.sh` (Hilfsskript fuer den CI Job um die Loesungen zu testen)
- `.gitlab-ci.yml` (CI Job Definition zum testen und Uebermitteln an den Analyser)

## Verwendung dieses Repos
Dieses Repo dient in erster Linie dazu, Ihnen die Aufgabenstellung und die Testfaelle zur Verfuegung zu stellen und Ihnen im
Gegenzug die Moeglichkeit zu geben, Ihre Loesungen einzureichen. Im Verzeichnis `result_files` haben wir zu Beginn fuer
alle Testfaelle gueltige, aber eher schlechte Loesungen als Beispiel fuer Sie hinterlegt. Im Prinzip brauchen Sie diese
nur durch Ihre Loesung zu ersetzen, committen und pushen. Die neuen Loesungen werden dann geprueft und - falls Sie in Ordnung
sind - an den Analyser uebergeben. Der Analyzer wertet diese dann aus und generiert daraus einige aussagekraeftige Bilder,
Videos und Grafiken.

Die aktuelle Auswertung aller Loesungen aller Teams findet man unter
https://wettbewerb.mathe-dual.de.
Die Seite wird nach jeder Aenderung zeitnah aktualisiert. Die Idee dahinter ist, dass ueber die Zeit des Wettbewerbs (ca. 
4 Wochen) die von Ihnen gefundenen Loesungen durch Inspektion der Auswertungseite analysiert und diskutiert werden, um 
diese dann schrittweise weiter zu verbessern.

**Bitte beachten Sie, dass nicht vorgesehen ist, dass Sie Dateien ausserhalb des `result_files` Verzeichnis aendern!**
Sollten dort Aenderungen noetig werden, darf das nur durch den Veranstalter durchgefuehrt werden. Sollten Sie ihren
Quelltext ebenfalls in diesem Repo verwalten wollen, dann duerfen Sie ein Verzeichnis mit dem Namen `src` hinzufuegen, in
dem Sie machen koennen, was Sie wollen. Die anderen Teams haben keinen Zugriff auf Ihr Repo.:D

## Uebergabe und Teilnehmer
Am Tage des KickOff Meetings wird vom Veranstalter fuer jedes Team je ein Repo zur Verfuegung gestellt. Um die 
Registrierung nicht ausufern zu lassen, uebergeben wir das Repo zunaechst nur an eine Person, die Teamleitung. Die
Teamleitung sollte schon einen GitLab Zugang haben und wird von uns berechtigt, die weiteren Teammitglieder selbst dem Repo
hinzuzufuegen. Die Teamleitung verpflichtet sich dazu, dafuer zu sorgen, dass alle Teammitglieder den Zugang erhalten.
Natuerlich kann sie sich an den Veranstalter wenden, wenn es dabei Probleme gibt.

Ausserdem sollen die Teilneher noch in der folgenden Tabelle (hier im README.md) ergaenzt werden (die fuer diesen Zweck
natuerlich geandert werdem darf).
