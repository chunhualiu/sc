#!/bin/bash
#
# Copyright (C) 2015 Chunhua Liu
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; either version 2 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.
#

line=$(stty size)
items=($line)
LINES=${items[0]}
COLUMNS=${items[1]}
test $LINES   -eq 0 && LINES=24
test $COLUMNS -eq 0 && COLUMNS=80

declare -A enabled_services
checklist=""
width=0

while read line;
do
	if [ -z "$line" ]; then
		break
	fi
	
	if [[ "$line" =~ "UNIT FILE".* ]]; then
		continue
	fi
	
	items=($line)
	unit=${items[0]}
	state=${items[1]}
	
	if (( ${#unit} > width )); then
		width=${#unit}
	fi
	
	if [ $state = "enabled" ]; then
		checklist="$checklist $unit $state ON"
		enabled_services["$unit"]="enabled"
	else
		checklist="$checklist $unit $state OFF"
	fi
	
done <<< "$(systemctl list-unit-files --type=service)"

width=$((width+30))
if (( width > $COLUMNS )); then
	width=$COLUMNS
fi

height=$(($LINES-5))

services=$(whiptail --backtitle "sc 1.0 - (C) 2015 Chunhua Liu" --title "SystemD Services" --checklist "What services should be automatically started?" $height $width $((height-7)) $checklist 3>&1 1>&2 2>&3)

if [ $? -ne 0 ]; then
	exit 1
fi

for s in $services;
do
	s=${s#\"}
	s=${s%\"}
	state=$(systemctl is-enabled $s)
	if [ $state != "enabled" ]; then
		cmd="systemctl enable $s"
		echo $cmd
		eval $cmd
	fi
	unset enabled_services[$s]
done

for s in ${!enabled_services[*]};
do
	cmd="systemctl disable $s"
	echo $cmd
	eval $cmd
done
