screen -S GTCServer -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S FFAServer -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S LobbyServer -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`

sleep 10

screen -S GTCServer -X stuff 'stop'`echo -ne '\015'`
screen -S FFAServer -X stuff 'stop'`echo -ne '\015'`
screen -S LobbyServer -X stuff 'stop'`echo -ne '\015'`
screen -S Waterfall -X stuff 'end'`echo -ne '\015'`