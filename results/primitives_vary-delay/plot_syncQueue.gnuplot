
#set title "Latencies of 10000 operations on one zookeeper server with asynchronous operations"
set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'primitives_latencies_varDelay_syncqueue.eps'

set xlabel "inter-cluster latency (ms)"
set ylabel "client-server latency (ms)"

XTICS="0 25 50 75 100 125 150 175 200 225 250"
YTICS="0 25 50 75 100 125 150 175 200 225 250"

set for [i=1:words(XTICS)] xtics ( word(XTICS,i) i-1 )
set for [i=1:words(YTICS)] ytics ( word(YTICS,i) i-1 )

set view map
set pm3d map

splot 'data_syncQueue.raw' matrix with image 
