# modify the drops percentage on cluster servers

if [ $# -lt 1 ]
then
	echo "USAGE: $0 [drops]"
	exit
fi


if [ $1 == "setup" ]
then
	pssh -t 0 sudo modprobe ifb
	pssh -t 0 sudo ip link set dev ifb0 up
	pssh -t 0 sudo tc qdisc add dev eth0 ingress
	pssh -t 0 sudo tc filter add dev eth0 parent ffff: protocol ip u32 match u32 0 0 flowid 1:1 action mirred egress redirect dev ifb0
else
	pssh -t 0 sudo tc qdisc change dev ifb0 root netem loss $1
	pssh -t 0 sudo tc qdisc change dev eth0 root netem loss $1
fi



