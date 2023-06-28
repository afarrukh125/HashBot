# HashBot

A simple discord bot with a track player with playlist saving capabilities.
Caters to a smaller number of discord servers.

## Requirements

* Java 20
  * Will be moved to Java 21 once that is released
* Maven 3.9+
  * To compile the jar simply run `mvn package` and an uber jar will be placed in the target folder
* Optional: A Neo4j instance
  * Feel free to boot up an [aura instance](https://neo4j.com/cloud/platform/aura-graph-database) for free and use that, then place the credentials in the
  settings file that will be generated on first run
  * If Neo4j credentials are not provided, a local SQLite implementation will be used instead
