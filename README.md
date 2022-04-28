# P2P MapReduce

by: Nick Osborn, Riley Grant, Soma Badri

## Quick Usage MAYBE

*N.B. All jars will print logging output to a file and standard out*
1. Start the Coordinator

`java -jar ./Coordinator.jar` to start the coordinator.
2. Start the Bulk Clients

`java -jar ./BulkClient.jar localhost 1099` to start 5 clients. They will act as the peers on which jobs will be scheduled.
3. Start the Failing Client

`java -jar ./FailingClient.jar localhost 1099` to start a failing client that timeouts and showcases fault tolerance of the system.

4. Start the MapReduce running Client with `--example`

`java -jar ./BulkClient.jar WordCountExampleClient localhost 1099 --example` to start the word counting example.