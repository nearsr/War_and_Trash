#!/usr/bin/env bash

source "${SIMGRADING}/grader-lib.sh"

set -e

RUNS=${1:-100}
SEED=${2:-7654}
SIMS="warntrashci"

# -N 0 -> oo random values
declare -a gslexec=("${SIMGRADING}/gsl-make-trace" -N 0 - u 0 1 )

if ! test -d "${SIMGRADING}" -a -x "${SIMGRADING}/gsl-make-trace" ; then
    echo >&2 "ERROR:  SIMGRADING or SIMGRADING/gsl-make-trace not found on system."
    echo >&2 "ERROR:  have you run setup.sh from grader-resources.tar.bz2 in your shell?"
    exit 1
fi

cat <<EoT |grader_msg 
Running SIM with non-existent trace file.  This run should exit with non-zero
status and without OUTPUT lines...
EoT
set +e   # disregard exit status
for S in ${SIMS} ; do 
    echo ">> $S BEGIN Output <<" 
    "${simloc}/SIM" ${RUNS} /this/file/should/not/exist 
    r=$?
    echo '>> END   Output <<'
    echo ${simloc}/SIM exit status $r
    echo
    grader_keystroke
done


grader_msg <<EoT
Running SIM trash with truncated trace file.  These runs should exit with
non-zero status and without OUTPUT lines...
EoT
set +e   # disregard exit status
for S in ${SIMS} ; do 
    echo ">> ${S} BEGIN Output <<" 
    "${simloc}/SIM" ${RUNS} <("${gslexec[@]}" 12343 | head -n 30 )
    r=$?
    echo '>> END   Output <<'
    echo ${simloc}/SIM exit status $r
    echo
    grader_keystroke
done


grader_msg <<EoT
Running SIM experiment generating ${RUNS} confidence intervals.  This may take
some time ...
EoT
set -e
METRICS="l warn trashn"
missingdata=0
for S in ${SIMS} ; do
	residualprefix="__residual-${S}"
	rm -f _missingdata
	( \
    "${simloc}/SIM" ${RUNS} \
			<("${gslexec[@]}" $SEED |tee "${residualprefix}-random-run-$i.dat" )  \
			| tee "${residualprefix}-output-run-${i}.log" \
			| "${SIMGRADING}/output-pipe" ;
	echo
	) | ( while read x ; do \
		test "${x:0:1}" = ':' && echo ; echo -n $x "" ; done ; echo ) | \
	awk \
	-v ci=${RUNS} -v missingdata=_missingdata \
	-v warldat=_warl-$S-$SEED-$RUNS.dat \
	-v warndat=_warn-$S-$SEED-$RUNS.dat \
	-v trashldat=_trashl-$S-$SEED-$RUNS.dat \
	-v trashndat=_trashn-$S-$SEED-$RUNS.dat \
	'BEGIN { warl=0; warn=0; trashl=0; trashn=0; }
	/^:war-l/ { warl++; printf "%f %s\n%f %s\n\n", warl, $2, warl, $4 >warldat; }
	/^:war-n/ { warn++; printf "%f %s\n%f %s\n\n", warn, $2, warn, $4 >warndat; }
	/^:trash-l/ { trashl++; printf "%f %s\n%f %s\n\n", trashl+0.1, $2, trashl+0.1, $4 >trashldat; }
	/^:trash-n/ { trashn++; printf "%f %s\n%f %s\n\n", trashn, $2, trashn, $4 >trashndat; }'
#FIXME	END { if( warn+warl+trashn+trashl != 4*ci ) print "war-n" warn "war-l" warl "trash-n" trashn "trash-l" trashl >_missingdata ; }'
	test -f _missingdata && missing_data=1

    echo >&2
    grader_save_residuals ${S}

    if test ${missingdata} -eq 1 ; then 
        #grader_missing_data $S $RUNS $SEED 231
		FIXME
    fi

    grader_msg <<EoT
... generating comparison plots for ${S} ...
EoT
    for x in ${METRICS} ; do 
        # generate plots from the *good* data results
        gp=_$x-$S-${SEED}-${RUNS}.gplot
        cat "${graderloc}/_${x}-$S-plot.gplot" | sed \
				-e 's/\(title.*\)N=0\(.*\)/\1'"N=${RUNS}"'\2/' \
				-e "s/_\([^-]*-[^-]*\)-0-0/_\1-$SEED-$RUNS/g" |\
            tee "${gp}" | cat >/dev/null
            gnuplot -d -e "set terminal pdf" -e 'set output "'${gp/gplot/pdf}'"' "${gp}"
    done
done

grader_msg <<EoT
Inspect _*-$(grader_args2re ${METRICS})-*-*.pdf for 
submission results vs. rubric results.  See the comments at the top of this
script for borderline cases...
EoT

