#!/bin/bash
#SMOKE_PATH="../../..//zk-smoketest"
SMOKE_PATH=/home/alex/project/zk-smoketest/

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials] {log args}"
	exit
fi

num_trials=$1
shift

pwd=$(pwd)

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	#for rtt in 0 1 2 3 4 5 10 15 20 25 30 40 50 75 100 125 150 200 250 300
	for rtt in 0 25 50 75 100 125 150 175 200 225 250
	do
		echo "	rtt: ${rtt}"
		./modify_delays_local.sh del ${rtt} &> /dev/null
		./modify_delays_local.sh add ${rtt}

		cd "$SMOKE_PATH"
		./latency.sh > /dev/null
		lat=$( ./latency.sh )

		cd "$pwd"
		echo "$lat" > "../results/client_rtt_${rtt}.$i.[$*]"

		sleep 3
	done
done

./modify_delays_local.sh del 0
