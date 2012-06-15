#set datafile separator ",";

set terminal postscript "Helvetica" 20
set terminal postscript color
set terminal postscript eps enhanced
set output 'primitives_vary-contention_rtt-0.eps'

#XTICS="0 25 50 75 100 125 150 175 200"

#set for [i=1:words(XTICS)] xtics ( word(XTICS,i) i-1 )

#set logscale y
set xlabel "service time (ms)"
set ylabel "latency (ms)"

set key left

set pointsize 2

plot "data_rtt-0.raw" using 1:2 title col with linespoints lt -1,\
"" using 1:3 title col with linespoints lt -1,\
"" using 1:4 title col with linespoints lt -1,\
"" using 1:5 title col with linespoints lt -1


