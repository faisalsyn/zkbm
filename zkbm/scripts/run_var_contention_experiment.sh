#!/bin/bash

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials] {other_args}"
	exit
fi

num_trials=$1
shift

interarrival_time=( 2000 1000 666 500 400 333 286 250 222 )

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	for (( y=0 ; y<9 ; y++ ))
	do
		echo "	interarrival time: $interarrival_time{$y]}"
		./run_multiTest.sh -i ${interarrival_time[$y]} "$*" > "../results/varContention_interarrival-${interarrival_time[$y]}_[$*].$i"
	done
done

