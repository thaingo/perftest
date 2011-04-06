#/bin/bash

RESULTS=$PWD/pretty

rm -rf $RESULTS
mkdir $RESULTS


mkdir $RESULTS/latestthroughput
F1="$PWD/results/InMemTest/InMemGetTest/valuelength/512/cs/s1c5/1.6.5/"
T1="Membase 1.6.5 - (512b values) (client-side moxi)"
F2="$PWD/results/InMemTest/InMemGetTest/valuelength/1024/cs/s1c5/1.6.5/"
T2="Membase 1.6.5 - (1k values) (client-side moxi)"
F3="$PWD/results/InMemTest/InMemGetTest/valuelength/2048/cs/s1c5/1.6.5/"
T3="Membase 1.6.5 - (2k values) (client-side moxi)"
F4="$PWD/results/InMemTest/InMemGetTest/valuelength/4098/cs/s1c5/1.6.5/"
T4="Membase 1.6.5 - (4k values) (client-side moxi)"
sh $PWD/results/InMemTest/chart.sh $RESULTS/latestthroughput "$F1" "$T1" "$F2" "$T2" "$F3" "$T3" "$F4" "$T4"
