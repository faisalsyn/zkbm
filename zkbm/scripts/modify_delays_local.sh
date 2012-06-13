# modify the delay on cluster servers

if [ $# -lt 2 ]
then
	echo "USAGE: $0 [add/del] [delay in ms]"
	exit
fi

# delay of incoming packets
indelay=$(($2 * 1000 / 2))
# delay of outgoing packets
outdelay=$(($2 * 1000 / 2))

echo "$1 (in=${indelay}us, out=${outdelay}us)"

if [ $1 == "setup" ]
then
	sudo modprobe ifb
	sudo ip link set dev ifb0 up
	sudo tc qdisc add dev eth0 ingress
	sudo tc filter add dev eth0 parent ffff: protocol ip u32 match u32 0 0 flowid 1:1 action mirred egress redirect dev ifb0

else

	sudo tc qdisc $1 dev ifb0 root netem delay ${indelay}us
	sudo tc qdisc $1 dev eth0 root netem delay ${outdelay}us

fi



