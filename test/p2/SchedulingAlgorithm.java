import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {

  // Main entry point for the scheduling algorithm
  public static Results Run(int runtime, Vector<sProcess> processVector, Results result, int timeSlice) {
    String resultsFile = "Summary-Processes"; // Output file for results

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      roundRobin(runtime, timeSlice, processVector, result, out); // Execute the round-robin algorithm
      out.close(); // Close the file stream after processing
    } catch (IOException e) { // Handle IO exceptions
      e.printStackTrace();
    }
    return result; // Return the results object
  }

  // Implements the Round-Robin scheduling algorithm
  private static void roundRobin(int runtime, int timeSlice, Vector<sProcess> processVector, Results result,
                                 PrintStream out) throws IOException {
    int currentTime = 0; // Keeps track of the current simulation time
    int size = processVector.size(); // Total number of processes
    int currentProcess = 0; // Index of the process currently being scheduled

    // Set scheduling metadata
    result.schedulingType = "Interactive";
    result.schedulingName = "Round-Robin";

    try {
      boolean[] completed = new boolean[size]; // Tracks whether each process is completed
      int numCompleted = 0; // Count of completed processes

      // Main scheduling loop: runs until runtime is exceeded or all processes are completed
      while (currentTime < runtime && numCompleted < size) {
        sProcess process = processVector.elementAt(currentProcess); // Get the current process

        if (!completed[currentProcess]) { // Only process if not already completed
          // Check if the process can finish execution within the remaining runtime
          if (runtime - currentTime < timeSlice && process.ioblocking - process.ionext < timeSlice
                  && process.cputime - process.cpudone < timeSlice) {
            printRegistered(out, currentProcess, process, currentTime); // Log process registration
            break; // Exit if runtime is insufficient to continue
          }

          // Log process registration and execution details
          printRegistered(out, currentProcess, process, currentTime);

          // Calculate execution time for this cycle
          int remainingTime = process.cputime - process.cpudone; // Time left for process to complete
          int executeTime = Math.min(timeSlice, remainingTime); // Time to execute in this round

          // Check and adjust execution time based on IO blocking
          if (process.ioblocking > 0) {
            int timeToNextBlock = process.ioblocking - process.ionext; // Time until next IO block
            if (timeToNextBlock > 0) {
              executeTime = Math.min(executeTime, timeToNextBlock); // Adjust for IO block timing
            }
          }

          // Update the process and simulation time
          process.cpudone += executeTime; // Add executed time to the process's progress
          currentTime += executeTime; // Increment simulation time
          process.ionext += executeTime; // Update time to the next IO block

          // Check if the process has completed its CPU time
          if (process.cpudone >= process.cputime) {
            completed[currentProcess] = true; // Mark the process as completed
            numCompleted++; // Increment completed process count
            printCompleted(out, currentProcess, process, currentTime); // Log completion details
          }
          // Handle IO blocking if applicable
          else if (process.ioblocking > 0 &&
                  process.ionext >= process.ioblocking &&
                  currentTime < runtime) { // Only log IO blocking if within simulation time
            process.numblocked++; // Increment IO block count for the process
            process.ionext = 0; // Reset time to the next IO block
            printIOBlocked(out, currentProcess, process, currentTime); // Log IO blocking details
          }
        }

        // Rotate to the next process in the queue (round-robin mechanism)
        currentProcess = (currentProcess + 1) % size;
      }

      // Log if not all processes could complete within the runtime
      if (numCompleted < size) {
        out.println("Not enough time to complete all processes!");
      }
      out.close(); // Close the output stream

    } catch (Exception e) {
      System.err.println("Could not create output file: " + e.getMessage()); // Handle unexpected exceptions
    }

    result.compuTime = currentTime; // Store total computation time in the results
  }

  // Helper method: logs when a process is registered for execution
  private static void printRegistered(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
            + process.cpudone + " " + comptime + ")");
  }

  // Helper method: logs when a process completes its execution
  private static void printCompleted(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " "
            + process.cpudone + " " + comptime + ")");
  }

  // Helper method: logs when a process encounters an IO block
  private static void printIOBlocked(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " "
            + process.cpudone + " " + comptime + ")");
  }
}
