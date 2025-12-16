package fr.ensimag.deca;

import java.io.File;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
            throw new UnsupportedOperationException("decac -b not yet implemented");
        }
        if (options.getSourceFiles().isEmpty()) {
            throw new UnsupportedOperationException("decac without argument not yet implemented");
        }
        if (options.getParallel()) {
            //Fait au dessous: Instancier DecacCompiler pour chaque fichier à
            // compiler, et lancer l'exécution des méthodes compile() de chaque
            // instance en parallèle. Il est conseillé d'utiliser
            // java.util.concurrent de la bibliothèque standard Java.
            int nbThreads = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool(nbThreads);
            List<Future<Boolean>> resultats = new ArrayList<>();

            for (File source : options.getSourceFiles()) {
                DecacCompiler compiler = new DecacCompiler(options, source);

                Future<Boolean> res = executor.submit(() -> {
                    return compiler.compile();
                });

                resultats.add(res);
            }

            for (Future<Boolean> res : resultats) {
                try {
                    if (res.get()) {
                        error = true;
                    }
                } catch (InterruptedException e) {
                    error = true;
                } catch (ExecutionException e) {
                    error = true;
                }
            }

            executor.shutdown();

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
