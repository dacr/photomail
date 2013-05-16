#!/bin/sh

png2ico logo.ico logo.png logo32.png
cp -f *.{png,ico}   ../../conf/icons/
cp -f 3rdParty/*.{png,jpg} logo*.{png,ico}    ../../site/images/

