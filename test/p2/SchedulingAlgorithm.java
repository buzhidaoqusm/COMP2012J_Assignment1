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
      // Call round robin here, after implementing it and remove or comment out the
      // following line.
      roundRobin(runtime, timeSlice, processVector, result, out);
      // FIFO(runtime, processVector, result, out);
      //
      out.close();
    } catch (IOException e) { /* Handle exceptions */
      e.printStackTrace();
    }
    return result;
  }

  private static void roundRobin(int runtime, int timeSlice, Vector<sProcess> processVector, Results result, PrintStream out) throws IOException {
    int comptime = 0;
    int completed = 0;
    int currentProcess = 0;
    int currentTime = 0;
    int size = processVector.size();

    String resultsFile = "test.log";
    PrintStream log = new PrintStream(new FileOutputStream(resultsFile));

    result.schedulingType = "Batch (Preemptive)";
    result.schedulingName = "Round Robin";

    for (sProcess process : processVector) {
      log.println("Process: " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " "
          + process.ionext + " " + process.numblocked);
    }

    try {
      sProcess process = (sProcess) processVector.elementAt(currentProcess);
      printRegistered(out, currentProcess, process, comptime);
      log.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
          + process.cpudone + " " + comptime + ")");
      while (comptime < runtime) {
        log.println("Process" + currentProcess + " cpudone: " + process.cpudone + " cputime: " + process.cputime + " ionext: " + process.ionext + " ioblocking: " + process.ioblocking + " numblocked: " + process.numblocked + " comptime: " + comptime + " currentTime: " + currentTime);
        if (process.cpudone == process.cputime) {
          currentTime = 0;
          completed++;
          printCompleted(out, currentProcess, process, comptime);
          log.println("Process: " + currentProcess + " completed... (" + process.cputime + " " + process.ioblocking + " "
              + process.cpudone + " " + comptime + ")");
          if (completed == size) {
            result.compuTime = comptime;
            return;
          }
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
          log.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
          + process.cpudone + " " + comptime + ")");
          if (comptime == 3840) {
            log.println("Process" + currentProcess + " cpudone: " + process.cpudone + " cputime: " + process.cputime + " ionext: " + process.ionext + " ioblocking: " + process.ioblocking + " numblocked: " + process.numblocked + " comptime: " + comptime + " currentTime: " + currentTime);
          }
        }
        if (currentTime == timeSlice) {
          currentTime = 0;

          if (process.ioblocking == process.ionext) {
            printIOBlocked(out, currentProcess, process, comptime);
            log.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " "
                + process.cpudone + " " + comptime + ")");
            process.numblocked++;
            process.ionext = 0;
          }

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
          log.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
          + process.cpudone + " " + comptime + ")");
        } else if (process.ioblocking == process.ionext) {
          printIOBlocked(out, currentProcess, process, comptime);
          log.println("Process: " + currentProcess + " I/O blocked... (" + process.cputime + " " + process.ioblocking + " "
                + process.cpudone + " " + comptime + ")");
          process.numblocked++;
          process.ionext = 0;
          currentTime = 0;

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
          log.println("Process: " + currentProcess + " registered... (" + process.cputime + " " + process.ioblocking + " "
          + process.cpudone + " " + comptime + ")");
        }
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
    // ADD YOUR CODE HERE AND ROMOVE THE FOLLOWING LINE
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
