# Concurrent Project: Game of Life (PCBE/CEBP)

This is Project 1, a concurrent application in Java that simulates a custom "Game of Life". The simulation focuses on cells as independent threads that compete for limited resources (food) on a shared map.

## Description

Each cell in the simulation is a thread (`Runnable`) that manages its own life cycle:

* **Feeding:** Cells must consume food units to survive.
* **Survival:** A cell that eats is "full" for a time (`T_FULL`). After that, it becomes hungry. If it doesn't eat within a given time (`T_STARVE`), it dies.
* **Death:** When a cell dies, it leaves behind a small amount of food.
* **Reproduction:** After eating a minimum number of times, a cell attempts to reproduce.
    * **Asexual:** It divides into two new cells.
    * **Sexual:** It must find another sexual cell that is also looking to reproduce.

The main objective of the project is to correctly manage concurrency and access to shared state (the world map, food) using Java synchronization mechanisms.

## Technologies and Packages

* **Language:** Java (JDK 11+)
* **Core Concurrency:**
    * `java.lang.Runnable` and `java.lang.Thread`: Each cell is a thread.
    * `java.util.concurrent.ExecutorService`: For efficiently managing the thread pool (cells).
* **Thread-Safe Data Structures:**
    * `java.util.concurrent.ConcurrentHashMap`: Used to store the world state (cell and food positions) to allow atomic access and prevent `ConcurrentModificationException`.
* **Synchronization:**
    * `synchronized` (Monitor Pattern): Used to manage coordination logic, such as the mating of sexual cells.
    * `volatile`: Used for flags (e.g., `isAlive`) accessed by multiple threads.

## Project Structure (Proposal)

```
src/main/java/com/gameoflife/
│
├── Simulation.java         // Main class, starts the simulation
├── World.java              // Monitor class (Singleton), manages shared state
├── Constants.java          // Simulation constants (T\_FULL, T\_STARVE, etc.)
│
├── cell/
│   ├── Cell.java           // Interface (Runnable)
│   ├── State.java          // Enum (IDLE, HUNGRY, STARVING, REPRODUCING)
│   ├── BaseCell.java       // Abstract class with basic logic (life, death)
│   ├── AsexualCell.java    // Implementation for asexual reproduction
│   └── SexualCell.java     // Implementation for sexual reproduction
│
├── util/
│   └── Position.java       // Record or class for coordinates (x, y)
│
└── visualization/
└── Visualizer.java     // (Optional) Runnable that displays the world state in the console
```

## How to Run

### Option 1: Without a Build Tool

1.  **Compile:**
    ```bash
    javac -d out src/main/java/com/gameoflife/*.java src/main/java/com/gameoflife/cell/*.java src/main/java/com/gameoflife/util/*.java src/main/java/com/gameoflife/visualization/*.java
    ```

2.  **Run:**
    ```bash
    java -cp out com.gameoflife.Simulation
    ```

### Option 2: With Maven (Recommended)

1.  **Compile:**
    ```bash
    mvn compile
    ```

2.  **Run:**
    ```bash
    mvn exec:java -Dexec.mainClass="com.gameoflife.Simulation"
    ```