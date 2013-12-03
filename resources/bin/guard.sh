#!/bin/ksh
TZ=GMT; export TZ;

ALERT_NOTIFICATION_EMAIL=monitoring@billiongoods.ru
SUPPORT_TEAM_EMAIL=monitoring@billiongoods.ru

seach_instance ()
{
	echo `ps ux | awk '/java/ && !/awk/ {print $2}'`
}

bounce_instance ()
{
	echo "Bouncing app..."
	sudo service tomcat7 restart
#	mailx -s "[MIAS] Dead Instance found by guard" -R $SUPPORT_TEAM_EMAIL $ALERT_NOTIFICATION_EMAIL < guardBounceMail.txt
	echo "Instance has been bounced"
}

echo "=== Start script: $(date)"

PID=`seach_instance`
echo "The following PID found: $PID"
if [ -z $PID ]; then
	bounce_instance
	exit 1
fi