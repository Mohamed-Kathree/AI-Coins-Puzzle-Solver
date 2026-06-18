# 🧠 AI Coins Puzzle Solver

> A Java implementation of a Best-First Search algorithm that solves a 7-cell coin sliding puzzle, using a misplaced-coins heuristic to guide the search toward the goal state in the minimum number of steps.

![Java](https://img.shields.io/badge/Java-CSC311%20AI-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Algorithm](https://img.shields.io/badge/Algorithm-Best--First%20Search-1B3A5C?style=flat-square)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen?style=flat-square)

---

## The Puzzle

A 7-cell board holds three blue coins (`B`), three red coins (`R`), and one empty cell (`0`). The goal is to reach a sorted configuration from any valid starting arrangement.

**Goal state:**

```
B  B  B  0  R  R  R
```

**Legal moves — a coin may:**
- Slide into an adjacent empty cell (move left or right by 1)
- Jump over an adjacent coin into an empty cell two places away

---

## Algorithm — Best-First Search

The solver uses **Best-First Search** with a priority queue ordered by a heuristic function `h`. At each step, the state with the lowest `h` value is expanded first, directing the search toward the goal without exhaustively exploring every possible path.

### Heuristic function

```
h(state) = number of coins not in their goal position
```

The goal positions are fixed: indices 0–2 must be `B`, index 3 must be `0`, indices 4–6 must be `R`. Any coin that occupies the wrong index contributes 1 to `h`. The empty cell is excluded from the count.

```
Goal:    B  B  B  0  R  R  R
         0  1  2  3  4  5  6

State:   B  R  B  R  0  R  B    →  h = 3  (indices 1, 3, 6 misplaced)
```

`h = 0` means the goal state has been reached.

### Search procedure

```
1. Create initial State from input file
2. Add to PriorityQueue (ordered by h ascending)
3. Maintain a visited set to avoid revisiting states
4. Loop:
   a. Poll state with lowest h from the queue
   b. Skip if already visited
   c. Mark visited, print state + h value
   d. If h == 0 → goal reached, stop
   e. Generate all valid child states (up to 4 moves)
   f. Add unvisited children to the queue
5. If queue empties with no solution → report failure
```

### Move generation

From the current empty cell index, four moves are attempted:

| Move | Direction | Type |
|---|---|---|
| −1 | Left | Slide |
| +1 | Right | Slide |
| −2 | Left | Jump |
| +2 | Right | Jump |

For jumps (±2), the intermediate cell must not be empty. All moves are bounds-checked against the 7-cell board.

---

## Example Solutions

### State A — Starting position: `B R B R 0 R B`

```
B R B R 0 R B  h=3   ← start
B R B 0 R R B  h=2
B 0 B R R R B  h=2
0 B B R R R B  h=2
B B 0 R R R B  h=2
B B R 0 R R B  h=2
...
B B 0 B R R R  h=1
B B B 0 R R R  h=0   ← goal
Goal state reached!
```

### State B — Starting position: `R R R 0 B B B`

```
R R R 0 B B B  h=6   ← start (fully reversed)
R R 0 R B B B  h=6
R R B R 0 B B  h=5
R R B 0 R B B  h=4
...
B B 0 B R R R  h=1
B B B 0 R R R  h=0   ← goal
Goal state reached!
```

State B (fully reversed) requires significantly more expansions — the search explores a wider tree before finding the optimal path.

---

## Project Structure

```
CoinsPuzzle/
│
├── CoinsPuzzle.java   # Main solver — search logic, file I/O, entry point
├── StateA.txt         # Input: starting configuration for puzzle A
├── StateB.txt         # Input: starting configuration for puzzle B
├── OutputA.txt        # Output: full search path and solution for A
└── OutputB.txt        # Output: full search path and solution for B
```

### Input file format (`StateA.txt` / `StateB.txt`)

Space-separated characters on a single line, exactly 7 tokens:

```
B R B R 0 R B
```

Valid tokens: `B` (blue coin), `R` (red coin), `0` (empty cell). Exactly one `0` must be present.

### Output format

Each expanded state is written on one line, showing the board configuration and its heuristic value:

```
B R B R 0 R B h=3
B R B 0 R R B h=2
...
B B B 0 R R R h=0
Goal state reached!
```

---

## Running the Program

### Prerequisites
- Java Development Kit (JDK) 8 or later

### Update file paths

Open `CoinsPuzzle.java` and update the four file path strings at the top of `main()` to match your local directory structure:

```java
String startStateAFilePath = "path/to/StateA.txt";
String startStateBFilePath = "path/to/StateB.txt";
String outputAFilePath     = "path/to/OutputA.txt";
String outputBFilePath     = "path/to/OutputB.txt";
```

### Compile and run

```bash
javac CoinsPuzzle.java
java CoinsPuzzle
```

Output is written to the specified output files and also printed to the console.

---

## Key Classes

### `State`
Represents a single board configuration. Each `State` stores:
- `puzzleCoins` — the 7-element char array (`B`, `R`, or `0`)
- `emptyIndex` — position of the empty cell
- `hValue` — misplaced-coin heuristic, computed on construction

Implements `Comparable<State>` so the `PriorityQueue` orders states by `h` automatically.

### `CoinsPuzzle`
Contains `main()`, `processStartState()`, and `solvePuzzle()`. Handles file I/O and drives the Best-First Search loop.

---

## Concepts Demonstrated

- Best-First Search with an admissible heuristic
- Priority queue for frontier management (`java.util.PriorityQueue`)
- Visited-state tracking with a hash set to prevent cycles
- State-space search and goal-state detection
- Move generation with bounds and validity checking
- Object-oriented design — `State` encapsulates board logic
- File I/O — reading initial states, writing full search paths

---

*CSC311 — Artificial Intelligence, Practical 1, Term 1 2025. University of the Western Cape.*
