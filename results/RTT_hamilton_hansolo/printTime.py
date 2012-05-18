
import sys

if ( len (sys.argv) < 2 ):
	sys.exit()


fd = open (sys.argv[1], 'r')

for line in fd:
	#print line
	comps = line.split()
	try:
		#print float(comps[6].split('=')[1])
		print float(comps[6].split('=')[1])-1.6
	except:
		print "",


