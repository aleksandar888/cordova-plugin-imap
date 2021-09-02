#import <Cordova/CDVPlugin.h>

@interface Imap : CDVPlugin

- (void)connect:(CDVInvokedUrlCommand *)command;

- (void)disconnect:(CDVInvokedUrlCommand *)command;

- (void)isConnected:(CDVInvokedUrlCommand *)command;

- (void)listMailFolders:(CDVInvokedUrlCommand *)command;

- (void)getMessageCountByFolderName:(CDVInvokedUrlCommand *)command;

- (void)searchMessagesByDatePeriod:(CDVInvokedUrlCommand *)command;

- (void)listMessagesHeadersByConsecutiveNumber:(CDVInvokedUrlCommand *)command;

- (void)listMessagesHeadersByDate:(CDVInvokedUrlCommand *)command;

- (void)getFullMessageData:(CDVInvokedUrlCommand *)command;

- (void)getFullMessageDataOnNewSession:(CDVInvokedUrlCommand *)command;

- (void)copyToFolder:(CDVInvokedUrlCommand *)command;

- (void)setFlag:(CDVInvokedUrlCommand *)command;

@end
