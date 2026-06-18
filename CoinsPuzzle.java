import java.io.*; //Mohamed Yusuf Kathree
import java.util.*;//4253340

// State class represents a configuration of the puzzle
class State implements Comparable<State> {
    private int emptyIndex; // The index of the empty cell represented by '0'
    private char[] puzzleCoins; // Array of coins where '0' is empty and others are either 'B' or 'R'
    private int hValue; // Heuristic value representing the number of misplaced coins
    
    // Constructor initializes a state with an empty cell and coin configuration
    public State(int emptyIndex, char[] puzzleCoins) {
        this.emptyIndex = emptyIndex;
        this.puzzleCoins = Arrays.copyOf(puzzleCoins, puzzleCoins.length); // Create a copy of the coin configuration to avoid mutation
        this.hValue = calculateHeuristic(); // Calculate the heuristic value
    }
    
    // Heuristic function to count misplaced coins compared to the goal state
    private int calculateHeuristic() {
        int misplacedCoins = 0;
        char[] goalState = {'B', 'B', 'B', '0', 'R', 'R', 'R'}; // The goal state
        for (int i = 0; i < 7; i++) {
            if (puzzleCoins[i] != '0' && puzzleCoins[i] != goalState[i]) { // Check if coin is misplaced
                misplacedCoins++;
            }
        }
        return misplacedCoins; // Return the number of misplaced coins
    }
    
    // Getter for empty cell index
    public int getEmptyIndex() {
        return emptyIndex;
    }
    
    // Getter for the coins configuration
    public char[] getPuzzleCoins() {
        return puzzleCoins;
    }
    
    // Getter for the heuristic value
    public int getHValue() {
        return hValue;
    }
    
    // Generates the children of the current state by making possible moves of the empty cell
    public List<State> generateChildren() {
        List<State> childStates = new ArrayList<>();
        int[] moves = {-1, 1, -2, 2}; // Possible moves: left, right, jump left, jump right
        
        // Try each move and generate a new state
        for (int move : moves) {
            int newEmptyIndex = emptyIndex + move;
            if (isValidMove(newEmptyIndex, move)) { // Check if move is valid
                char[] newCoins = Arrays.copyOf(puzzleCoins, puzzleCoins.length); // Create a copy of the coins
                newCoins[emptyIndex] = newCoins[newEmptyIndex]; // Swap the empty cell with the new position
                newCoins[newEmptyIndex] = '0'; // Set the new empty position
                childStates.add(new State(newEmptyIndex, newCoins)); // Add the new state to the list of children
            }
        }
        return childStates; // Return the list of child states
    }
    
    // Checks if a move to a new position is valid
    private boolean isValidMove(int newEmptyIndex, int move) {
        if (newEmptyIndex < 0 || newEmptyIndex >= 7) return false; // Ensure the move is within bounds
        if (Math.abs(move) == 2) { // If jumping (move is 2 or -2) check the next cell
            int intermediateCell = emptyIndex + move/2;
            return puzzleCoins[intermediateCell] != '0'; // Ensure the intermediate cell is not empty
        }
        return true; // Otherwise, the move is valid
    }
    
    // Compare this state with another state based on the heuristic value (for priority queue)
    @Override
    public int compareTo(State other) {
        return Integer.compare(this.hValue, other.hValue);
    }
    
    // Prints the current state to the console and a file
    public void printState(PrintWriter writer) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < puzzleCoins.length; i++) {
            if (i > 0) sb.append(" ");
            sb.append(puzzleCoins[i]); // Append each coin to the string
        }
        sb.append(" h=").append(hValue); // Append the heuristic value
        String stateStr = sb.toString();
        System.out.println(stateStr); // Print to console
        writer.println(stateStr); // Print to file
    }
}

// Main class to solve the puzzle
public class CoinsPuzzle {
    public static void main(String[] args) throws IOException {
        // Paths for input and output files
        String startStateAFilePath = "C:\\Users\\moham\\OneDrive\\Documents\\CSC311\\AI\\AI_Assignment1\\lib\\StateA.txt";
        String startStateBFilePath = "C:\\Users\\moham\\OneDrive\\Documents\\CSC311\\AI\\AI_Assignment1\\lib\\StateB.txt";
        String outputAFilePath = "C:\\Users\\moham\\OneDrive\\Documents\\CSC311\\AI\\AI_Assignment1\\lib\\OutputA.txt";
        String outputBFilePath = "C:\\Users\\moham\\OneDrive\\Documents\\CSC311\\AI\\AI_Assignment1\\lib\\OutputB.txt";
        
        // Process the two start states A and B
        System.out.println("\nProcessing State A...");
        processStartState(startStateAFilePath, outputAFilePath);
        
        System.out.println("\nProcessing State B...");
        processStartState(startStateBFilePath, outputBFilePath);
    }
    
    // Reads the initial state from the file and solves the puzzle
    private static void processStartState(String inputFilePath, String outputFilePath) throws IOException {
        // Read the initial state from the input file
        BufferedReader br = new BufferedReader(new FileReader(inputFilePath));
        String line = br.readLine().trim();
        br.close();
        
        // Split the line into parts (coins) and validate the format
        String[] parts = line.split("\\s+");
        if (parts.length != 7) {
            throw new IllegalArgumentException("Input must contain exactly 7 characters (with spaces)");
        }
        
        char[] puzzleCoins = new char[7];
        int emptyIndex = -1;
        for (int i = 0; i < 7; i++) {
            puzzleCoins[i] = parts[i].charAt(0); // Convert each part to a char and store in coins array
            if (puzzleCoins[i] == '0') {
                emptyIndex = i; // Find the index of the empty cell
            }
        }
        
        if (emptyIndex == -1) {
            throw new IllegalArgumentException("Input must contain a '0' representing the empty space");
        }
        
        // Initialize the output file for writing results
        PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath));
        
        // Solve the puzzle and write the steps to the output file
        solvePuzzle(emptyIndex, puzzleCoins, writer);
        
        writer.close();
    }
    
    // Solves the puzzle 
    private static void solvePuzzle(int startEmptyIndex, char[] startPuzzleCoins, PrintWriter writer) {
        // Priority queue for, ordering by heuristic value
        PriorityQueue<State> pq = new PriorityQueue<>();
        Set<String> visitedStates = new HashSet<>();
        
        // Initialize the initial state and add it to the priority queue
        State initialState = new State(startEmptyIndex, startPuzzleCoins);
        pq.add(initialState);
        
        // Start solving the puzzle by exploring states
        while (!pq.isEmpty()) {
            State currentState = pq.poll(); // Get the state with the lowest heuristic value 
            String stateKey = new String(currentState.getPuzzleCoins()); // Create a unique key for the state
            
            // Check if we have already visited this state
            if (!visitedStates.contains(stateKey)) {
                visitedStates.add(stateKey); // Mark this state as visited
                currentState.printState(writer); // Print this state
                
                // If we reach the goal state 
                if (currentState.getHValue() == 0) {
                    System.out.println("Goal state reached!");
                    writer.println("Goal state reached!");
                    return;
                }
                
                // Add all child states to the priority queue for further exploration
                pq.addAll(currentState.generateChildren());
            }
        }
        
        // If the queue is empty and no solution was found, print a failure message
        System.out.println("No solution found!");
        writer.println("No solution found!");
    }

    //References
    //https://chatgpt.com/ (used for understanding and debugging errors in my search algorithm)
    //https://www.youtube.com/watch?v=71CEj4gKDnE
    //https://www.youtube.com/watch?v=PisHmi_SyNg
    //https://www.youtube.com/watch?v=LDZc3Bph1vw
    //https://www.youtube.com/watch?v=PeFyhRr42ac

}
