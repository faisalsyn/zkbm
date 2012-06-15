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

NUM_CYCLES=50
INTERARRIVAL_TIME=500
SERVICE_TIME=0

for (( i=1 ; i<=$num_trials ; i++ ))
do
	echo "Trial: $i"
	#for rtt in 0 1 2 3 4 5 10 15 20 25 30 40 50 75 100 125 150 200 250 300
	for rtt in 0 25 50 75 100 125 150 175 200 225 250
	do
		echo "	rtt: ${rtt}"
		./modify_delays_local.sh del ${rtt} &> /dev/null
		./modify_delays_local.sh add ${rtt}

		#cd "$SMOKE_PATH"
		#./latency.sh > /dev/null
		#lat=$( ./latency.sh )

		latQ=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -q 2>/dev/null )
		sleep 2
		latA=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -a 2>/dev/null )
		sleep 2
		latL=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -l 2>/dev/null )
		sleep 2
		latT=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -t 2>/dev/null )
		sleep 2
		latE=$( ./run_multiTest.sh -servers gediz,rubicon,pacific,euphrates,lochness -n $NUM_CYCLES -i $INTERARRIVAL_TIME -s $SERVICE_TIME -e 2>/dev/null )

		#cd "$pwd"
		echo "$latQ" > "../results/client_rtt_${rtt}.$i.[$*].Q"
		echo "$latA" > "../results/client_rtt_${rtt}.$i.[$*].A"
		echo "$latL" > "../results/client_rtt_${rtt}.$i.[$*].L"
		echo "$latT" > "../results/client_rtt_${rtt}.$i.[$*].T"
		echo "$latE" > "../results/client_rtt_${rtt}.$i.[$*].E"

		sleep 2
	done
done

./modify_delays_local.sh del 0
