#Este script se encarga del envió automatico de las alertas registradas en la tabla filtro_alertas_historicos de la BD dbsaai
cd /tools/mailer/bin
java -cp .:../lib/* -Xmx128m main/MainEnvioAutomaticoAlertasBLOB >> /tools/mailer/MailerThreadsAutomatico.log 2>&1 &
