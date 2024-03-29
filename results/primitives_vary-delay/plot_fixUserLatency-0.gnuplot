#set datafile separator ",";

set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'primitives_fixUserLatency-0.eps'

XTICS="0 25 50 75 100 125 150 175 200 225"

set for [i=1:words(XTICS)] xtics ( word(XTICS,i) i-1 )

set xrange [0:9]
#set logscale y
set xlabel "inter-cluster RTT (ms)"
set ylabel "latency (ms)"

set pointsize 2
set key left

plot "data_syncQueueT.raw" using 1 title "Sync. Queue" with linespoints lt -1,\
"data_asyncQueueT.raw" using 1 title "Async. Queue" with linespoints lt -1,\
"data_syncTAST.raw" using 1 title "Sync. TAS" with linespoints lt -1,\
"data_asyncTAST.raw" using 1 title "Async. TAS" with linespoints lt -1


