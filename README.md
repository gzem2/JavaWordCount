JavaWordCount
=========

Build and Run
-------------

1. Run in the command line:
	```
	mvn compile exec:java
	```

2. Enter arguments:
	```
	Url:https://www.simbirsoft.com/
	Delimiter:{' ', ',', '.', '! ', '?','"', ';', ':', '[', ']', '(', ')', '\n', '\r', '\t'}
	```

3. Check database:
	```
	java -cp C:\Users\%USERNAME%\.m2\repository\org\hsqldb\hsqldb\2.4.0\hsqldb-2.4.0.jar org.hsqldb.util.DatabaseManagerSwing --url jdbc:hsqldb:file:HSQLDB/JavaWordCount
	```