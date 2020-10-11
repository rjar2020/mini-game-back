# MiniGame Backend

## Prerequisites
Java 11 installed

## Running the backend
To execute the mini game backend, ***from the project root*** use:

- For running the pre-packaged jar
```bash
./execute-me
```
- For building and running a newly generated jar
```bash
./execute-me -b
```
- For building and running a newly generated jar, with gradle output
```bash
./execute-me -v
```
- If you want to run directly the pre-packaged jar:
```bash
java -jar mini-game-backend.jar
```

Use ***Control + c*** to stop the server after any of the bellow options.

## From the IDE
- Import the source as a Gradle project

- com.minigame.MiniGameBootstrap can be run/debug 

## Notes about the code review
Modeling the stores as a singleton and with static collections because:
- This is a test, so it's the most straight forward way to simulate a persistence layer. Other option could be an in-memory DB.
- I consider an static structure is good enough for a test like this, as I don't think is a good idea to mock a java collection, as the behaviour is well-known, tests will be less complex and with less boilerplate code.
- The methods defined in the DAO classes are already abstractions of the persistence layer. Changing the implementation details of the dao, including DB or supporting data structures, doesn't imply a change in the behaviour.