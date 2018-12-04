//db.js
var path = require('path');
const sql = require('mysql');
const self = this;
self.config=require(path.resolve(__dirname, 'config.js'));
var dbConfig=self.config.database;

self.execSql = function (database, dbSql, userInput = []) {
	return new Promise((resolve, reject) => {
		var data = executeData(database, dbSql, userInput, function (data) {
			if (data.error == "") {
				resolve(JSON.parse(JSON.stringify(data.response)));
			} else {
				reject(data.error);
			}
		});
	});
}
async function executeData(databasename, dbSql, userInput = [], callback) {
	dbConfig.database = databasename;
	const pool = sql.createPool(dbConfig);
	pool.getConnection((err,connection)=>{
		if(err)	{
			callback({"response":"none","error":err});
			return;
		}
		connection.beginTransaction((err)=>{
			if(err)	{
				callback({"response":"none","error":err});
				connection.release();
				return;
			}
			try{
				var arg=userInput.length==0?[true]:userInput;
				connection.query(dbSql, arg, (error, results) => {
					if (error) {
						return connection.rollback(function() {
							callback({"response":"none","error":error});
							connection.release();
						  });
					}
					connection.commit(function(err) {
						if (err) {
						  return connection.rollback(function() {
								callback({"response":"none","error":err});
								connection.release();
						  });
						}
						callback({"response":results,"error":""});
						connection.release();
					  });
					})
			}catch(err){
				return connection.rollback(function() {
					callback({"response":"none","error":err});
					connection.release();
				  });
			}
		});
	});
};

self.execSqlWithOutput = function (database, dbSql, userInput = {}) {
	return new Promise((resolve, reject) => {
		var data = executeDataWithOutPut(database, dbSql, userInput, function (data) {

			if (data.error == "") {
				console.log(data.response);
				resolve(JSON.parse(JSON.stringify(data.response[1][0])));
			} else {
				reject(data.error);
			}
		});
	});
}

async function executeDataWithOutPut(databasename, dbSql, userInput = [], callback) {
	var sqlString=[];
	var j;
	var nextJ;
	for(var i=0;i<dbSql.length;i++){
		j=dbSql.indexOf('@',i);
		if(j<0)
			break;
		nextJ=dbSql.indexOf(',',j);
		if(nextJ==-1)
			nextJ=dbSql.indexOf(')',j);
		sqlString.push(dbSql.substring(j+1,nextJ));
		i=j;
	}
	var string=dbSql+" select ";
	for(var i=0;i<sqlString.length;i++){
		string+="@"+sqlString[i]+" "+sqlString[i];
		if(i!=sqlString.length-1){
			string+=",";
		}
	}
	dbSql=string+";";
	dbConfig.database = databasename;
	const pool = sql.createPool(dbConfig);
	pool.getConnection((err,connection)=>{
		if(err)	{
			callback({"response":"none","error":err});
			return;
		}
		connection.beginTransaction((err)=>{
			if(err)	{
				callback({"response":"none","error":err});
				connection.release();
				return;
			}
			try{
				var arg=userInput.length==0?[true]:userInput;
				connection.query(dbSql, arg, (error, results) => {
					if (error) {
						return connection.rollback(function() {
							callback({"response":"none","error":error});
							connection.release();
						  });
					}
					connection.commit(function(err) {
						if (err) {
						  return connection.rollback(function() {
								callback({"response":"none","error":err});
								connection.release();
						  });
						}
						callback({"response":results,"error":""});
						connection.release();
					  });
					})
			}catch(err){
				return connection.rollback(function() {
					callback({"response":"none","error":err});
					connection.release();
				  });
			}
		});
	});
};

module.export = self;