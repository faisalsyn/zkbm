if [ $# -ne 1 ]
then
	echo "enter the name of the primary tex file. if it is xxxx.tex, then enter $1 xxxx without the .tex"
	exit
fi

latex $1.tex ; bibtex $1 ; latex $1.tex ; latex $1.tex ; dvipdf $1.dvi
#latex TMAC-camera-ready-8May.tex; dvips -o TMAC-camera-ready-8May.ps TMAC-camera-ready-8May.dvi; ps2pdf TMAC-camera-ready-8May.ps
