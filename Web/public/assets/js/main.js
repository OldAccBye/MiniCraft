$(() => {
    if (document.cookie.indexOf('cookieCheck=') === -1) {
        $('#cookieCheck').toggleIt('d-none');
        setTimeout(() => $('#cookieCheck').toggleIt('load'), 20);
    }

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
    const windowWidth = window.innerWidth ?? (document.documentElement.clientWidth ?? screen.width),
    windowHeight = window.innerHeight ?? (document.documentElement.clientHeight ?? screen.height),
    positionLeft = ((windowWidth / 2) - 450) + (window.screenLeft ?? screen.left),
    positionTop = ((windowHeight / 2) - 250) + (window.screenTop ?? screen.top);

    window.open("...", "MiniCraft - Donate", "scrollbars=yes, width=900, height=500, top=" + positionTop + ", left=" + positionLeft);
}

function setCookieCheck() {
    document.cookie = "cookieCheck=true";
    $('#cookieCheck').toggleIt('hide');
    setTimeout(() => $('#cookieCheck').toggleIt('d-none'), 500);
}