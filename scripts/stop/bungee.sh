screen -S FFAServer -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S GTCServer-2 -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S GTCServer-1 -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S LobbyServer-2 -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`
screen -S LobbyServer-1 -X stuff 'say Server wird in 10 Sekunden neugestartet!'`echo -ne '\015'`

sleep 10

screen -S FFAServer -X stuff 'stop'`echo -ne '\015'`
screen -S GTCServer-2 -X stuff 'stop'`echo -ne '\015'`
screen -S GTCServer-1 -X stuff 'stop'`echo -ne '\015'`
screen -S LobbyServer-2 -X stuff 'stop'`echo -ne '\015'`
screen -S LobbyServer-1 -X stuff 'stop'`echo -ne '\015'`
screen -S Waterfall -X stuff 'end'`echo -ne '\015'`