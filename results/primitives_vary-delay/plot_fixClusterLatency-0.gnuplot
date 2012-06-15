#set datafile separator ",";

set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'primitives_fixClusterLatency-0.eps'

XTICS="0 25 50 75 100 125 150 175 200 225 250"

set for [i=1:words(XTICS)] xtics ( word(XTICS,i) i-1 )

#set logscale y
set xlabel "user to cluster RTT (ms)"
set ylabel "latency (ms)"

set key left

plot "data_syncQueue.raw" using 1 title "Sync. Queue" with linespoints lt -1,\
"data_syncTAS.raw" using 1 title "Sync. TAS" with linespoints lt -1,\
"data_asyncQueue.raw" using 1 title "Async. Queue" with linespoints lt -1,\
"data_asyncTAS.raw" using 1 title "Async. TAS" with linespoints lt -1


