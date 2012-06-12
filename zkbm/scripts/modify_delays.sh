# modify the delay on cluster servers

if [ $# -lt 2 ]
then
	echo "USAGE: $0 [add/del] [delay]"
	exit
fi

# delay of incoming packets
indelay=$2
# delay of outgoing packets
outdelay=$2

echo $1 and $2

if [ $1 == "setup" ]
then
	pssh -t 0 sudo modprobe ifb
	pssh -t 0 sudo ip link set dev ifb0 up
	pssh -t 0 sudo tc qdisc add dev eth0 ingress
	pssh -t 0 sudo tc filter add dev eth0 parent ffff: protocol ip u32 match u32 0 0 flowid 1:1 action mirred egress redirect dev ifb0
else

	p_outdelay=$(($outdelay / 10))
	p_indelay=$(($indelay / 10))	

	pssh -t 0 sudo tc qdisc $1 dev eth0 root handle 1: prio
	pssh -t 0 sudo tc qdisc $1 dev eth0 parent 1:3 handle 30: netem delay ${outdelay}ms

	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.237/32 flowid 1:3
	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.241/32 flowid 1:3
	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.163/32 flowid 1:3
	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.163/32 flowid 1:3
	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.167/32 flowid 1:3
	pssh -t 0 sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.108/32 flowid 1:3

	sudo tc qdisc $1 dev eth0 root handle 1: prio
        sudo tc qdisc $1 dev eth0 parent 1:3 handle 30: netem delay ${outdelay}ms

        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.237/32 flowid 1:3
        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.241/32 flowid 1:3
        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.163/32 flowid 1:3
        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.163/32 flowid 1:3
        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.167/32 flowid 1:3
        sudo tc filter $1 dev eth0 protocol ip parent 1:0 prio 3 u32 match ip dst 128.111.44.108/32 flowid 1:3

	#pssh -t 0 sudo tc qdisc $1 dev ifb0 root netem delay $indelay
	#pssh -t 0 sudo tc qdisc $1 dev eth0 root netem delay $outdelay
fi



