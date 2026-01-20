package fr.ensimag.deca;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * User-specified options influencing the compilation.
 *
 * @author gl42
 * @date 01/01/2026
 */
public class CompilerOptions {
    public static final int QUIET = 0;
    public static final int INFO  = 1;
    public static final int DEBUG = 2;
    public static final int TRACE = 3;
    public int getDebug() {
        return debug;
    }

    public boolean getParallel() {
        return parallel;
    }

    public boolean getPrintBanner() {
        return printBanner;
    }

    public boolean getParseOption() {
        return parseOption;
    }

    public boolean getVerificationOption() {
        return verificationOption;
    }

    public boolean getNoCheckOption() {
        return noCheckOption;
    }
    
    public List<File> getSourceFiles() {
        return Collections.unmodifiableList(sourceFiles);
    }

    public boolean getByteOption() {
        return byteOption;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    private int debug = 0;
    private boolean parallel = false;
    private boolean printBanner = false;
    private boolean parseOption = false;
    private boolean verificationOption = false;
    private boolean noCheckOption = false;
    private int registerCount = 4; 
    private boolean byteOption = false;
    private List<File> sourceFiles = new ArrayList<File>();

    
    public void parseArgs(String[] args) throws CLIException {

        // Parseur des arguments :
        for (int i = 0 ; i < args.length ; ++i) {
            String arg = args[i];

            // -v pour --verification est déjà utilisé par -v de --verbose à demander
            // au prof absolument sinon ne passe pas les tout premiers tests => note catastrophique
            // J'ai remplacé par -d. Il faut répeter l'option plusieurs fois pour avoir 
            // plus de trace comme demandé dans le pdf page 103

            switch (arg) {
            case "-d":
            case "--debug":
                debug++;
                if (debug > 3) debug = 3;
                break;
            case "-b":
            case "--banner":
                printBanner = true;
                break;
            case "-P":
            case "--parallel":
                parallel = true;
                break;
            case "-p":
            case "--parse":
                parseOption = true;
                break;
            case "-v":
            case "--verify":
                verificationOption = true;
                break;
            case "-n":
            case "--noCheck":
                noCheckOption = true;
                break;
            case "--byte":
                byteOption = true;
                break;
            case "-r":
                if (i + 1 >= args.length) {
                    throw new CLIException("-r necessite un int");
                }
                try {
                    registerCount = Integer.parseInt(args[i + 1]);
                } catch (NumberFormatException e) {
                    throw new CLIException("Format int non correcte pour l'option -r");
                }
                if (registerCount < 4 || registerCount > 16) {
                    throw new CLIException("Nombre de registre doit être compris entre 4 et 16");
                }
                i++;
                break;
            default:
                if (arg.startsWith("-")) {
                    throw new CLIException("Unknown option: " + arg);
                } else {
                    sourceFiles.add(new File(arg));
                }
            }
        }

        Logger logger = Logger.getRootLogger();
        // map command-line debug option to log4j's level.
        switch (getDebug()) {
        case QUIET: break; // keep default
        case INFO:
            logger.setLevel(Level.INFO); break;
        case DEBUG:
            logger.setLevel(Level.DEBUG); break;
        case TRACE:
            logger.setLevel(Level.TRACE); break;
        default:
            logger.setLevel(Level.ALL); break;
        }
        logger.info("Application-wide trace level set to " + logger.getLevel());

        boolean assertsEnabled = false;
        assert assertsEnabled = true; // Intentional side effect!!!
        if (assertsEnabled) {
            logger.info("Java assertions enabled");
        } else {
            logger.info("Java assertions disabled");
        }

        if (args.length == 0) {
            displayUsage();
            System.exit(0);
        }

        if (parseOption && verificationOption) {
            throw new CLIException("Les options -p et -v sont incompatibles");
        }

        if (printBanner) {
            if (!sourceFiles.isEmpty() || parseOption || verificationOption
                || noCheckOption || parallel || debug > 0) {
                throw new CLIException("L’option'-b' ne peut être utilisée que sans autre option, et sans fichier source");
            }
        }

        if (sourceFiles.isEmpty() && !printBanner) {
            throw new CLIException("Aucun fichier source fourni");
        }
    }
    
    protected void displayUsage() {
        System.err.println(
            "La syntaxe d’utilisation de l’exécutable decac est :\n" +
            "decac [[-p | -v] [-n] [-r X] [-d]* [-P] [-w] <fichier deca>...] | [-b]\n" +
            "  -b        Affiche une bannière indiquant le nom de l'équipe\n" +
            "  -p        Arrête decac après l'étape de construction de " + //
                                "l'arbre, et affiche la décompilation de ce dernier " + //
                                "(i.e. s'il n'y a qu'un fichier source à " + //
                                "compiler, la sortie doit être un programme " + //
                                "deca syntaxiquement correct)\n" +
            "  -v        Arrête decac après l'étape de vérifications " + //
                                "(ne produit aucune sortie en l'absence d'erreur)\n" +
            "  -n        Supprime les tests à l'exécution spécifiés dans " + //
                                "les points 11.1 et 11.3 de la sémantique de Deca.\n" +
            "  -r X      Limite les registres banalisés disponibles à " + //
                                "R0 ... R{X-1}, avec 4 <= X <= 16\n" +
            "  -d        Active les traces de debug. Répéter " + //
                                "l'option plusieurs fois pour avoir plus de " + //
                                "traces.\n" +
            "  -P        S'il y a plusieurs fichiers sources, " + //
                                "lance la compilation des fichiers en " + //
                                "parallèle (pour accélérer la compilation)\n" +
            "  -w        Autorise l’affichage de messages d’avertissement (« warnings ») " + //
                                "en cours de compilation."
        );
    }
}
