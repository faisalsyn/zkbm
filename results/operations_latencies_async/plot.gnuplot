
#set title "Latencies of 10000 operations on one zookeeper server with asynchronous operations"
set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'ops_latencies.eps'
#set auto x
set y2range [0:13]
set yrange [0:0.845]
set style data histogram
set style histogram cluster gap 1
set style fill solid border -1
#set boxwidth 0.9
set y2tics border

set xtic rotate by -45 scale 0
set xlabel "operation"
set ylabel "asynchronous latency (ms)"
set y2label "synchronous latency (ms)"
#set bmargin 10 
plot 'data.raw' using 2:xtic(1) axes x1y1 ti col, '' using 3:xtic(1) axes x1y2 ti col
