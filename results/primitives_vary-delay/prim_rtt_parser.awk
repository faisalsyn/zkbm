#!/usr/bin/awk

function pad_zero(value) {
  if(value < 10) return "00" value
  if(value < 100) return "0" value
  return value
}

BEGIN {
  prev_filename = ""
}

{
  if(FILENAME != prev_filename) {

    prev_filename = FILENAME

    match(FILENAME, /_[0-9]+\./)
    client_rtt = int(substr(FILENAME, RSTART+1, RLENGTH-2))

    match(FILENAME, /=[0-9]+\]/)
    server_rtt = int(substr(FILENAME, RSTART+1, RLENGTH-2))

    mode = "?"
    if(FILENAME ~ /.Q$/) mode = "sq"
    if(FILENAME ~ /.A$/) mode = "aq"
    if(FILENAME ~ /.L$/) mode = "sl"
    if(FILENAME ~ /.T$/) mode = "al"
    if(FILENAME ~ /.E$/) mode = "eq"

    server_rtts[pad_zero(server_rtt)] = 1
    client_rtts[pad_zero(client_rtt)] = 1

  }

}

/Execution/ {
  time = int($3)
  exec_times[server_rtt, client_rtt, mode] = time
}

/WaitTime/ {
  time = int($3)
  wait_times[server_rtt, client_rtt, mode] = time
}

END {
  asorti(server_rtts)
  asorti(client_rtts)

  print_table("sq");
  printf("\n")

  print_table("aq");
  printf("\n")

  print_table("eq");
  printf("\n")

  print_table("sl");
  printf("\n")

  print_table("al");
  printf("\n")


}

function print_table(mode) {
  printf(mode)
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
      printf("\t%d", wait_times[srv, cli, mode])
    }
    printf("\n")
  }
}
