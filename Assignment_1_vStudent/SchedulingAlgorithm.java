// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {

  public static Results Run(int runtime, Vector<sProcess> processVector, Results result, int timeSlice) {
    String resultsFile = "Summary-Processes";

    try {
      PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
      roundRobin(runtime, timeSlice, processVector, result, out);
      out.close();
    } catch (IOException e) { /* Handle exceptions */
      e.printStackTrace();
    }
    return result;
  }

  private static void roundRobin(int runtime, int timeSlice, Vector<sProcess> processVector, Results result, PrintStream out) throws IOException {
    int comptime = 0; // completion time
    int completed = 0; // number of completed processes
    int currentProcess = 0; // current process index
    int currentTime = 0; // time slice counter
    int size = processVector.size();

    result.schedulingType = "Batch (Preemptive)";
    result.schedulingName = "Round Robin";

    try {
      // Get the first process
      sProcess process = (sProcess) processVector.elementAt(currentProcess);
      printRegistered(out, currentProcess, process, comptime);
      while (comptime < runtime) {
        // Check completion of the process
        if (process.cpudone == process.cputime) {
          currentTime = 0;
          completed++;
          printCompleted(out, currentProcess, process, comptime);
          // Check if all the processes are completed
          if (completed == size) {
            result.compuTime = comptime;
            return;
          }
          // scheduling the next process, traverse the vector until the previous position of the current process.
          // If i equals current process, no action needs, current process will continue to proceed.
          int i = (currentProcess + 1) % size;
          while (i % size != currentProcess) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime) {
              currentProcess = i;
              break;
            }
            i = (i + 1) % size;
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          printRegistered(out, currentProcess, process, comptime);
        }
        // Checking if the time slice is run out.
        if (currentTime == timeSlice) {
          currentTime = 0;

          // Check if the process is blocked, because io blocking and time slice running out can happen at the same time.
          if (process.ioblocking == process.ionext) {
            printIOBlocked(out, currentProcess, process, comptime);
            process.numblocked++;
            process.ionext = 0;
          }

          // scheduling the next process
          int i = (currentProcess + 1) % size;
          while (i % size != currentProcess) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime) {
              currentProcess = i;
              break;
            }
            i = (i + 1) % size;
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          printRegistered(out, currentProcess, process, comptime);
        } else if (process.ioblocking == process.ionext) {
          // Check if the process is blocked
          printIOBlocked(out, currentProcess, process, comptime);
          process.numblocked++;
          process.ionext = 0;
          currentTime = 0; // Reset time slice counter

          // scheduling the next process
          int i = (currentProcess + 1) % size;
          while (i % size != currentProcess) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime) {
              currentProcess = i;
              break;
            }
            i = (i + 1) % size;
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          printRegistered(out, currentProcess, process, comptime);
        }
        
        // increment timer counters
        process.cpudone++;
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
        currentTime++;
      }
      out.println("Not enough time to complete all processes!");
    } catch (Exception e) {
      result.compuTime = comptime;
      throw (e);
    }
  }

  private static void FIFO(int runtime, Vector<sProcess> processVector, Results result, PrintStream out) {
    int i = 0;
    int comptime = 0;
    int currentProcess = 0;
    int previousProcess = 0;
    int size = processVector.size();
    int completed = 0;

    result.schedulingType = "Batch (Nonpreemptive)";
    result.schedulingName = "First-Come First-Served";

    try {
      sProcess process = (sProcess) processVector.elementAt(currentProcess);
      printRegistered(out, currentProcess, process, comptime);
      while (comptime < runtime) {

        // Check completion of the process
        if (process.cpudone == process.cputime) {
          completed++;
          printCompleted(out, currentProcess, process, comptime);
          if (completed == size) {
            result.compuTime = comptime;
            return;
          }
          // scheduling the next process
          for (i = size - 1; i >= 0; i--) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime) {
              currentProcess = i;
            }
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          printRegistered(out, currentProcess, process, comptime);

        }
        // Checking for blocking time
        if (process.ioblocking == process.ionext) {
          printIOBlocked(out, currentProcess, process, comptime);
          process.numblocked++;
          process.ionext = 0;

          // scheduling the next process
          previousProcess = currentProcess;
          for (i = size - 1; i >= 0; i--) {
            process = (sProcess) processVector.elementAt(i);
            if (process.cpudone < process.cputime && previousProcess != i) {
              currentProcess = i;
            }
          }
          process = (sProcess) processVector.elementAt(currentProcess);
          printRegistered(out, currentProcess, process, comptime);
        }
        // increment timer counters
        process.cpudone++;
        if (process.ioblocking > 0) {
          process.ionext++;
        }
        comptime++;
      }
      out.println("Not enough time to complete all processes!");
    } catch (Exception e) {
      result.compuTime = comptime;
      throw (e);
    }
  }

  private static void printRegistered(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
        + process.cpudone + " " + comptime + ")");
  }

  private static void printCompleted(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " "
        + process.cpudone + " " + comptime + ")");
  }

  private static void printIOBlocked(PrintStream out, int currentProcess, sProcess process, int comptime) {
    out.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " "
        + process.cpudone + " " + comptime + ")");
  }
}
