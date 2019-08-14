#!/bin/sh

QUERY_STRING=$1
FNAME=$2
LNAME=$3
EMAIL=$4
GENDER=$5

#java FormSubmition $QUERY_STRING

java -Dcgi.query_string=$QUERY_STRING -Dcgi.fname=$FNAME -Dcgi.lname=$LNAME -Dcgi.email=$EMAIL -Dcgi.gender=$GENDER FormSubmition


# #!/bin/sh
# QUERY_STRING=$1
# echo $1
# java -Dcgi.query_string=$QUERY_STRING FormSubmition
