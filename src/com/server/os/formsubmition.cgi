#!/bin/sh
QUERY_STRING=$1
java -Dcgi.query_string=$QUERY_STRING FormSubmition
