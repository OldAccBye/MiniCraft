$(() => {
    $('#sendMail').on('submit', async (e) => {
        e.preventDefault();
    
        const formData = new FormData(e.target);
        console.log(formData.get('group'));
    
        const response = await fetch('/posts/sendMail', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                group: formData.get('group'),
                username: formData.get('username'),
                securitycode: formData.get('securitycode'),
                email: formData.get('email'),
                text: formData.get('text')
            })
        });
    
        if (!response.ok)
            return $('#message').html('<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Ein unbekannter Fehler ist aufgetreten!</p></div>');
        
        const result = await response.json();
        $('#message').html(result.message);
    });
});