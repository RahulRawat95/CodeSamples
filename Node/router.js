const express = require('express');

var path = require('path');

const router = express.Router();
var bodyParser = require('body-parser');

const fileWriter = require(path.resolve(__dirname, 'fileSystem.js'));

var dbSql = require(path.resolve(__dirname, 'mysqldb.js'));

var mail = require(path.resolve(__dirname, 'mail.js'));

// parse application/x-www-form-urlencoded
router.use(bodyParser.urlencoded({ extended: false }));

// parse application/json
router.use(bodyParser.json());

router.post("/insertProduct", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_PRODUCT_I(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/updateProduct", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_PRODUCT_U(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/deactivateProduct", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_PRODUCT_D(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getAllUnits", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `SELECT * FROM TBL_R_UNIT;`;
    dbSql.execSql(dbname, sql, [])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getProductsPaginate", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_PRODUCT_S(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getProductCount", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `select count(*) as count from TBL_M_PRODUCT where ACTIVE_STATUS = 1`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/deactivateArea", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_D(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getAreasPaginate", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_S(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getAreaCount", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `select count(*) as count from TBL_M_AREA where ACTIVE_STATUS = 1`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/insertArea", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_I(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/updateArea", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_U(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getAreaLocalityPaginate", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_LOCALITY(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});
//make all other api's like this in a way that verifyToken comes after apiname so that the token is automatically checked
//app.get('/apiName',verifyToken,function(req,res){})

router.post("/changeUserPassword", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    var EMAIL = data.JsonData.EMAIL_VC;
    let sql = `CALL USP_CHANGE_PASSWORD(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json({ message: "Password changed successfully" });
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getAreaAutoComplete", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_AREA_AC(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getLocalityAutoComplete", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_LOCALITY_AC(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getStockInHand", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_STOCK_IN_HAND(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0][0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getStockSheetPaginate", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_T_STOCK_SHEET_S(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json({ msg: 'success' });
        }).catch(err => {
            res.status(500).json({ msg: "error" + err });
        });
});

router.post("/insertStockSheet", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_T_STOCK_SHEET_INSERT(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json({ msg: 'success' });
        }).catch(err => {
            res.status(500).json({ msg: "error" + err });
        });
});

router.post("/updateStockSheet", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_T_STOCK_SHEET_UPDATE(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getProductAutoComplete", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `CALL USP_TBL_M_PRODUCT_AC(?)`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [JSON.stringify(data.JsonData)])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

router.post("/getCompanyType", function (req, res) {
    var data = JSON.parse(req.body.data);
    dbname = data.dbName;
    let sql = `select * from TBL_R_COMPANY_TYPE`; //Calling procedure in mysql with parameters
    dbSql.execSql(dbname, sql, [])
        .then(result => {
            res.json(result[0]);
        }).catch(err => {
            res.status(500).json({ response: "error" + err });
        });
});

module.exports = router;