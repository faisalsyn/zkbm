
#set title "Latencies of 10000 operations on one zookeeper server with asynchronous operations while changing number of processors"
set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output '1_machine_diff_cpu.eps'
#set auto x
set yrange [0:30000]
set style data histogram
set style histogram cluster gap 1
set style fill solid border -1
#set boxwidth 0.9
set xtic rotate by -45 scale 0
set xlabel "operation"
set ylabel "time (ms)"
#set bmargin 10 
plot '1_machine_diff_cpu.data' using 2:xtic(1) ti col, '' using 3:xtic(1) ti col, '' using 4:xtic(1) ti col
