0.7
---
Added a message handler (BodyOnlyMessageHandler) that just outputs the mqtt body

0.6
----
Enabled TLS1.2 support by default.
Made the  core Modular Input Framework compatible with latest Splunk Java SDK
Please use a Java Runtime version 7+
If you need to use SSLv3 , you can turn this on in bin/mqtt.py
SECURE_TRANSPORT = "tls"
#SECURE_TRANSPORT = "ssl"

0.5
-----
Initial beta release
