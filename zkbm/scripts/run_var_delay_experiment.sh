#!/bin/bash

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials] {other_args}"
	exit
fi

num_trials=$1
shift

delays=( 0ms 10ms 30ms 50ms 70ms 100ms 150ms 200ms 500ms )

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	for (( y=0 ; y<7 ; y++ ))
	do
		echo "	delay: ${delays[$y]}"
		./modify_delays.sh del ${delays[$y]}
		./modify_delays.sh add ${delays[$y]}
		
		./run_multiTest.sh "$*" > "../results/varDelays_delay-${delays[$y]}_[$*].$i"
	done
done

./modify_delays.sh del 0ms
