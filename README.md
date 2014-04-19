HDMI Toggle
===========

Simple widget to toggle HDMI, for OMAP4 based devices with Kernel that support HDMI Toggle 

It uses JBX kernel feature to control HDMI via sysfs:
> echo 1 > /sys/kernel/hdmi/hdmi_active
