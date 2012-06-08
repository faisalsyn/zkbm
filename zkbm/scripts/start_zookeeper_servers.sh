#!/bin/sh
pscp zoo.cfg ~/zoo/
pssh -t 0 sudo /usr/bin/java -Dzookeeper.log.dir=/tmp/zookeeper/ -Dzookeeper.root.logger=DEBUG,ROLLINGFILE -cp /etc/zookeeper/conf:/usr/share/java/jline.jar:/usr/share/java/log4j-1.2.jar:/usr/share/java/xercesImpl.jar:/usr/share/java/xmlParserAPIs.jar:/usr/share/java/zookeeper.jar org.apache.zookeeper.server.quorum.QuorumPeerMain $HOME/zoo/zoo.cfg &
