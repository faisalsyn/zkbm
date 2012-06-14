
#set title "Latencies of 10000 operations on one zookeeper server with asynchronous operations"
set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'ops_latencies_logscale.eps'
#set auto x
set yrange [:30]
#set yrange [0:0.845]
set logscale y
set style data histogram
set style histogram cluster gap 1
set style fill solid border -1
#set boxwidth 0.9

set xtic rotate by -45 scale 0
set xlabel "operation"
set ylabel "latency (ms)"
#set bmargin 10 
plot 'data.raw' using 2:xtic(1) ti col, '' using 3:xtic(1) ti col
