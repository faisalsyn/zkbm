#!/usr/bin/awk

function pad_zero(value) {
  if(value < 10) return "00" value
  if(value < 100) return "0" value
  return value
}

BEGIN {}

/^created .*permanent/ {
  match(FILENAME, /_[0-9]+\./)
  client_rtt = int(substr(FILENAME, RSTART+1, RLENGTH-2))

  match(FILENAME, /=[0-9]+\]/)
  server_rtt = int(substr(FILENAME, RSTART+1, RLENGTH-2))

  match($8, /[0-9\.]+/)
  time = substr($8, RSTART, RLENGTH)

  times[server_rtt, client_rtt] = time

  server_rtts[pad_zero(server_rtt)] = 1
  client_rtts[pad_zero(client_rtt)] = 1
}

END {
  asorti(server_rtts)
  asorti(client_rtts)

  for(i=0;i<length(server_rtts);i++) {
    srv = int(server_rtts[i])
    printf("\t%d", srv)
  }
  printf("\n")

  for(i=0;i<length(client_rtts);i++) {
    cli = int(client_rtts[i])
    printf("%d", cli)

    for(j=0;j<length(server_rtts);j++) {
      srv = int(server_rtts[j])
      printf("\t%0.2f", times[srv, cli])
    }
    printf("\n")
  }
}
