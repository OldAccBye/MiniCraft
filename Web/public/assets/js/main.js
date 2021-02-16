$(() => {
    if ($('#donate') !== null) {
        $('#donate').on('submit', async (e) => {
            e.preventDefault();
        
            const formData = new FormData(e.target);
        
            await fetch('/posts/donate', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({
                    username: formData.get('username'),
                    securitycode: formData.get('securitycode'),
                    email: formData.get('email'),
                    pin: formData.get('pin'),
                    sendMail: formData.has('sendMail') ? 1 : 0
                })
            }).then(response => response.json()).then(data => $('#message').html(data.message));
        });
    }
});