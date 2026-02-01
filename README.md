Mars Satellite Simulation
AUTHOR: Michael Durkan
=================

## Description

Simulates the actions and movements of drones and rovers on mars.

The rovers and drones are provided with commands from the command generator. The positions and data being recorded is changed based on these commands every simluation tick. The results of these changes can be seen within the diagnostics.txt logging file.

The systems handles errors/corruption in the commands and uses design patterns to improve the maintainability, seperation of concerns and extensbility of the system.

Java code can be read within \src\main\java\edu\curtin\mars

## Dependancies

This is a [Gradle][]-based Java project structure. Provided you have the [OpenJDK][] installed, the `gradlew` script will take care of all other dependencies.

[Java]: https://docs.oracle.com/javase/tutorial/
[Gradle]: https://gradle.org/
[OpenJDK]: https://adoptium.net/temurin/releases/

## Running

To run code

```
./gradlew run
```

If you run into permission problems:

```
bash gradlew run
```

## Output Information

Each 'SOL' (Mars Day) is printed in diagnostics.txt along with the current state and position of each drone/rover.
