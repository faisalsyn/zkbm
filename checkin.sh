#!/bin/sh

if [ $# -ne "2" ]
then
  echo "$# Execute the script as ./checkin <Destination Path>  <Commit Message>"
  exit
fi
#git add frontend/*
git add $1

#git commit -m "Removing NodeSQL.php file"
git commit -m "$2"

git push origin master
