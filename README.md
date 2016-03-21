# sc
Service Control for configuring systemd services

* ntsysv alike command line for systemd


```sh
[root@an3 rpmbuild]$ sh sc -h
getopt: invalid option -- 'h'

ntsysv-systemd: ntsysv alike command line for systemd
Usage: sc -[ade]
Options:
         -a print state enabled, disabled and static
         -d print only disabled state
         -e print only enabled state
    If do not give any options, print enabled or disabled state

[root@an3 rpmbuild]$
```
