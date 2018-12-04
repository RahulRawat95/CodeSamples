var jwt = require('jsonwebtoken');

var oneTimeToken = require('jsonwebtoken');

var path = require('path');

var config = require(path.resolve(__dirname, 'config.js'));

var app = require('express')();

var expireTime = 1800;

const server = app.listen(config.server.port, config.server.address, function () {
	var address = server.address().address + ':' + server.address().port;//server address
	console.log('Connected to ' + address);
});

const bodyParser = require("body-parser");

const fileUpload = require('express-fileupload');//npm install express-fileupload

// DEFAULT OPTIONS
app.use(fileUpload());

/** bodyParser.urlencoded(options)
 * Parses the text as URL encoded data (which is how browsers tend to send form data from regular forms set to POST)
 * and exposes the resulting object (containing the keys and values) on req.body
 */
app.use(bodyParser.urlencoded({
	extended: true
}));

/**bodyParser.json(options)
 * Parses the text as JSON and exposes the resulting object on req.body.
 */
app.use(bodyParser.json());

//private key to create jwt token
var privateKey = 'asdioasodiaso23412mdwek23*kl234132uio25bn212po409f8sa-123jkl246h903DBJKf';

//verify token function that verifies and responds to requests based on token's validity
function verifyToken(req, res, next) {
	var token = req.headers['authorization'];
	if (!token)
		return res.status(403).send({ auth: false, message: 'No token provided.' });
	jwt.verify(token, privateKey, function (err, decoded) {
		if (err){
			console.log(err);
			return res.status(500).send({ auth: false, message: 'Failed to authenticate token.' });

		}
		// if everything good, save to request for use in other routes
		req.userId = decoded.id;
		next();
	});
}

function verifyOneTimeToken(req, res, next) {
	var token = req.headers['one-time-token'];
	if (!token)
		return res.status(403).send({ auth: false, message: 'No token provided.' });
	oneTimeToken.verify(token, privateKey, function (err, decoded) {
		if (err)
			return res.status(500).send({ auth: false, message: 'Failed to authenticate token.' });
		// if everything good, save to request for use in other routes
		req.userId = decoded.id;
		next();
	});
}

const router = require(path.join(__dirname, 'router'));

app.use('/router/', verifyToken, router);

//after verification of otp call this api to get token and database name
app.get('/getTokenAndDb', function (req, res) {
	var id = req.query.id;
	var verification_token = jwt.sign({ email: id }, privateKey, { expiresIn: expireTime });
	res.json({ db_name: 'w2aOxySalesDB', token: verification_token, expiresIn: expireTime });
});

//after verification of otp call this api to get token and database name
app.get('/getOneTimeToken', function (req, res) {
	var verification_token = oneTimeToken.sign({ email: Math.random() }, privateKey, { expiresIn: expireTime });
	res.json({ db_name: 'w2aOxySalesDB', token: verification_token, expiresIn: 100 });
});

app.get("/getFileByPath", function (req, res) {
    var path = req.query.PATH;
    var parentDir = path.substr(0, path.lastIndexOf('/'));
    parentDir = parentDir.substr(parentDir.lastIndexOf('/') + 1);
    if (parentDir == 'image')
        res.download(decodeURI(req.query.PATH));
    else
        res.status(500).json({ msg: "error" });
});