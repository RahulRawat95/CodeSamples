const fs = require('fs');

const self = this;

const high = 10000;
const low = 1000;

writeFile = function (dir, key, filename, bufferData) {
    return write(dir, key, filename, bufferData);
}

writeMultipleFile = function (dir, key = [], fileName = [], bufferData = []) {
    return recurseWrite(0, dir, key, fileName, bufferData, []);
}

self.writeJson = function writeJson(dir, files, data, deleteFileNames) {
    return new Promise((resolve, reject) => {
        if (deleteFileNames == null || deleteFileNames.length == 0)
            updateJsonObject(dir, files, data, function (err, data) {
                if (err)
                    reject(err);
                else
                    resolve(data);
            });
        else
            deleteFiles(dir, 0, deleteFileNames)
                .then(result => {
                    updateJsonObject(dir, files, data, function (err, data) {
                        if (err)
                            reject(err);
                        else
                            resolve(data);
                    });
                })
                .catch(err => {
                    reject(err);
                });
    });
}

self.deleteFilesInNode = function(dir, fileNames = []){
    return new Promise((resolve, reject) => {
        deleteFiles(dir, 0, fileNames)
                .then(result => {
                    resolve(result);
                })
                .catch(err => {
                    reject(err);
                });
    })
}

function updateJsonObject(dir, files, data, callback) {
    if (files == null) {
        callback(null, data);
        return;
    }
    var values = [];
    var fileNames = [];
    var keys = Object.keys(files);
    var pushKeys = [];
    var obj;
    for (var i = 0; i < keys.length; i++) {
        obj = files[keys[i]];
        if (obj.length == null) {
            values.push(obj.data);
            fileNames.push(obj.name);
            pushKeys.push(keys[i]);
        } else {
            for (var j = 0; j < obj.length; j++) {
                values.push(obj[j].data);
                fileNames.push(obj[j].name);
                pushKeys.push(keys[i]);
            }
        }
    }
    writeMultipleFile(dir, pushKeys, fileNames, values)
        .then(result => {
            for (var j = 0; j < result.length; j++) {
                var keys = Object.keys(result[j]);
                var string;
                for (var i = 0; i < keys.length; i++) {
                    if (data[keys[i]] == null)
                        data[keys[i]] = result[j][keys[i]];
                    else {
                        string = result[j][keys[i]];
                        string = string.substr(string.lastIndexOf("/") + 1);
                        data[keys[i]] = data[keys[i]] + "#" + string;
                    }
                }
            }
            callback(null, data);
        }).catch(err => {
            callback(err, null);
        });
}

function deleteFiles(dir, index, fileNames = []) {
    return new Promise((resolve, reject) => {
        if (index == fileNames.length)
            resolve({ result: "success" });
        else
            fs.unlink(fileNames[index], (err) => {
                if (err) reject(err);
                deleteFiles(dir, index + 1, fileNames)
                    .then(result => {
                        resolve(result);
                    })
                    .catch((err) => {
                        reject(err);
                    })
            });
    });
}

function recurseWrite(index, dir, key = [], fileName = [], bufferData = [], createdFileNames = []) {
    return new Promise((resolve, reject) => {
        var size = fileName.length;
        if (size == index) {
            resolve(createdFileNames);
        } else {
            write(dir, key[index], fileName[index], bufferData[index])
                .then(result => {
                    createdFileNames.push(result);
                    recurseWrite(index + 1, dir, key, fileName, bufferData, createdFileNames)
                        .then(result => {
                            resolve(createdFileNames);
                        }).catch(err => {
                            reject(err);
                        });
                }).catch(err => {
                    reject(err);
                });
        }
    })
}

function write(dir, key, filename, bufferData) {
    return new Promise((resolve, reject) => {
        fs.mkdir(dir, function (err) {
            var date = new Date().getTime();
            var random = (Math.floor(Math.random() * (high - low) + low));
            var offset = date + "" + random;
            fs.writeFile(dir + "/" + offset + filename, bufferData, function (err) {
                if (err) {
                    reject(err);
                } else {
                    var returnVal = "{ \"" + key + "\" : \"" + __dirname + "/" + dir + "/" + offset + filename + "\"}";
                    resolve(JSON.parse(returnVal));
                }
            });
        });
    });
}

module.export = self;