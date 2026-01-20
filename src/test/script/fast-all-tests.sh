#! /bin/sh

# Auteur : gl42
# Tests globaux Deca - Version rapide avec verification IMA si disponible

set -u

curr_dir=$(cd "$(dirname "$0")" && pwd)
SCRIPT_PATH="$curr_dir/$(basename "$0")"

cd "$curr_dir/../../.." || { echo "Error: Cannot change to project root"; exit 1; }
PROJECT_ROOT=$(pwd)

DECAC="$PROJECT_ROOT/src/main/bin/decac"

if [ ! -x "$DECAC" ]; then
    echo "Erreur critique : Le compilateur n'est pas trouvé ou non exécutable."
    exit 1
fi

BASE_DIR="src/test/deca"
JOBS=${JOBS:-4}

IMA="ima"
if ! command -v "$IMA" >/dev/null 2>&1; then
    if [ -x "/usr/local/ima/bin/ima" ]; then
        IMA="/usr/local/ima/bin/ima"
    else
        IMA=""
    fi
fi

# echo "Lancement des tests Deca (JOBS=$JOBS) depuis $PROJECT_ROOT"
# echo "Compilateur : $DECAC"
# if [ -n "$IMA" ]; then
#     echo "Simulateur  : $IMA"
# else
#     echo "Simulateur  : NON DISPONIBLE"
# fi

if [ "${1:-}" = "--worker-valid" ]; then
    mode="$2"     
    testfile="$3"
    
    case "$mode" in
        syntax)
            flags="-p"
            ;;
        context)
            flags="-v"
            ;;
        codegen | avecObjet)
            flags="" 
            ;;
        *)
            echo "Mode inconnu : $mode"
            exit 1
            ;;
    esac

    out=$(mktemp)
    err=$(mktemp)
    trap 'rm -f "$out" "$err"' EXIT

    if "$DECAC" $flags "$testfile" >"$out" 2>"$err"; then
        
        if [ "$mode" = "syntax" ]; then
            if [ ! -s "$out" ]; then
                echo "ECHEC (-p : Pas de sortie) : $testfile"
                cat "$err"
                exit 1
            elif [ -s "$err" ]; then
                 echo "ECHEC (-p : stderr non vide) : $testfile"
                 cat "$err"
                 exit 1
            else
                 echo "OK (Valide -p) : $testfile"
                 exit 0
            fi

        elif [ "$mode" = "context" ]; then
            if [ -s "$out" ] || [ -s "$err" ]; then
                echo "ECHEC (Sortie non vide) : $testfile"
                cat "$out"
                cat "$err"
                exit 1
            else
                echo "OK (Valide -v) : $testfile"
                exit 0
            fi

        else 
            if [ -s "$out" ] || [ -s "$err" ]; then
                echo "ECHEC (Compilation non silencieuse) : $testfile"
                cat "$out"
                cat "$err"
                exit 1
            fi

            if [ -n "$IMA" ]; then
                ass_file="${testfile%.deca}.ass"
                res_file="${testfile%.deca}.res"
                
                if [ ! -f "$ass_file" ]; then
                     echo "ECHEC (Fichier .ass non généré) : $testfile"
                     exit 1
                fi

                out_ima=$(mktemp)
                if "$IMA" "$ass_file" > "$out_ima"; then
                    if [ -f "$res_file" ]; then
                         if ! cmp -s "$out_ima" "$res_file"; then
                             echo "ECHEC (Sortie exécution incorrecte) : $testfile"
                             echo "Attendue :"
                             cat "$res_file"
                             echo "Obtenue :"
                             cat "$out_ima"
                             rm -f "$out_ima"
                             exit 1
                         fi
                    fi
                    
                    rm -f "$out_ima"
                    echo "OK (Valide + Exec) : $testfile"
                    exit 0
                else
                    echo "ECHEC (Erreur exécution IMA) : $testfile"
                    cat "$out_ima"
                    rm -f "$out_ima"
                    exit 1
                fi
            else
                echo "OK (Valide - Pas d'exec) : $testfile"
                exit 0
            fi
        fi

    else
        echo "ECHEC (Erreur compilation) : $testfile"
        cat "$out"
        cat "$err"
        exit 1
    fi
fi

if [ "${1:-}" = "--worker-invalid" ]; then
    mode="$2"
    testfile="$3"

    case "$mode" in
        syntax)   flags="-p" ;;
        context | avecObjet)  flags="-v" ;; 
        codegen) flags="" ;;
        *)        echo "Mode inconnu"; exit 1 ;;
    esac

    if [ "$mode" = "codegen" ]; then
         if "$DECAC" $flags "$testfile" > /dev/null 2>&1; then
             
             if [ -n "$IMA" ]; then
                 ass_file="${testfile%.deca}.ass"
                 if "$IMA" "$ass_file" >/dev/null 2>&1; then
                      echo "ECHEC (Succès exécution inattendu) : $testfile"
                      exit 1
                 else
                      echo "OK (Invalide Runtime) : $testfile"
                      exit 0
                 fi
             else
                 echo "SKIP (Pas d'IMA pour verif runtime) : $testfile"
                 exit 0
             fi
         else
             echo "ECHEC (Erreur compilation inattendue pour test runtime) : $testfile"
             exit 1
         fi
    else
        if "$DECAC" $flags "$testfile" > /dev/null 2>&1; then
            echo "ECHEC (Succès inattendu) : $testfile"
            exit 1
        else
            echo "OK (Invalide) : $testfile"
            exit 0
        fi
    fi
fi

run_category() {
    folder="$1" 
    mode="$2"

    echo
    echo "Tests $folder (Mode: $mode)"

    dir_invalid="$BASE_DIR/$folder/invalid"
    if [ -d "$dir_invalid" ]; then
        find "$dir_invalid" -name "*.deca" -print0 | \
        xargs -0 -P "$JOBS" -n 1 "$SCRIPT_PATH" --worker-invalid "$mode" || global_status=1
    fi

    dir_valid="$BASE_DIR/$folder/valid"
    if [ -d "$dir_valid" ]; then
        find "$dir_valid" -name "*.deca" -print0 | \
        xargs -0 -P "$JOBS" -n 1 "$SCRIPT_PATH" --worker-valid "$mode" || global_status=1
    fi
}

global_status=0

run_category syntax    syntax
run_category context   context
run_category codegen   codegen
run_category avecObjet avecObjet

echo
echo "================================================================="
if [ $global_status -eq 0 ]; then
    echo "TOUS LES TESTS SONT PASSÉS AVEC SUCCÈS"
    exit 0
else
    echo "DES TESTS ONT ÉCHOUÉ"
    exit 1
fi
