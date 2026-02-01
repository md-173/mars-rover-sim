Mars Satellite Simulation
AUTHOR: Michael Durkan
=================

## Description

Simulates the actions of drones and rovers on mars when provided my commadns through satellite link to earth.

The rovers and drones are provided with commands from the command generator. The positions and data being recorded is changed based on these commands every simluation tick. The results of these changes can be seen within the logging file.

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

