#!/bin/bash
if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials]"
	exit
fi

num_trials=$1
shift

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Server trial: $i"
	for rtt in 0 1 2 3 4 5 10 15 20 25 30 40 50 75 100 125 150 200 250 300
	do
		echo "	rtt: ${rtt}"
		./modify_delays.sh add ${rtt} &>/dev/null

		./client_rtt_experiment.sh $num_trials "server_rtt=$rtt"
	done
done

./modify_delays.sh del 0
