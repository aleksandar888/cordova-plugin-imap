#import "Imap.h"
#import <Cordova/CDVPlugin.h>
#import <MailCore/MailCore.h>

@implementation Imap

MCOIMAPSession *session;

- (void)connect:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{

        __block CDVPluginResult *pluginResult = nil;

        @try {
            NSObject *clientData = [command.arguments objectAtIndex:0];
            __block NSDictionary *resultData = nil;

            if (clientData != nil) {
                session = [Imap createImapSession:clientData];

                MCOIMAPOperation *accountOperation = [session checkAccountOperation];
                [accountOperation start:^(NSError *error) {

                    if (error == nil) {
                        resultData = @{
                                @"status": @((BOOL) true),
                                @"connection": @"connected"
                        };
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
                    } else {
                        session = nil;

                        resultData = @{
                                @"status": @((BOOL) false),
                                @"exception": [error localizedDescription]
                        };
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:resultData];
                    }

                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            } else {
                resultData = @{
                        @"status": @((BOOL) false),
                        @"exception": @"Invalid input data"
                };
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsDictionary:resultData];
                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)disconnect:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            MCOIMAPOperation *operation = [session disconnectOperation];

            if (operation != nil) {
                [operation start:^(NSError *error) {

                    if (error == nil) {
                        session = nil;
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];
                    } else {
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:[error localizedDescription]];
                    }

                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];

                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

+ (void)checkSessionStatus:(MCOIMAPSession *)session {
    NSException *exception = [NSException exceptionWithName:@"ImapSessionNotCreatedException"
                                                     reason:@"Not connected"
                                                   userInfo:nil];

    if (session == nil) @throw exception;
}

- (void)isConnected:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            MCOConnectionType connectionLog = [session connectionType];

            if (connectionLog != nil) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];
            } else {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:false];
            }

            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)listMailFolders:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;
        NSString *pattern = [command.arguments objectAtIndex:0];

        @try {
            [Imap checkSessionStatus:session];

            MCOIMAPFetchFoldersOperation *foldersOperation = [session fetchAllFoldersOperation];
            [foldersOperation start:^(NSError *error, NSArray *folders) {

                if (error == nil) {

                    NSMutableArray *stringArray = [[NSMutableArray alloc] init];

                    for (MCOIMAPFolder *folder in folders) {

                        if ([pattern isEqual:@"*"]) {
                            [stringArray addObject:(folder.path)];
                        } else {
                            NSRegularExpression *regex = [NSRegularExpression regularExpressionWithPattern:pattern
                                                                                                   options:NSRegularExpressionCaseInsensitive
                                                                                                     error:&error];
                            NSUInteger numberOfMatches = [regex numberOfMatchesInString:folder.path
                                                                                options:0
                                                                                  range:NSMakeRange(0, [folder.path length])];
                            if (numberOfMatches > 0) {
                                [stringArray addObject:(folder.path)];
                            }
                        }
                    }

                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:stringArray];
                } else {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR];
                }

                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];

        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)getMessageCountByFolderName:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];

            MCOIMAPFolderInfoOperation *folderInfoOperation = [session folderInfoOperation:folderName];
            [folderInfoOperation start:^(NSError *error, MCOIMAPFolderInfo *info) {
                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsInt:[info messageCount]];

                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)searchMessagesByDatePeriod:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];
            NSNumber *dateInMilliseconds = [command.arguments objectAtIndex:1];
            NSString *comparison = [command.arguments objectAtIndex:2];

            NSDate *date = [NSDate dateWithTimeIntervalSince1970:(dateInMilliseconds.doubleValue / 1000.0)];

            MCOIMAPSearchExpression *expr = [Imap searchExpressionOptions:comparison :date];

            MCOIMAPSearchOperation *searchOperation = [session searchExpressionOperationWithFolder:folderName
                                                                                        expression:expr];
            [searchOperation start:^(NSError *__nullable error, MCOIndexSet *searchResult) {
                if (error == nil) {

                    NSIndexSet *messagesIndexSet = [searchResult nsIndexSet];

                    NSMutableArray *resultData = [NSMutableArray array];

                    [messagesIndexSet enumerateIndexesUsingBlock:^(NSUInteger idx, BOOL *stop) {
                        [resultData addObject:[NSNumber numberWithInteger:idx]];
                    }];

                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:resultData];

                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                } else {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                                     messageAsString:[error localizedDescription]];

                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }];

        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

+ (MCOIMAPSearchExpression *)searchExpressionOptions:(NSString *)comparison :(NSDate *)date {
    if ([comparison isEqualToString:@"LT"]) {
        return [MCOIMAPSearchExpression searchBeforeReceivedDate:date];
    } else if ([comparison isEqualToString:@"EQ"]) {
        return [MCOIMAPSearchExpression searchOnReceivedDate:date];
    } else if ([comparison isEqualToString:@"GE"]) {
        return [MCOIMAPSearchExpression searchSinceReceivedDate:date];
    } else if ([comparison isEqualToString:@"NE"]) {
        return [MCOIMAPSearchExpression searchNot:[MCOIMAPSearchExpression searchOnReceivedDate:date]];
    } else {
        @throw [Imap invalidSearchExpression];
    }
}

+ (NSException *)invalidSearchExpression {
    return [NSException exceptionWithName:@"ImapInvalidSearchExpression"
                                   reason:@"The selected search expression is unavailable on iOS"
                                 userInfo:nil];
}

- (void)listMessagesHeadersByConsecutiveNumber:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];
            int startLocation = [[command.arguments objectAtIndex:1] intValue];
            int endLocation = [[command.arguments objectAtIndex:2] intValue];

            MCOIMAPMessagesRequestKind request = (MCOIMAPMessagesRequestKindHeaders | MCOIMAPMessagesRequestKindFlags | MCOIMAPMessagesRequestKindInternalDate);

            int rangeLength = (endLocation - startLocation);
            MCOIndexSet *uids = [MCOIndexSet indexSetWithRange:MCORangeMake(startLocation, rangeLength)];

            MCOIMAPFetchMessagesOperation *fetchOperation = [session fetchMessagesOperationWithFolder:folderName
                                                                                          requestKind:request
                                                                                                 uids:uids];

            [fetchOperation start:^(NSError *error, NSArray *fetchedMessages, MCOIndexSet *vanishedMessages) {

                if (error == nil) {

                    NSMutableArray *resultData = [[NSMutableArray alloc] init];

                    for (MCOIMAPMessage *message in fetchedMessages) {
                        [resultData addObject:([Imap parseMessagesHeaders:message :folderName])];
                    }

                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:resultData];

                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                } else {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                     messageAsString:[error localizedFailureReason]];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

+ (NSMutableArray *)parseAddressHeader:(NSArray *)emailAccounts {
    NSMutableArray *emailAddress = [[NSMutableArray alloc] init];

    @try {
        for (MCOAddress *address in emailAccounts) {
            [emailAddress addObject:[Imap parseAddressObject:address]];
        }

        return emailAddress;
    } @catch (NSException *exception) {
        return emailAddress;
    }
}

+ (NSObject *)parseAddressObject:(MCOAddress *)address {
    return @{
            @"address": (address.mailbox == nil) ? @"" : address.mailbox,
            @"personal": (address.displayName == nil) ? @"" : address.displayName
    };
}

+ (NSString *)convertDateToStringDate:(NSDate *)date {
    @try {
        NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
        [formatter setDateFormat:@"EEE MMM d HH:mm:ss ZZZZ yyyy"];

        return [formatter stringFromDate:date];
    } @catch (NSException *exception) {
        return @"";
    }
}

- (void)listMessagesHeadersByDate:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];
            NSNumber *dateInMilliseconds = [command.arguments objectAtIndex:1];
            NSString *comparison = [command.arguments objectAtIndex:2];

            NSDate *date = [NSDate dateWithTimeIntervalSince1970:(dateInMilliseconds.doubleValue / 1000.0)];
            MCOIMAPSearchExpression *expr = [Imap searchExpressionOptions:comparison :date];

            MCOIMAPSearchOperation *searchOperation = [session searchExpressionOperationWithFolder:folderName
                                                                                        expression:expr];
            [searchOperation start:^(NSError *__nullable error, MCOIndexSet *searchResult) {
                if (error == nil) {

                    MCOIMAPMessagesRequestKind request = (MCOIMAPMessagesRequestKindHeaders | MCOIMAPMessagesRequestKindFlags);

                    MCOIMAPFetchMessagesOperation *fetchMessagesOperation = [session fetchMessagesOperationWithFolder:folderName
                                                                                                          requestKind:request
                                                                                                                 uids:searchResult];

                    [fetchMessagesOperation start:^(NSError *_Nullable error, NSArray *_Nullable messages, MCOIndexSet *_Nullable vanishedMessages) {

                        NSMutableArray *resultData = [[NSMutableArray alloc] init];

                        for (MCOIMAPMessage *message in messages) {
                            [resultData addObject:([Imap parseMessagesHeaders:message :folderName])];
                        }

                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsArray:resultData];

                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
                } else {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                     messageAsString:[error localizedFailureReason]];
                    [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                }
            }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

+ (NSDictionary *)parseMessagesHeaders:(MCOIMAPMessage *)message :(NSString *)folderName {
    return @{
            @"messageNumber": @(message.uid),
            @"folder": folderName,
            @"from": [Imap parseFromHeadder:message.header.from],
            @"toRecipients": [Imap parseAddressHeader:message.header.to],
            @"ccRecipients": [Imap parseAddressHeader:message.header.cc],
            @"bccRecipients": [Imap parseAddressHeader:message.header.bcc],
            @"receivedDate": [Imap convertDateToStringDate:message.header.receivedDate],
            @"subject": message.header.subject == nil ? @"" : message.header.subject,
            @"flags": [Imap parseFlags:message.flags]
    };
}

+ (NSMutableArray *)parseFromHeadder:(MCOAddress *)formHeader {

    NSMutableArray *emailAddress = [[NSMutableArray alloc] init];

    @try {
        [emailAddress addObject:[Imap parseAddressObject:formHeader]];

        return emailAddress;
    } @catch (NSException *exception) {
        return emailAddress;
    }
}

+ (NSArray *)parseFlags:(MCOMessageFlag)flags {

    NSMutableArray *parsedFlags = [[NSMutableArray alloc] init];

    if (flags & MCOMessageFlagNone) {
        [parsedFlags addObject:@"None"];
    }
    if (flags & MCOMessageFlagSeen) {
        [parsedFlags addObject:@"Seen"];
    }
    if (flags & MCOMessageFlagAnswered) {
        [parsedFlags addObject:@"Answered"];
    }
    if (flags & MCOMessageFlagFlagged) {
        [parsedFlags addObject:@"Flagged"];
    }
    if (flags & MCOMessageFlagDeleted) {
        [parsedFlags addObject:@"Deleted"];
    }
    if (flags & MCOMessageFlagDraft) {
        [parsedFlags addObject:@"Draft"];
    }
    if (flags & MCOMessageFlagMDNSent) {
        [parsedFlags addObject:@"Sent"];
    }
    if (flags & MCOMessageFlagForwarded) {
        [parsedFlags addObject:@"Forwarded"];
    }
    if (flags & MCOMessageFlagSubmitPending) {
        [parsedFlags addObject:@"SubmitPending"];
    }
    if (flags & MCOMessageFlagSubmitted) {
        [parsedFlags addObject:@"Submitted"];
    }

    return parsedFlags;
}

- (void)getFullMessageData:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];
            int uid = [[command.arguments objectAtIndex:1] intValue];

            [Imap downloadFullMessageData:session :folderName :uid
                            completeBlock:^(NSDictionary *resultData) {
                                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];

                                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                            } errorBlock:^(NSError *error) {
                        NSString *errorMessage = [Imap convertErrorMessagesToString:error];
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];

                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)getFullMessageDataOnNewSession:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            NSObject *clientData = [command.arguments objectAtIndex:0];

            NSString *folderName = [command.arguments objectAtIndex:1];
            int uid = [[command.arguments objectAtIndex:2] intValue];

            MCOIMAPSession *privateSession = [Imap createImapSession:clientData];

            [Imap downloadFullMessageData:privateSession :folderName :uid
                            completeBlock:^(NSDictionary *resultData) {
                                pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];

                                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                            } errorBlock:^(NSError *error) {
                        NSString *errorMessage = [Imap convertErrorMessagesToString:error];
                        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:errorMessage];

                        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
                    }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

+ (MCOIMAPSession *)createImapSession:(NSObject *)clientData {

    NSString *host = [clientData valueForKey:@"host"];
    NSString *user = [clientData valueForKey:@"user"];
    NSString *password = [clientData valueForKey:@"password"];

    int hostPort = [[clientData valueForKey:@"port"] intValue];
    int port = (hostPort != 0) ? hostPort : 993;

    NSString *connectionType = [clientData valueForKey:@"connectionType"];
    MCOConnectionType imapConnectionType = (connectionType == nil) ? MCOConnectionTypeTLS : [Imap parseConnectionType:connectionType];

    MCOIMAPSession *imapSession = [[MCOIMAPSession alloc] init];
    imapSession.hostname = host;
    imapSession.port = port;
    imapSession.username = user;
    imapSession.password = password;
    imapSession.connectionType = imapConnectionType;

    return imapSession;
}

+ (MCOConnectionType)parseConnectionType:(NSString *)connectionType {
    if ([connectionType isEqual:@"Clear"]) {
        return MCOConnectionTypeClear;
    } else if ([connectionType isEqual:@"StartTLS"]) {
        return MCOConnectionTypeStartTLS;
    } else if ([connectionType isEqual:@"TLS/SSL"]) {
        return MCOConnectionTypeTLS;
    } else {
        @throw [Imap invalidConnectionTypeException];
    }
}

+ (NSException *)invalidConnectionTypeException {
    return [NSException exceptionWithName:@"ImapInvalidConnectionTypeException"
                                   reason:@"The selected connection type does not exist"
                                 userInfo:nil];
}

+ (NSString *)convertErrorMessagesToString:(NSError *)error {
    return [[[error userInfo].allValues valueForKey:@"description"] componentsJoinedByString:@""];;
}

+ (void)downloadFullMessageData:(MCOIMAPSession *)currentSession :(NSString *)folderName :(uint32_t)uid
                  completeBlock:(void (^)(NSDictionary *))completeBlock
                     errorBlock:(void (^)(NSError *))errorBlock {

    [Imap getMessagesHeaders:currentSession :folderName :uid completeBlock:^(MCOIMAPMessage *headers) {

        [Imap getMessageContent:currentSession :folderName :uid completeBlock:^(NSData *contentData) {

            MCOMessageParser *message = [MCOMessageParser messageParserWithData:contentData];
            NSDictionary *resultData = [Imap parseFullMessage:headers :message :folderName];

            completeBlock(resultData);
        }            errorBlock:^(NSError *error) {
            errorBlock(error);
        }];
    }             errorBlock:^(NSError *error) {
        errorBlock(error);
    }];
}

+ (void)getMessagesHeaders:(MCOIMAPSession *)currentSession :(NSString *)folderName :(uint32_t)uid
             completeBlock:(void (^)(MCOIMAPMessage *))completeBlock
                errorBlock:(void (^)(NSError *))errorBlock {

    MCOIMAPMessagesRequestKind request = (MCOIMAPMessagesRequestKindHeaders | MCOIMAPMessagesRequestKindFlags);
    MCOIndexSet *uidSet = [MCOIndexSet indexSetWithRange:MCORangeMake(uid, 0)];

    MCOIMAPFetchMessagesOperation *fetchMessagesHeadersOperation = [currentSession fetchMessagesOperationWithFolder:folderName
                                                                                                        requestKind:request
                                                                                                               uids:uidSet];

    [fetchMessagesHeadersOperation start:^(NSError *_Nullable error, NSArray *_Nullable messages, MCOIndexSet *_Nullable vanishedMessages) {
        if (error == nil) {
            completeBlock(messages.firstObject);
        } else {
            errorBlock(error);
        }
    }];
}

+ (void)getMessageContent:(MCOIMAPSession *)currentSession :(NSString *)folderName :(uint32_t)uid
            completeBlock:(void (^)(NSData *))completeBlock
               errorBlock:(void (^)(NSError *))errorBlock {

    MCOIMAPFetchContentOperation *fetchMessageContentOperation = [currentSession fetchMessageOperationWithFolder:folderName
                                                                                                             uid:uid];

    [fetchMessageContentOperation start:^(NSError *_Nullable error, NSData *_Nullable contentData) {
        if (error == nil) {
            completeBlock(contentData);
        } else {
            errorBlock(error);
        }
    }];
}

+ (NSDictionary *)parseFullMessage:(MCOIMAPMessage *)messageHeaders :(MCOMessageParser *)message :(NSString *)folderName {
    return @{
            @"messageNumber": @(messageHeaders.uid),
            @"folder": folderName,
            @"allRecipients": [Imap getAllRecipients:message],
            @"from": [Imap parseFromHeadder:message.header.from],
            @"toRecipients": [Imap parseAddressHeader:message.header.to],
            @"ccRecipients": [Imap parseAddressHeader:message.header.cc],
            @"bccRecipients": [Imap parseAddressHeader:message.header.bcc],
            @"replyTo": [Imap parseAddressHeader:message.header.replyTo],
            @"receivedDate": [Imap convertDateToStringDate:message.header.receivedDate],
            @"subject": message.header.subject == nil ? @"" : message.header.subject,
            @"sentDate": [Imap convertDateToStringDate:message.header.date],
            @"fileName": message.mainPart.filename == nil ? @"" : message.mainPart.filename,
            @"flags": [Imap parseFlags:messageHeaders.flags],
            @"bodyContent": [Imap parseMessageBodyContent:message],
            @"size": @(message.data.length)
    };
}

+ (NSArray *)getAllRecipients:(MCOMessageParser *)message {

    NSMutableSet *allRecipientsResult = [NSMutableSet set];

    NSArray *toRecipients = [Imap parseAddressHeader:message.header.to];
    NSArray *ccRecipients = [Imap parseAddressHeader:message.header.cc];
    NSArray *bccRecipients = [Imap parseAddressHeader:message.header.bcc];

    [allRecipientsResult addObjectsFromArray:toRecipients];
    [allRecipientsResult addObjectsFromArray:ccRecipients];
    [allRecipientsResult addObjectsFromArray:bccRecipients];

    return [allRecipientsResult allObjects];
}

+ (NSMutableArray *)parseMessageBodyContent:(MCOMessageParser *)message {

    NSMutableArray *fullContent = [NSMutableArray array];

    @try {
        [fullContent addObject:(@{
                @"type": @"text/html",
                @"content": message.htmlBodyRendering
        })];

        [fullContent addObject:(@{
                @"type": @"text/plain",
                @"content": message.plainTextBodyRendering
        })];

        for (MCOAttachment *attachmentPart in message.attachments) {

            if([@"text/calendar" isEqualToString:attachmentPart.mimeType] && attachmentPart.data != nil) {
                [fullContent addObject:(@{
                        @"type": attachmentPart.mimeType,
                        @"content": [NSString stringWithUTF8String: attachmentPart.data.bytes]
                })];
            } else {
                [fullContent addObject:(@{
                        @"type": attachmentPart.mimeType,
                        @"fileName": attachmentPart.filename
                })];
            }
        }

        for (MCOAttachment *htmlAttachment in message.htmlInlineAttachments) {
            [fullContent addObject:(@{
                    @"type": htmlAttachment.mimeType,
                    @"fileName": htmlAttachment.filename
            })];
        }

        return fullContent;
    } @catch (NSException *exception) {
        return fullContent;
    }
}

- (void)copyToFolder:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *sourceFolder = [command.arguments objectAtIndex:0];
            NSString *destinationFolder = [command.arguments objectAtIndex:1];
            NSArray *messageIndexes = [command.arguments objectAtIndex:2];

            MCOIndexSet *uids = [Imap convertIndexArrayToMCOIndexSet:messageIndexes];

            MCOIMAPCopyMessagesOperation *copyMessagesOperation = [session copyMessagesOperationWithFolder:sourceFolder
                                                                                                      uids:uids
                                                                                                destFolder:destinationFolder];

            [copyMessagesOperation start:^(NSError *error, NSDictionary *uidMapping) {
                if (error == nil) {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:true];
                } else {
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                                     messageAsString:[error localizedFailureReason]];
                }

                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)setFlag:(CDVInvokedUrlCommand *)command {
    [self.commandDelegate runInBackground:^{
        __block CDVPluginResult *pluginResult = nil;

        @try {
            [Imap checkSessionStatus:session];

            NSString *folderName = [command.arguments objectAtIndex:0];
            NSArray *messageIndexes = [command.arguments objectAtIndex:1];
            NSString *flag = [command.arguments objectAtIndex:2];
            bool status = [[command.arguments objectAtIndex:3] boolValue];

            MCOIMAPStoreFlagsRequestKind flagsRequestKing = status ? MCOIMAPStoreFlagsRequestKindSet : MCOIMAPStoreFlagsRequestKindRemove;
            MCOIndexSet *uids = [Imap convertIndexArrayToMCOIndexSet:messageIndexes];
            MCOMessageFlag selectedFlag = [Imap parseInputFlag:flag];

            MCOIMAPOperation *operation = [session storeFlagsOperationWithFolder:folderName
                                                                            uids:uids
                                                                            kind:flagsRequestKing
                                                                           flags:selectedFlag];

            [operation start:^(NSError *error) {
                __block NSDictionary *resultData = nil;

                if (error == nil) {

                    BOOL deletedFlag = selectedFlag & MCOMessageFlagDeleted;

                    if (deletedFlag) {
                        [Imap expungeFolderWhenSettingDeletedFlag:folderName];
                    }

                    resultData = @{
                            @"status": @((BOOL) true),
                            @"modified": messageIndexes
                    };
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];

                } else {
                    resultData = @{
                            @"status": @((BOOL) false),
                            @"modified": messageIndexes
                    };
                    pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsDictionary:resultData];
                }

                [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
            }];
        } @catch (NSException *exception) {
            pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                             messageAsString:[exception reason]];
            [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        }
    }];
}

- (void)downloadEmailAttachment:(CDVInvokedUrlCommand *)command {
  [self.commandDelegate runInBackground:^{
    __block CDVPluginResult *pluginResult = nil;

      @try {
          [Imap checkSessionStatus:session];

          NSString *folderName = [command.arguments objectAtIndex:0];
          int uid = [[command.arguments objectAtIndex:1] intValue];
          NSString *path = [command.arguments objectAtIndex:2];
          __block NSString *fileName = [command.arguments objectAtIndex:3];

          __block BOOL result = false;

          if(fileName == nil || [fileName isEqual:[NSNull null]] || [fileName length] == 0) {
             @throw [NSException exceptionWithName:@"Invalid Parameter" reason:@"Failed: Please provide a 'fileName'" userInfo:nil];
          }

          [Imap getMessageContent:session :folderName :uid completeBlock:^(NSData *contentData) {

              MCOMessageParser *message = [MCOMessageParser messageParserWithData:contentData];

              NSData *attachmentData = nil;

              for (MCOAttachment *attachment in message.attachments) {

                  if([fileName isEqualToString:attachment.filename]) {
                      fileName = attachment.filename;
                      attachmentData = attachment.data;
                      break;
                  }
              }

              if(attachmentData == nil) {
                  for (MCOAttachment *htmlAttachment in message.htmlInlineAttachments) {

                      if([fileName isEqualToString:htmlAttachment.filename]) {
                          fileName = htmlAttachment.filename;
                          attachmentData = htmlAttachment.data;
                          break;
                      }
                  }
              }

              if(attachmentData != nil) {
                  [Imap saveFileAttachment:attachmentData :fileName :path];
                  result = true;
              }

              pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsBool:result];
              [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
          }
          errorBlock:^(NSError *error) {

              pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR
                                               messageAsString:[error localizedDescription]];
              [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
          }];
      } @catch (NSException *exception) {
          pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_JSON_EXCEPTION
                                           messageAsString:[exception reason]];
          [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
    }
  }];
}

+ (void) saveFileAttachment:(NSData *) attachmentData :(NSString *) fileName :(NSString *) path  {
    @try{
        NSString *attachmentPath = [path stringByAppendingPathComponent:fileName];
        BOOL fileExists = [[NSFileManager defaultManager] fileExistsAtPath:attachmentPath];

        if (fileExists) {

            int index = 1;

            NSString *fileNameWithoutExtension = [fileName stringByDeletingPathExtension];
            NSString *fileExtension = [fileName pathExtension];

            while(fileExists) {
                attachmentPath = [path stringByAppendingPathComponent:[NSString stringWithFormat:@"%@ (%d).%@", fileNameWithoutExtension, index, fileExtension]];
                fileExists = [[NSFileManager defaultManager] fileExistsAtPath:attachmentPath];
                index++;
            }
        }

        [attachmentData writeToFile:attachmentPath atomically:YES];
    } @catch (NSException *exception) {
        @throw [exception reason];
    }
}

+ (BOOL)expungeFolderWhenSettingDeletedFlag:(NSString *)folderName {
    @try {
        __block BOOL result = false;

        MCOIMAPOperation *expungeOperation = [session expungeOperation:folderName];

        [expungeOperation start:^(NSError *error) {
            if (error == nil) {
                result = true;
            }
        }];

        return result;
    } @catch (NSException *exception) {
        return false;
    }
}

+ (MCOIndexSet *)convertIndexArrayToMCOIndexSet:(NSArray *)indexArray {

    MCOIndexSet *indexes = [[MCOIndexSet alloc] init];

    for (id index in indexArray) {
        [indexes addIndex:([index intValue])];
    }

    return indexes;
}

+ (MCOMessageFlag)parseInputFlag:(NSString *)flag {
    if ([flag isEqual:@"ANSWERED"]) {
        return MCOMessageFlagAnswered;
    } else if ([flag isEqual:@"DRAFT"]) {
        return MCOMessageFlagDraft;
    } else if ([flag isEqual:@"FLAGGED"]) {
        return MCOMessageFlagFlagged;;
    } else if ([flag isEqual:@"SEEN"]) {
        return MCOMessageFlagSeen;;
    } else if ([flag isEqual:@"DELETED"]) {
        return MCOMessageFlagDeleted;
    } else if ([flag isEqual:@"Sent"]) {
        return MCOMessageFlagMDNSent;
    } else if ([flag isEqual:@"Forwarded"]) {
        return MCOMessageFlagForwarded;
    } else if ([flag isEqual:@"SubmitPending"]) {
        return MCOMessageFlagSubmitPending;
    } else if ([flag isEqual:@"Submitted"]) {
        return MCOMessageFlagSubmitted;
    } else {
        @throw [Imap invalidFlagException];
    }
}

+ (NSException *)invalidFlagException {
    return [NSException exceptionWithName:@"ImapInvalidFlagException"
                                   reason:@"The selected flag is unavailable on iOS"
                                 userInfo:nil];
}

@end
