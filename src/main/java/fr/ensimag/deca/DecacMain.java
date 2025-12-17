package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// POUR getParallel()
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
//

/**
 * Main class for the command-line Deca compiler.
 *
 * @author gl42
 * @date 01/01/2026
 */
public class DecacMain {
    private static Logger LOG = Logger.getLogger(DecacMain.class);
    
    public static void main(String[] args) {
        // example log4j message.
        LOG.info("Decac compiler started");
        boolean error = false;
        final CompilerOptions options = new CompilerOptions();
        try {
            options.parseArgs(args);
        } catch (CLIException e) {
            System.err.println("Error during option parsing:\n"
                    + e.getMessage());
            options.displayUsage();
            System.exit(1);
        }
        if (options.getPrintBanner()) {
            System.out.println("Deca compiler — GROUPE 7 / 42");
            System.exit(0);
        }
        if (options.getSourceFiles().isEmpty()) {
            throw new UnsupportedOperationException("decac without argument not yet implemented");
        }
        if (options.getParallel()) {
            int nbThreads = Runtime.getRuntime().availableProcessors();

            ExecutorService executorService = Executors.newFixedThreadPool(nbThreads);
            List<Future<Boolean>> futureList = new ArrayList<>();

            for (File sourceFile : options.getSourceFiles()) {
                Future<Boolean> future = executorService.submit(() -> {
                    DecacCompiler decaCompiler = new DecacCompiler(options, sourceFile);
                    return decaCompiler.compile();
                });
                futureList.add(future);
            }

            for (Future<Boolean> future : futureList) {
                try {
                    if (future.get()) {
                        error = true;
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("Erreur pendant compilation parallèle");
                    error = true;
                }
            }

            executorService.shutdown();
        } else {
            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);
                if (compiler.compile()) {
                    error = true;
                }
            }
        }
        System.exit(error ? 1 : 0);
    }
}
