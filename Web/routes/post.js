const express = require('express'), router = express.Router(), nodemailer = require('nodemailer'), players = require('../models/players');

router.post('/sendMail', async (req, res) => {
    // Gruppe
    if (!req.body.group) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Wähle eine Gruppe!</p></div>' });
    else if (!(req.body.group === "builder" || req.body.group === "supporter")) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Nope! 🤪</p></div>' });

    // Benutzername
    if (!req.body.username) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Gib einen Benutzernamen ein!</p></div>' });

    const player = await players.findOne({ username: req.body.username });
    if (player === null) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Du besitzt noch keine Daten in unserer Datenbank!</p></div>' });

    // Sperre
    if (player.banned) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Dieser Account kann keine Bewerbungen verschicken!</p></div>' });

    // Sicherheitscode
    if (!req.body.securitycode) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Gib dein Sicherheitscode ein!</p></div>' });
    else if (player.securitycode === "null") return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Dein Sicherheitscode wurde noch nicht gesetzt!</p></div>' });
    else if (req.body.securitycode !== player.securitycode) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Dein Sicherheitscode ist falsch!</p></div>' });

    // E-Mail-Adresse
    if (!req.body.email) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Gib deine E-Mail-Adresse ein!</p></div>' });
    else if (!req.body.email.includes('@')) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Gib eine gültige E-Mail-Adresse ein!</p></div>' });
    
    // Text
    if (!req.body.text) return res.json({ message: '<div class="alert alert-danger"><h4 class="alert-heading">Hoppla!</h4><p class="m-0">Schreib eine Bewerbung!</p></div>' });

    // E-Mail an Spieler
    nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: 'network.minicraft@gmail.com',
            pass: 'newpassword982'
        }
    }).sendMail({
        from: 'network.minicraft@gmail.com',
        to: `${req.body.email}`,
        subject: '[Bewerbung] MiniCraft - Bestätigung',
        html: `<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Document</title>
        </head>
        <style>
            * {margin: 0; padding: 0; font-family: sans-serif;}
        </style>
        <body style="text-align: center;">
            <header style="border-bottom: 5px solid #5dbd50; width: 100%; height: 100px; background-color: #65d056; vertical-align: middle; line-height: 100px; color: #fff;">
                <h1>MiniCraft</h1>
            </header>
            <p style="margin: 1rem;">
                Vielen herzlichen Dank für deine Bewerbung!
                <br>
                <br>
                Wir werden dich Kontaktieren, sobald deine Bewerbung bearbeitet wurde!
            </p>
            <h3 style="margin: 1rem;">
                Kopie deiner Bewerbung:
            </h3>
            <p>
                ${req.body.text}  
            </p>
        </body>
        </html>`
    }, (error, info) => {
        if (error) console.log(error);
        else console.log('[0] Neue E-Mail gesendet!');
    });

    // E-Mail an uns
    nodemailer.createTransport({
        service: 'gmail',
        auth: {
            user: 'network.minicraft@gmail.com',
            pass: 'newpassword982'
        }
    }).sendMail({
        from: 'network.minicraft@gmail.com',
        to: `network.minicraft@gmail.com`,
        subject: `[${req.body.username}][${req.body.group}] Bewerbung`,
        html: `<!DOCTYPE html>
        <html lang="en">
        <head>
            <meta charset="UTF-8">
            <meta http-equiv="X-UA-Compatible" content="IE=edge">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Document</title>
        </head>
        <body>
            <p>
                Name: ${req.body.username}
            </p>
            <p>
                E-Mail: ${req.body.email}
            </p>
            <p>
                Bewerbung:
            </p>
            <p>
                ${req.body.text}
            </p>
        </body>
        </html>`
    }, (error, info) => {
        if (error) console.log(error);
        else console.log('[1] Neue E-Mail gesendet!');
    });

    res.json({ message: '<div class="alert alert-success"><h4 class="alert-heading">Klasse!</h4><p class="m-0">Deine Bewerbung wurde abgeschickt!</p></div>' });
});

module.exports = router;