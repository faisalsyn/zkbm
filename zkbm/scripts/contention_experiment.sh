#!/bin/bash

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [number_of_trials] {log args}"
	exit
fi

num_trials=$1
shift

pwd=$(pwd)

NUM_CYCLES=50
INTERARRIVAL_TIME=200
#SERVICE_TIME=0
rtt=100

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	#for rtt in 0 1 2 3 4 5 10 15 20 25 30 40 50 75 100 125 150 200 250 300
	for SERVICE_TIME in 1 25 50 75 100 125 150 175 200
	do
		echo "	SERVICE_TIME: ${SERVICE_TIME}"
		./modify_delays_local.sh del ${rtt} &> /dev/null
		./modify_delays_local.sh add ${rtt}

		#./latency.sh > /dev/null
		#lat=$( ./latency.sh )

		latQ=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -q 2>/dev/null )
		sleep 2
		latA=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -a 2>/dev/null )
		sleep 2
		latL=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -l 2>/dev/null )
		sleep 2
		latT=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -t 2>/dev/null )
		#sleep 2
		#latE=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -e 2>/dev/null )

		#cd "$pwd"
		echo "$latQ" > "../results/contention_service_${SERVICE_TIME}_rtt_${rtt}.$i.[$*].Q"
		echo "$latA" > "../results/contention_service_${SERVICE_TIME}_rtt_${rtt}.$i.[$*].A"
		echo "$latL" > "../results/contention_service_${SERVICE_TIME}_rtt_${rtt}.$i.[$*].L"
		echo "$latT" > "../results/contention_service_${SERVICE_TIME}_rtt_${rtt}.$i.[$*].T"
		#echo "$latE" > "../results/contention_service_${SERVICE_TIME}_rtt_${rtt}.$i.[$*].E"

		sleep 2
	done
done

./modify_delays_local.sh del 0
