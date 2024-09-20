var exec = require('cordova/exec');

exports.connect = function (config, success, error) {
    exec(success, error, 'Imap', 'connect', [config]);
};

exports.disconnect = function (success, error) {
    exec(success, error, 'Imap', 'disconnect', []);
};

exports.isConnected = function (success, error) {
    exec(success, error, 'Imap', 'isConnected', []);
};

exports.listMailFolders = function (pattern, success, error) {
    exec(success, error, 'Imap', 'listMailFolders', [pattern]);
};

exports.getMessageCountByFolderName = function (folderName, success, error) {
    exec(success, error, 'Imap', 'getMessageCountByFolderName', [folderName]);
};

exports.searchMessagesByDatePeriod = function (folderName, dateInMilliseconds,comparison, success, error) {
    exec(success, error, 'Imap', 'searchMessagesByDatePeriod', [folderName, dateInMilliseconds, comparison]);
};

exports.listMessagesHeadersByConsecutiveNumber = function (folderName, start, end, success, error) {
    exec(success, error, 'Imap', 'listMessagesHeadersByConsecutiveNumber', [folderName, start, end]);
};

exports.listMessagesHeadersByDate = function (folderName, dateInMilliseconds,comparison, success, error) {
    exec(success, error, 'Imap', 'listMessagesHeadersByDate', [folderName, dateInMilliseconds, comparison]);
};

exports.getFullMessageData = function (folderName, messageNumber, success, error) {
    exec(success, error, 'Imap', 'getFullMessageData', [folderName, messageNumber]);
};

exports.getFullMessageDataOnNewSession = function (config, folderName, messageNumber, success, error) {
    exec(success, error, 'Imap', 'getFullMessageDataOnNewSession', [config, folderName, messageNumber]);
};

exports.copyToFolder = function (sourceFolder, destinationFolder, messageNums, success, error) {
    exec(success, error, 'Imap', 'copyToFolder', [sourceFolder, destinationFolder, messageNums]);
};

exports.setFlag = function (folderName, messageNums, flag, status, success, error) {
    exec(success, error, 'Imap', 'setFlag', [folderName, messageNums, flag, status]);
};

exports.downloadEmailAttachment = function (folderName, messageNo, path, contentID, attachmentFileName, success, error) {
    exec(success, error, 'Imap', 'downloadEmailAttachment', [folderName, messageNo, path, contentID, attachmentFileName]);
};