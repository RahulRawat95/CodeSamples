var path = require('path');
const self = this;
self.config = require(path.resolve(__dirname, 'config.js'));

var nodemailer = require('nodemailer');

const mailConfig = self.config.email;

const transporter = nodemailer.createTransport({
    name: mailConfig.hostEmail,
    host: mailConfig.hostEmail,
    port: mailConfig.portEmail,
    secure: mailConfig.secureEmail,
    auth: {
        user: mailConfig.userEmail,
        pass: mailConfig.passwordEmail
    },
    pool: mailConfig.poolEmail,
    tls: {
        rejectUnauthorized: mailConfig.rejectUnauthorizedEmail
    }
});

self.sendMail = function (to, subject, text) {
    var mailOptions = {
        from: mailConfig.fromEmail,
        to: to,
        subject: subject,
        text: text
    };
    return new Promise((resolve, reject) => {
		transporter.sendMail(mailOptions, function (error, info) {
            if (error) {
                console.log(error);
                reject(error);
            } else {
                resolve(info);
            }
        });
	});
}