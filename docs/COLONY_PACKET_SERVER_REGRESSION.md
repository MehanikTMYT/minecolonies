# Colony packet dedicated-server regression (2026-10-27)

`ColonyPackageManager` called a static writer on `ColonyView`, whose class references ClientLevel. On a dedicated 26.1.2 server the QA invocation reproduced NoClassDefFoundError for net/minecraft/client/multiplayer/ClientLevel.

The writer now lives in common-only `ColonyNetworkData`. The existing ColonyView method delegates to it, preserving its signature and packet order. No gameplay or packet-format changes are intended.

Validation on NL, Java 25 / NeoForge 26.1.2.122:
- Full MineColonies build passed with local OperaPublicaCreator script.
- Old packaged JAR: opt-in Volcanic Mason probe VMQA_PACKET_WRITER=...ColonyView reproduced the classloading failure.
- New packaged JAR: VMQA_PACKET_WRITER=...ColonyNetworkData passed packet name/dimension/center/remaining-data assertions and reached VMQA COMPLETE; server stopped normally.
- Existing serializer body compared against HEAD before extraction; unchanged except constant ownership.

Evidence: /root/home/agent-work/ports-audit-20261027/{minecolonies-build.log,colony-packet-before.log,colony-packet-after.log}. Probe source: colonial-addons/VolcanicMason/src/qa/java/dev/ferriarnus/volcanicmason/qa/ServerProbe.java.

Not yet tested: connected graphical client, full packet round-trip, prolonged multiplayer subscriptions and resource reload. This regression proves common-side serialization without loading ClientLevel; it does not certify all client interactions.
