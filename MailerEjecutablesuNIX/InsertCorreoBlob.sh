#Este script inserta 15 correos html sobre pedimento en rojo 
cd /tools/mailer/bin/ 
java -cp .:../lib/* main/InsertarMailPruebaBlobMain >> /tools/mailer/TestInsertBlob.log & 
