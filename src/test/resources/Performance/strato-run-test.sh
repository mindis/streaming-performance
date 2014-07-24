#!/bin/bash
toDir=$1
testParams=$2
length=$3
stratoDir=$4
jarFile=$5
classPath=$6

if [ -d "${toDir}" ] ; then
	echo "removing files"
	./strato-remove-files.sh $stratoDir

	paramsWithSpace="${testParams//_/ }"

	rm -r $toDir/$testParams/*;
	mkdir $toDir/$testParams;

	ssh -n strato@dell150.ilab.sztaki.hu "timeout ${length} ./$stratoDir/bin/flink run -v -j ./$stratoDir/lib/${jarFile} -c $classPath -a cluster /home/strato/${stratoDir}/resources/hamlet.txt /home/strato/${stratoDir}/log/counter/ /home/strato/${stratoDir}/lib/${jarFile} ${paramsWithSpace}"
    ssh -n strato@dell150.ilab.sztaki.hu "$stratoDir/bin/stop-cluster.sh; sleep 2; $stratoDir/bin/start-cluster.sh"

	echo "job finished"

	echo "copying"
	./strato-copy-files.sh $toDir/$testParams $stratoDir
else
	echo "USAGE:"
	echo "run <save directory> <test params separated by _> <length of test in seconds> <strato directory> <jar file to run> <class to run (whole path)>"
fi
