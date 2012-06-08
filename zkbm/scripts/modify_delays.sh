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
	pssh -t 0 sudo tc qdisc $1 dev ifb0 root netem delay $indelay
	pssh -t 0 sudo tc qdisc $1 dev eth0 root netem delay $outdelay
fi



