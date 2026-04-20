## Package: OH\_Ops

This package contains functionality used by the OneHUB operations team.

### Operational State

The operational state of this server affects how it responds to load balancer pings. Operational state is configured with the extended setting:

    watt.an.ops.state

Possible values for the operational state are:

    ACTIVE   # Responds with successfully (HTTP 200) to load balancer pings. 
    OFFLINE  # Responds with negatively (HTTP 500) to load balancer pings.


Setting the operational state to `OFFLINE` (or anything other than `ACTIVE`) causes negative responses to load balancer pings. The `OFFLINE` state can therefore be used to leave the server up and available (eg. for maintenance), while preventing new requests from arriving. 


### Trading Networks Archiving

Trading Networks documents are archived by daily scheduled invocations of the service:

    oh.ops.pub.tn:archive

This service is controlled with the following extended settings:

    watt.an.ops.tn.archive.archiveAfterDays    # After this many days, move TN docs from runtime to archive tables
    watt.an.ops.tn.archive.deleteAfterDays     # After this many days, delete TN docs from both runtime and archive tables
    watt.an.ops.tn.archive.maxRows             # Max number of rows to archive/delete for entire archive job run
    watt.an.ops.tn.archive.batchSize           # Interatively archive/delete by batches of this size

