ClimateMonitoring 

1. SET UP Database

Prima di avviare l’applicazione, è necessario configurare il database PostgreSQL. 
Il procedimento da eseguire in maniera dettagliata è specificato sia nel manuale utente che tecnico.

 • Creare il database ClimateMonitoring
 • Usare il file di backup ClimMon-backup.sql presente dentro la directory data, ed eseguire lo script di quest'ultimo all'interno del querytool
 • Riempire la tabella coordinatemonitoraggio con il seguente script 

   COPY coordinatemonitoraggio ( id, nome_citta, stato, paese, latitudine, longitudine ) 
   FROM 'percorso\climate-monitoringB\data\DATI.csv' 
   DELIMITER ';' CSV HEADER;

 e sostituire dopo il FROM con la posizione effettiva di DATI.csv

 • Durante l'avvio del server verrà richiesto il numero di porta, username e password di postgre

Il programma applicativo cercherà automaticamente di stabilire una connessione con il database creato.


2. Installazione sdk 

Scaricare la versione compatibile al proprio sistema operativo delle sdk 23.0.> dal seguente link 

https://gluonhq.com/products/javafx/https://gluonhq.com/products/javafx/

Estrarre lo zip dentro 
C:\Program Files\

per l'esecuzione di javafx in maniera corretta

3 Avvio del server

Dopo aver estratto la cartella del progetto

 1. Aprire il prompt dei comandi e navigare fino alla directory del progetto:

 cd C:\percorso\climate-monitoringB\bin

 2. Una volta nella directory corretta, eseguire il comando per avviare il server:

   java --module-path "C:\Program Files\javafx-sdk-23.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar Server-ClimateMonitoring.jar

 3. Verranno richieste le credenziali di amministratore del server
 per autenticarsi correttamente. 
 • Local Host : numero di porta
 • Nome utente postgre.
 • Password postgre.

 Se le credenziali sono corrette, il serverCM rimarrà attivo e inizializzerà la connessione con il database.
 In caso contrario è necessario riavviare il server  

 Nel momento in cui si vuole chiudere l'applicazione, digitare sul tasto "disconnetti" chiudendo il servizio rmi e la connesione con il database
 


4 Avvio del client
 Dopo aver estratto la cartella del progetto

 1. Aprire il prompt dei comandi e navigare fino alla directory del progetto:

 cd C:\percorso\climate-monitoringB\bin

 2. Una volta nella directory corretta, eseguire il comando per avviare il server:

   java --module-path "C:\Program Files\javafx-sdk-23.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar Client-ClimateMonitoring.jar

 Nota : Se il server non è attivo, il client non partirà e verrà mostrato un messaggio di errore.

MAVEN
E' possibile sfruttare l'integrazione di Maven all'interno dell'ide Intellj, alternativamente si può scaricare in locale.

Installazione di Maven su Windows

1. Sul sito ufficiale https://maven.apache.org/download.cgi 
scegliere la versione binaria di Maven (ad es., "Binary zip archive") e scaricare il file .zip.

2. Estrarre il contenuto della cartella compressa in una directory a vostra scelta.

3. Andare su Pannello di controllo 
   > Sistema e sicurezza 
   > Sistema 
   > Impostazioni di sistema avanzate 
   > Variabili d'ambiente, e creare due nuove variabili d'ambiente:
	 a. M2_HOME che punta alla directory appena estratta 
	 b. PATH che punta alla medesima cartella del punto a

4. Verificare l'installazione aprendo il Prompt dei Comandi e digitare "mvn -v", se avete fatto tutto correttamente il risultato dovrebbe essere simile a questo:  
Apache Maven 3.9.9 (8e8579a9e76f7d015ee5ec7bfcdc97d260186937)
Maven home: C:\Users\samuele\OneDrive\Desktop\apache-maven-3.9.9
Java version: 19, vendor: Oracle Corporation, runtime: C:\Program Files\Java\jdk-19
Default locale: en_GB, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"

COMANDI MAVEN
Per compilare ed eseguire il Progetto:

4.1 Pulizia del Progetto, un comando di pulizia per rimuovere tutti i file compilati in precedenza: 

 mvn clean

4.2 Creazione del Pacchetto (Jar) Per creare un file JAR eseguibile del progetto: 
 
 mvn package 

 Questo comando creerà un file JAR nella cartella target del progetto.

4.3) Esecuzione del Progetto Per eseguire il progetto, puoi usare il comando seguente, specificando il file JAR generato (ad esempio): 

java --module-path "C:\Program Files\javafx-sdk-23.0.1\lib" --add-modules javafx.controls,javafx.fxml -jar  target/ nome del jar.jar

FILE DI BUILD 

I file di build (pom.xml) sono :

Parent, presente all'interno della cartella, ma al di fuori dei moduli

All'interno di ciascuno modulo: client, server e common







