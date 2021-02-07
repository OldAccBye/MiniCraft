screen -S GTCServer -X stuff 'say Server wird in 30 Sekunden neugestartet!'`echo -ne '\015'`
screen -S FFAServer -X stuff 'say Server wird in 30 Sekunden neugestartet!'`echo -ne '\015'`
screen -S LobbyServer -X stuff 'say Server wird in 30 Sekunden neugestartet!'`echo -ne '\015'`

sleep 30

screen -S GTCServer -X stuff 'stop'`echo -ne '\015'`
screen -S FFAServer -X stuff 'stop'`echo -ne '\015'`
screen -S LobbyServer -X stuff 'stop'`echo -ne '\015'`
screen -S BungeeCord -X stuff 'end'`echo -ne '\015'`