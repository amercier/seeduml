#!/bin/bash

# ==============================================================================
# 
# NAME
#       seeduml.sh - PlantUML diagram generator from code base
#
# SYNOPSIS
#       seeduml.sh [OPTIONS] FILE|DIRECTORY [FILE|DIRECTORY ...]
#
# DESCRIPTION
#       seeduml.sh is a Bash script that provides a non-interactive way to
#       generate PlantUML class diagrams from the following source code base:
#           - XML Schemas      (.xsd)
#           - PHP files        (.php)
#           - JavaScript files (.js)
#
# DEPENDENCIES
#       TODO update dependencies
#
# OPTIONS
#       --help
#            Display this help and exit.
#
#       --version
#            Print the version number of controlpanel.sh to the standard output
#            stream.
#       
#       -l LANGUAGE, --language LANGUAGE
#            Force the language. LANGUAGE is one of the supported language: xsd,
#            js, php.
#
# LICENSE
#        Copyright (C) 2012 Alexandre Mercier and/or its affiliates.
#        
#        Permission is hereby granted, free of charge, to any person obtaining a
#        copy of this software and associated documentation files (the
#        "Software"), to deal in the Software without restriction, including
#        without limitation the rights to use, copy, modify, merge, publish,
#        distribute, sublicense, and/or sell copies of the Software, and to
#        permit persons to whom the Software is furnished to do so, subject to
#        the following conditions:
#        
#        The above copyright notice and this permission notice shall be included
#        in all copies or substantial portions of the Software.
#        
#        THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
#        OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
#        MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
#        IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
#        CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
#        TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
#        SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
# 
# AUTHOR 
#        Alexandre Mercier (alexandre.mercier12@gmail.com)
# 
# ==============================================================================
# version 1.0


# ------------------------------------------------------------------------------
# Displays this script's usage
# 
# @param $1 int Exit code, positive integer > 1
# ------------------------------------------------------------------------------
function show_syntax()
{
	echo >&2
	echo "Usage: $0 COMMAND [ARGUMENTS...]" >&2 >&2
	echo "Use $0 --help for a more detailed help" >&2
	
	if [ $# = 2 ]; then
		echo >&2
		echo -e "`tput bold`$2`tput sgr0`" >&2
		echo >&2
	fi
	
	exit $1
}


# ------------------------------------------------------------------------------
# Documentation
#
# The documentation is extracted from the script itself. All the text between
# the lines are included (78 characters "=")
# Words in HELP_BOLD are displayed as bold
# Words in HELP_UNDERLINE are displayed as underlined
# 
# Parameters:
#     none
# ------------------------------------------------------------------------------

HELP_BOLD=(NAME SYNOPSIS DESCRIPTION COMMANDS " OPTIONS" EXAMPLES TODO LICENSE "AUTHOR " NOK OK "$(basename $0)" --help --version --generate ' get ' ' reset ' ' set ' list-settings list-sections list-data add setup reset "'set'" '  backup ' '  restore ' )
HELP_UNDERLINE=(" COMMAND " ARGUMENTS SETTING VALUE PREFIX DATA SECTION FILE TAG TIMESTAMP REVISION COMMITER)

function show_help {

	# Generates the delimiters
	# We can't hard-code the delimiters as they would themselves be included
	# in the output of the command
	
	ch="$(printf "%78s" "")"
	doc_start_pattern=$(printf "%s\n" "${ch// /=}")
	doc_end_pattern=$(printf "%s\n" "${ch// /=}")
	
	# Extracts the documentation, and remove the # characters at the
	# beginning of each line
	documentation=$(cat $0 | sed -n "/$doc_start_pattern/,/$doc_end_pattern/p" | head -n-1 | tail -n+2 | sed -e "s/^#/ /g")
	
	# Add bold characters
	for i in ${!HELP_BOLD[*]}; do
		word=${HELP_BOLD[$i]}
		documentation=$(echo "$documentation" | sed -e "s/$word/`tput bold`$word`tput sgr0`/g")
	done
	
	# Add underline characters
	for i in ${!HELP_UNDERLINE[*]}; do
		
		oldword=$(echo "${HELP_UNDERLINE[$i]}" | sed -e 's/\[/\\[/g' | sed -e 's/\]/\\]/g')
		U="$(echo -e "\033[4m")"
		N="$(tput sgr0)"
		
		# Prevent from underlining the brackets and spaces
		# Example: with "[OPTIONS]", only OPTIONS will be underlined
		#          with " OPTIONS ", only OPTIONS will be underlined
		newword=$(echo "$U$oldword$N" | sed -e 's/\\\[/'$N'\\\['$U'/g' | sed -e 's/\\\]/'$N'\\\]/g' | sed -e 's/ /'$N' '$U'/g')
		
		documentation=$( \
			echo "$documentation" \
			| sed -e "s/$oldword/$newword/g" \
		)
	done
	
	# Shows the documentation
	echo "$documentation"
}


# ------------------------------------------------------------------------------
# Version 
#
# Version number is extracted from the script itself, at the line "# version XXX"
# ------------------------------------------------------------------------------

function show_version {
	echo "$(basename $0)  $(cat $0 | egrep '^# version ' | sed -e 's/# version //')"
}


# ------------------------------------------------------------------------------
# Tag filter
# 
# Input:
#     379 amercier              Apr 17 08:17 ./
#     153 amercier              Jan 27 00:22 0.8/
#     357 amercier              Apr 12 03:28 2.0.0-rc1/
#     370 amercier              Apr 17 06:56 2.0.0-rc2/
#     374 amercier              Apr 17 07:20 2.0.0-rc3/
# 
# Output:
#     

function filter_tag {
	while read revision username month day time tag; do
		echo $month $day $time;
	done
}


# ##############################################################################


# ------------------------------------------------------------------------------
# Script starts here
# ------------------------------------------------------------------------------

# Check the parameters count
if [ "$#" -lt "1" ]; then
	show_syntax 1
fi

# Switch on the first parameter
case $1 in
	
	"svn-status")
		cd "$(dirname $(readlink -f $0))/../.."
		if [ "$(svn info 2>/dev/null | wc -l)" == "0" ]; then
			echo "Not connected"
		else
			echo "Connected"
		fi
		;;
	
	"svn-url")
		export LANG=en
		cd "$(dirname $(readlink -f $0))/../.."
		svn info 2>/dev/null | grep '^URL' | egrep -o '[^ ]+$' | sed 's/\/branches\/.*$//' | sed 's/\/tags\/.*$//' | sed 's/\/trunk$//'
		;;
		
	"svn-path")
		export LANG=en
		cd "$(dirname $(readlink -f $0))/../.."
		svn info 2>/dev/null | grep '^URL' | egrep -o '(tags|branches)/[^/]+|trunk'
		;;
		
	"svn-tags")
		export LANG=en
		filename="$(readlink -f $0)"
		cd "$(dirname $filename)/../.."
		#echo $(svn --xml list $($filename svn-url)/tags 2>/dev/null) | xpath -e '//lists/list/entry' 2>/dev/null | while read line; do
		#echo $(svn --xml list $($filename svn-url)/tags 2>/dev/null) | xmlstarlet sel -t -v '//lists/list/entry' 2>/dev/null | while read line; do
		echo $(svn --xml --incremental list $($filename svn-url)/tags 2>/dev/null) | xargs echo | sed 's/<entry/\n<entry/g' | sed '1d' | while read line; do
		    echo -n -e "$(date -d $(echo -n line | echo $line | sed -rn 's/.*<date>(.*)<\/date>.*/\1/p' | egrep -o '^[^.]+') +%s)"
		    echo -n -e "\t$(echo -n line | echo $line | sed -rn 's/.*<name>(.*)<\/name>.*/\1/p')"
		    echo -n -e "\t$(echo -n line | echo $line | sed -rn 's/.*revision=\"(.*)\">.*/\1/p')"
		    echo    -e "\t$(echo -n line | echo $line | sed -rn 's/.*<author>(.*)<\/author>.*/\1/p')"
		done | sort
		;;
	
	"svn-tag")
		$0 svn-tags | grep "$($0 svn-branch)" | tail -n 1 | awk '{ print $2; }'
		;;
	
	"svn-tag-revision")
		if [ "$#" != "2" ]; then
			show_syntax 2
		fi
		#echo "[]\\/.{}" | sed -e 's/[\/\.\[\{\}]/\\&/g' | sed -e 's/]/\\]/g'
		$0 svn-tags | egrep "\\s$(echo $2 | sed -e 's/[\/\.\[\{\}]/\\&/g' | sed -e 's/]/\\]/g')\\s" | awk '{ print $3; }'
		;;
		
	"svn-branch")
		export LANG=en
		cd "$(dirname $(readlink -f $0))/../.."
		svn info 2>/dev/null | grep '^URL' | egrep -o '(tags|branches)/[^/]+|trunk' | egrep -o '[^/]+$'
		;;
		
	"svn-revision")
		export LANG=en
		cd "$(dirname $(readlink -f $0))/../.."
		svn info 2>/dev/null | grep Revision | egrep -o '[^ ]+$'
		;;
		
	"svn-version")
		if [ "$($0 svn-path)" == "trunk" ]; then
			echo "dev-$($0 svn-revision)"
		else
			#$0 svn-tags | grep "$($0 svn-branch)" | tail -n 1 | awk '{ print $2"-"$3; }'
			echo "$($0 svn-tag)-$($0 svn-revision)"
		fi
		;;
	
	"save-svn-version")
		$0 svn-version > "$(dirname $(readlink -f $0))/../../application/configs/version.txt"
		;;
		
	"--help")
		show_help
		;;
		
	"--version")
		show_version
		;;
		
	*)
		show_help 2 "Unknown command $B$1$R."
		exit 1
		;;

esac

exit 0

