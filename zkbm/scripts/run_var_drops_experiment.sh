#!/bin/bash

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials] {other_args}"
	exit
fi

num_trials=$1
shift

drops=( "0.0%" "0.01%" "0.05%" "0.1%" "0.5%" "1.0%" "5.0%" )

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	for (( y=0 ; y<1 ; y++ ))
	do
		echo "	drops: ${drops[$y]}"
		./modify_drops.sh ${drops[$y]}
		
		./run_multiTest.sh "$*" > "../results/varDrops_drops-${drops[$y]}_[$*].$i"
	done
done

./modify_drops.sh "0.0%"

