screen -S PrivatNormal -X stuff 'say Server wird in 30 Sekunden neugestartet!'`echo -ne '\015'`
screen -S PrivatForge -X stuff 'say Server wird in 30 Sekunden neugestartet!'`echo -ne '\015'`

sleep 30

screen -S PrivatNormal -X stuff 'stop'`echo -ne '\015'`
screen -S PrivatForge -X stuff 'stop'`echo -ne '\015'`