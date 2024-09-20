# Cordova IMAP plugin

This plugin will enable a Cordova application to use the IMAP (Internet Message Access Protocol) features. <br/>
The plugin offers support for Android and iOS. <br/>
To enable the IMAP features on Android, this plugin uses the
framework [Java Mail API](https://javaee.github.io/javamail/)
and for iOS, it uses the [MailCore 2](http://libmailcore.com/) library. <br/>

## Supported Platforms

- Android
- iOS

## Installation

Install the plugin by running:

```bash
cordova plugin add cordova-plugin-imap
```

## How to install on Ionic

More info for installation on Ionic including `import` examples
on: [Awesome Cordova Plugins](https://github.com/danielsogl/awesome-cordova-plugins#readme)

### Cordova

```bash
# NOTE: Install Core library (once per project)
npm install @awesome-cordova-plugins/core

# Install the IMAP plugin
ionic cordova plugin add cordova-plugin-imap

# Install the Awesome Cordova Plugins TypeScript wrapper
npm install @awesome-cordova-plugins/imap
```

### Capacitor

```bash
# NOTE: Install Core library (once per project)
npm install @awesome-cordova-plugins/core

# Install the IMAP plugin
npm install cordova-plugin-imap

# Install the Awesome Cordova Plugins TypeScript wrapper
npm install @awesome-cordova-plugins/imap

# Update native platform project(s) to include newly added plugin
ionic cap sync
``` 

## API

- connect([config](#config), success, error) - ([Connection](#connection)) - Tries to connect and authenticate with the
  IMAP server.
- disconnect(success, error) - (boolean) - Closes the connection with the server.
- isConnected(success, error) - (boolean) - Checks the current state of the connection.
- listMailFolders(pattern, success, error) - (string[]) - Lists the name of all the mail folders in the mailbox.
- getMessageCountByFolderName(folderName, success, error) - (number) - Gets the count of the messages in the folder.
- searchMessagesByDatePeriod(folderName, dateInMilliseconds, [comparison](#comparisonterm) : <code>enum</code>, success,
  error) - (number[]) - Returns the messages'
  consecutive numbers.
- listMessagesHeadersByConsecutiveNumber(folderName, start, end, success,
  error) - ([MessageHeaders](#messageHeaders)[]) - Returns messages' headers data.
- listMessagesHeadersByDate(folderName, dateInMilliseconds, [comparison](#ComparisonTerm) : <code>enum</code>, success,
  error) - ([MessageHeaders](#messageHeaders)[]) - Returns messages' headers data.
- getFullMessageData(folderName, messageNumber, success, error) - ([Message](#message)) - Returns the full message data
  inclucing its attachments.
- getFullMessageDataOnNewSession([config](#config), folderName, messageNumber, success, error) - ([Message](#message)) -
  Returns the full message data inclucing its attachments.
- copyToFolder (sourceFolder, destinationFolder, messageNums, success, error) - (boolean) - Copy messages to a desired
  folder
- setFlag (folderName, messageNums, [flag](#flag) : <code>enum</code>, status, success,
  error) - ([ModificationResult](#modificationresult)) - Sets a flag on a message.
  This method can also be used for deleting messages.
- downloadEmailAttachment(folderName, messageNo, path, contentID, attachmentFileName, success, error) - Download email attachment using "contentID" or "attachmentFileName". The "contentID" is preferred if available.

## Data types

#### Config

| Param          | Type                                            | Description                                                         |
|----------------|-------------------------------------------------|---------------------------------------------------------------------|
| host           | <code>string</code>                             | Hostname or IP address of the IMAP service.                         |
| port           | <code>number</code>                             | Optional. Port of the IMAP server to connect. Default set to: 993   |
| connectionType | <code> [ConnectionType](#connectionType)</code> | iOS ONLY. Optional. Encryption type to use. Default set to: TLS/SSL |
| user           | <code>string</code>                             | Username or email address for authentication.                       |
| password       | <code>string</code>                             | Password for authentication.                                        |

#### ConnectionType <code>enum</code>

| Name     | Type                           | Description                                                                                          |
|----------|--------------------------------|------------------------------------------------------------------------------------------------------|
| Clear    | <code>MCOConnectionType</code> | Clear-text connection for the protocol.                                                              |
| StartTLS | <code>MCOConnectionType</code> | Start with clear-text connection at the beginning, then switch to encrypted connection using TLS/SSL |
| TLS/SSL  | <code>MCOConnectionType</code> | Encrypted connection using TLS/SSL                                                                   |

#### Connection

| Param      | Type                 | Description                                                                                   |
|------------|----------------------|-----------------------------------------------------------------------------------------------|
| status     | <code>boolean</code> | Connection status.                                                                            |
| connection | <code>string</code>  | Optional. Connection String result, returned when the connection is established successfully. |
| exception  | <code>string</code>  | Optional. Exception details, in case an error occurs.                                         |

#### MessageHeaders

| Param         | Type                                        | Description                             |
|---------------|---------------------------------------------|-----------------------------------------|
| messageNumber | <code>number</code>                         | Message consecutive number.             |
| folder        | <code>string</code>                         | The name of the message's folder.       |
| from          | <code>`Array<`[Address](#address)`>`</code> | Sender's data.                          |
| toRecipients  | <code>`Array<`[Address](#address)`>`</code> | TO recipients data.                     |
| ccRecipients  | <code>`Array<`[Address](#address)`>`</code> | CC recipients data.                     |
| bccRecipients | <code>`Array<`[Address](#address)`>`</code> | BCC recipients data.                    |
| receivedDate  | <code>string</code>                         | The date when the message was received. |
| subject       | <code>string</code>                         | Message's subject.                      |
| flags         | <code>`Array<`string`>`</code>              | Message's active flags                  |

#### Message

| Param             | Type                                        | Description                                                |
|-------------------|---------------------------------------------|------------------------------------------------------------|
| messageNumber     | <code>number</code>                         | Message consecutive number.                                |
| folder            | <code>string</code>                         | The name of the message's folder.                          |
| from              | <code>`Array<`[Address](#address)`>`</code> | Sender's data.                                             |
| allRecipients     | <code>`Array<`[Address](#address)`>`</code> | All recipients data.                                       |
| toRecipients      | <code>`Array<`[Address](#address)`>`</code> | TO recipients data.                                        |
| ccRecipients      | <code>`Array<`[Address](#address)`>`</code> | CC recipients data.                                        |
| bccRecipients     | <code>`Array<`[Address](#address)`>`</code> | BCC recipients data.                                       |
| replyTo           | <code>`Array<`[Address](#address)`>`</code> | Reply data.                                                |
| sentDate          | <code>string</code>                         | The date when the message was sent.                        |
| receivedDate      | <code>string</code>                         | The date when the message was received.                    |
| subject           | <code>string</code>                         | Message's subject.                                         |
| description       | <code>string</code>                         | Android ONLY. Optional. Short description.                 |
| fileName          | <code>string</code>                         | /                                                          |
| disposition       | <code>string</code>                         | Android ONLY. Optional. /                                  |
| flags             | <code>`Array<`string`>`</code>              | Message's active flags                                     |
| lineCount         | <code>number</code>                         | Android ONLY. Optional. /                                  |
| allMessageHeaders | <code>object</code>                         | Android ONLY. Optional. All Headers available on a message |
| contentType       | <code>string</code>                         | Android ONLY. Optional. Type of message's content          |
| bodyContent       | <code>`Array<`[Content](#content)`>`</code> | Message's body with its content and attachments.           |
| size              | <code>number</code>                         | Message's memory size                                      |

#### Address

| Param    | Type                | Description                                  |
|----------|---------------------|----------------------------------------------|
| address  | <code>string</code> | Email address                                |
| personal | <code>string</code> | Optional. Name of the email address's owner. |
| type     | <code>string</code> | Android ONLY. Optional. Data type            |

#### Content

| Param    | Type                | Description                    |
|----------|---------------------|--------------------------------|
| type     | <code>string</code> | Content data type              |
| fileName | <code>string</code> | Optional. The name of the file |
| content  | <code>string</code> | Message's content              |

#### ModificationResult

| Param            | Type                         | Description                                         |
|------------------|------------------------------|-----------------------------------------------------|
| status           | <code>boolean</code>         | Status of the applied changes                       |
| modifiedMessages | <code>`Array<number>`</code> | Array with consecutive numbers of modified messages |

#### Flag : <code>enum</code>

Defines a flag that can be added or removed from a message.

#### Note: Some flags are available only for a particular platform

**Kind**: static enum property of <code>flag</code>  
**Available Flags**

| Name          | Type                                                  | Description                                                                                     |
|---------------|-------------------------------------------------------|-------------------------------------------------------------------------------------------------|
| ANSWERED      | <code>Flags.Flag</code> / <code>MCOMessageFlag</code> | "ANSWERED" message flag                                                                         |
| DRAFT         | <code>Flags.Flag</code> / <code>MCOMessageFlag</code> | "DRAFT" message flag                                                                            |
| FLAGGED       | <code>Flags.Flag</code> / <code>MCOMessageFlag</code> | "FLAGGED" message flag                                                                          |
| RECENT        | <code>Flags.Flag</code>                               | Android ONLY. "RECENT" message flag                                                             |
| SEEN          | <code>Flags.Flag</code> / <code>MCOMessageFlag</code> | "SEEN" message flag                                                                             |
| USER          | <code>Flags.Flag</code>                               | Android ONLY. "USER" message flag                                                               |
| DELETED       | <code>Flags.Flag</code> / <code>MCOMessageFlag</code> | "DELETED" message flag. <code>Note:</code> Add this flag to delete the message from the mailbox |
| SENT          | <code>MCOMessageFlag</code>                           | iOS ONLY. "SENT" message flag                                                                   |
| FORWARDED     | <code>MCOMessageFlag</code>                           | iOS ONLY. "FORWARDED" message flag                                                              |
| SubmitPending | <code>MCOMessageFlag</code>                           | iOS ONLY. "SubmitPending" message flag                                                          |
| SUBMITTED     | <code>MCOMessageFlag</code>                           | iOS ONLY. "SUBMITTED" message flag                                                              |

#### ComparisonTerm : <code>enum</code>

Comparison Operators. Used for listing messages by date period.

#### Note: Some operators are available only for a particular platform

**Kind**: static enum property of <code>comparison</code>  
**Available Operators**

| Name | Type                                                                 | Description                                       |
|------|----------------------------------------------------------------------|---------------------------------------------------|
| LE   | <code>ReceivedDateTerm</code>                                        | Android ONLY. The less than or equal to operator. |
| LT   | <code>ReceivedDateTerm</code> / <code>MCOIMAPSearchExpression</code> | The less than operator.                           |
| EQ   | <code>ReceivedDateTerm</code> / <code>MCOIMAPSearchExpression</code> | The equality operator.                            |
| NE   | <code>ReceivedDateTerm</code> / <code>MCOIMAPSearchExpression</code> | The not equal to operator.                        |
| GT   | <code>ReceivedDateTerm</code>                                        | Android ONLY. The greater than operator.          |
| GE   | <code>ReceivedDateTerm</code> / <code>MCOIMAPSearchExpression</code> | The greater than or equal to operator.            |

## License

[ISC](https://choosealicense.com/licenses/isc/)

## Thank you

Thank you for using this plugin. <br>
If you have any suggestions on how we can improve the plugin (missing feature or bug...), feel free to contact us.
