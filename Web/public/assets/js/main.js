$(() => {
    if (!!$('#buyPremium')) {
        $('#buyPremium').on('submit', async (e) => {
            e.preventDefault();
        
            const formData = new FormData(e.target);
        
            await fetch('/post/buyPremium', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    uuid: formData.get('uuid'),
                    securitycode: formData.get('securitycode'),
                    endOfPremium: formData.get('endOfPremium')
                })
            }).then(response => response.json()).then(data => $('#message').html(data.message));
        });
    }
    
    if (!!$('#getUUID')) {
        $('#getUUID').on('submit', async (e) => {
            e.preventDefault();
        
            const formData = new FormData(e.target);
        
            await fetch('/post/getUUID', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: formData.get('username')
                })
            }).then(response => response.json()).then(data => $('#message').html(data.message));
        });
    }
});

function openDonationDialog() {
    const sizeLeft = window.screenLeft ?? screen.left,
    sizeTop = window.screenTop ?? screen.top,
    windowWidth = window.innerWidth ?? (document.documentElement.clientWidth ?? screen.width),
    windowHeight = window.innerHeight ?? (document.documentElement.clientHeight ?? screen.height),
    positionLeft = windowWidth / 2 - 450 + sizeLeft,
    positionTop = windowHeight / 2 - 250 + sizeTop;

    var test = [{ uuid: "123-abc-def-456" }];
    test[0]['name'] = "CoolePizzaxD";
    console.log(test[0]);

    window.open("https://spenden.pp-h.eu/68454276-da3c-47c7-b95a-7fa443706a44", "MiniCraft - Donate", "scrollbars=yes, width=900, height=500, top=" + positionTop + ", left=" + positionLeft);
}