==================================================
Brief intro on how and behavior of the Application
==================================================

1) Application is built on Spring Boot and can be access through 8080 port.
2) /transactions and /statistics are the REST API exposed.
3) When the /transactions is called it verifies for transaction expiry. If the transaction expired i.e., greater than 60 secs of current time,
	then that transaction is rejected throwing 204. If the transaction is valid behavior as follows.
		1) Sends a response as 201.
		2) The valid transaction is put to Hazelcast IMDB map (In-Memory datastore built using Hazelcast) with time to live set to 60 secs.
		3) On every entry of a transaction to Hazelcast, the statistics are calculated.
		4) once the transaction kept in hazelcast map is expired, then statistics are re-calculated.
4) The output will be to near-perfect.