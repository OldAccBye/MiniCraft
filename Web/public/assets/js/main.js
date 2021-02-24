$(() => {
    if ($('#buyPremium') !== null) {
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
    } else if ($('#getUUID') !== null) {
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