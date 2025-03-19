import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.apache.cordova.PluginResult.Status;
import org.apache.cordova.PluginResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Properties;
import java.util.Date;
import java.util.Properties;
import java.util.Enumeration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.security.auth.callback.Callback;

import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.Part;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Folder;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.search.ReceivedDateTerm;
import javax.mail.search.SearchTerm;
import javax.mail.BodyPart;
import javax.mail.Header;
import javax.mail.Flags;


public class Imap extends CordovaPlugin {

    private static Store store;

    public Imap() {
        initializeStoreObject();
    }

    private static void initializeStoreObject() {
        try {
            store = Session.getDefaultInstance(System.getProperties()).getStore("imap");
        } catch (NoSuchProviderException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("connect")) {
            JSONObject clientData = args.getJSONObject(0);

            this.connect(clientData, callbackContext);
            return true;
        } else if (action.equals("disconnect")) {
            this.disconnect(callbackContext);
            return true;
        } else if (action.equals("isConnected")) {
            this.isConnected(callbackContext);
            return true;
        } else if (action.equals("listMailFolders")) {
            String pattern = args.getString(0);

            this.listMailFolders(pattern, callbackContext);
            return true;
        } else if (action.equals("getMessageCountByFolderName")) {
            String folderName = args.getString(0);

            this.getMessageCountByFolderName(folderName, callbackContext);
            return true;
        } else if (action.equals("searchMessagesByDatePeriod")) {
            String folderName = args.getString(0);
            long date = Long.parseLong(args.getString(1));
            ComparisonTerm comparison = ComparisonTerm.valueOf(args.getString(2));

            this.searchMessagesByDatePeriod(folderName, date, comparison, callbackContext);
            return true;
        } else if (action.equals("listMessagesHeadersByConsecutiveNumber")) {
            String folderName = args.getString(0);
            int start = Integer.parseInt(args.getString(1));
            int end = Integer.parseInt(args.getString(2));

            this.listMessagesHeaders(folderName, start, end, callbackContext);
            return true;
        } else if (action.equals("listMessagesHeadersByDate")) {
            String folderName = args.getString(0);
            long date = Long.parseLong(args.getString(1));
            ComparisonTerm comparison = ComparisonTerm.valueOf(args.getString(2));

            this.listMessagesHeaders(folderName, date, comparison, callbackContext);
            return true;
        } else if (action.equals("getFullMessageData")) {
            String folderName = args.getString(0);
            int mesNumber = Integer.parseInt(args.getString(1));

            this.getFullMessageData(folderName, mesNumber, callbackContext);
            return true;
        } else if (action.equals("getFullMessageDataOnNewSession")) {
            JSONObject clientData = args.getJSONObject(0);
            String folderName = args.getString(1);
            int mesNumber = Integer.parseInt(args.getString(2));

            this.getFullMessageDataOnNewSession(clientData, folderName, mesNumber, callbackContext);
            return true;
        } else if (action.equals("copyToFolder")) {
            String sourceFolder = args.getString(0);
            String destinationFolder = args.getString(1);
            int[] messageNums = convertJSONArrayToIntArray(args.getJSONArray(2));

            this.copyToFolder(sourceFolder, destinationFolder, messageNums, callbackContext);
            return true;
        } else if (action.equals("setFlag")) {
            String folderName = args.getString(0);
            int[] messageNums = convertJSONArrayToIntArray(args.getJSONArray(1));
            Flag flag = Flag.valueOf(args.getString(2));
            boolean status = Boolean.parseBoolean(args.getString(3));

            this.setFlag(folderName, messageNums, flag, status, callbackContext);
            return true;
        } else if (action.equals("downloadEmailAttachment")) {
            String folderName = args.getString(0);
            int messageNo = Integer.parseInt(args.getString(1));
            String path = args.getString(2);
            boolean replaceIfDuplicate = Boolean.parseBoolean(args.getString(3));
            String fileName = args.getString(4).equals("null") || args.getString(4).isEmpty() ? null : args.getString(4);
            String contentID = args.getString(5).equals("null") || args.getString(5).isEmpty() ? null : args.getString(5);

            this.downloadEmailAttachment(folderName, messageNo, path, replaceIfDuplicate, contentID, fileName, callbackContext);
            return true;
        }
        return false;
    }

    private static Store createStoreObject(String imapConnectionType) throws NoSuchProviderException {
        try {
            Properties properties = System.getProperties();
            String protocol = "imaps";

            if (imapConnectionType != null && !imapConnectionType.isEmpty()) {
                protocol = "imap";

                properties.setProperty("mail.imap.starttls.enable", String.valueOf(imapConnectionType.equals("StartTLS")));
                properties.setProperty("mail.imap.ssl.enable", String.valueOf(imapConnectionType.equals("TLS/SSL")));
            }
            properties.setProperty("mail.store.protocol", protocol);

            return Session.getDefaultInstance(properties)
                    .getStore(protocol);
        } catch (NoSuchProviderException ex) {
            throw ex;
        }
    }

    private static int[] convertJSONArrayToIntArray(JSONArray array) {
        if (array != null) {
            int[] numbers = new int[array.length()];

            for (int i = 0; i < array.length(); ++i) {
                numbers[i] = array.optInt(i);
            }

            return numbers;
        }

        return new int[]{};
    }

    private static <T> String parseStringResult(T data) {
        try {
            if (data != null) {
                return data.toString();
            }

            return "";
        } catch (Exception e) {
            return "";
        }
    }

    private static JSONArray parseAddressHeader(Address[] address) {
        try {
            JSONArray jsonArray = new JSONArray();

            if (address != null) {
                for (Address a : address) {
                    JSONObject jsonObject = new JSONObject();

                    InternetAddress iAddress = (InternetAddress) a;

                    jsonObject.put("address", iAddress.getAddress());
                    jsonObject.put("personal", iAddress.getPersonal());
                    jsonObject.put("type", iAddress.getType());

                    jsonArray.put(jsonObject);
                }
            }

            return jsonArray;
        } catch (Exception e) {
            return new JSONArray();
        }
    }

    private static JSONObject parseAllMessageHeaders(Message messageData) {
        try {
            JSONObject resultData = new JSONObject();

            MimeMessage message = (MimeMessage) messageData;

            Enumeration allHeaders = message.getAllHeaders();
            while (allHeaders.hasMoreElements()) {
                Header header = (Header) allHeaders.nextElement();
                String headerName = header.getName();
                String headerVal = header.getValue();

                resultData.put(headerName, headerVal);
            }

            return resultData;
        } catch (Exception ex) {
            return new JSONObject();
        }
    }

    public static JSONArray serializeFlags(Flags flags) {
        JSONArray flagsArray = new JSONArray();

        if (flags.contains(Flags.Flag.ANSWERED))
            flagsArray.put("Answered");
        if (flags.contains(Flags.Flag.DELETED))
            flagsArray.put("Deleted");
        if (flags.contains(Flags.Flag.DRAFT))
            flagsArray.put("Draft");
        if (flags.contains(Flags.Flag.FLAGGED))
            flagsArray.put("Flagged");
        if (flags.contains(Flags.Flag.RECENT))
            flagsArray.put("Recent");
        if (flags.contains(Flags.Flag.SEEN))
            flagsArray.put("Seen");
        if (flags.contains(Flags.Flag.USER))
            flagsArray.put("*");

        return flagsArray;
    }

    private static JSONArray parseMessagesHeaders(Message[] messages) throws MessagingException, JSONException {
        JSONArray resultData = new JSONArray();

        for (Message mes : messages) {
            JSONObject message = new JSONObject();

            message.put("messageNumber", mes.getMessageNumber());
            message.put("folder", parseStringResult(mes.getFolder()));
            message.put("from", parseAddressHeader(mes.getFrom()));
            message.put("toRecipients", parseAddressHeader(mes.getRecipients(Message.RecipientType.TO)));
            message.put("ccRecipients", parseAddressHeader(mes.getRecipients(Message.RecipientType.CC)));
            message.put("bccRecipients", parseAddressHeader(mes.getRecipients(Message.RecipientType.BCC)));
            message.put("receivedDate", parseStringResult(mes.getReceivedDate()));
            message.put("subject", parseStringResult(mes.getSubject()));
            message.put("flags", serializeFlags(mes.getFlags()));

            resultData.put(message);
        }

        return resultData;
    }

    private static JSONObject parseFullMessage(Message message) throws MessagingException, JSONException, IOException, Exception {
        JSONObject resultData = new JSONObject();

        resultData.put("messageNumber", message.getMessageNumber());
        resultData.put("folder", parseStringResult(message.getFolder()));
        resultData.put("from", parseAddressHeader(message.getFrom()));
        resultData.put("allRecipients", parseAddressHeader(message.getAllRecipients()));
        resultData.put("toRecipients", parseAddressHeader(message.getRecipients(Message.RecipientType.TO)));
        resultData.put("ccRecipients", parseAddressHeader(message.getRecipients(Message.RecipientType.CC)));
        resultData.put("bccRecipients", parseAddressHeader(message.getRecipients(Message.RecipientType.BCC)));
        resultData.put("replyTo", parseAddressHeader(message.getReplyTo()));
        resultData.put("sentDate", parseStringResult(message.getSentDate()));
        resultData.put("receivedDate", parseStringResult(message.getReceivedDate()));
        resultData.put("subject", parseStringResult(message.getSubject()));
        resultData.put("description", parseStringResult(message.getDescription()));
        resultData.put("fileName", parseStringResult(message.getFileName()));
        resultData.put("disposition", parseStringResult(message.getDisposition()));
        resultData.put("flags", serializeFlags(message.getFlags()));
        resultData.put("lineCount", message.getLineCount());
        resultData.put("allMessageHeaders", parseAllMessageHeaders(message));
        resultData.put("contentType", parseStringResult(message.getContentType()));
        resultData.put("bodyContent", new ParseMessageBody().getBodyContent(message));
        resultData.put("size", message.getSize());

        return resultData;
    }

    private void connect(JSONObject clientData, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    JSONObject resultData = new JSONObject();

                    if (clientData != null) {
                        String host = clientData.getString("host");
                        int port = ((clientData.has("port") && !clientData.isNull("port"))) ? Integer.parseInt(clientData.getString("port")) : 993;
                        String imapConnectionType = clientData.has("connectionType") ? clientData.getString("connectionType") : "";
                        String user = clientData.getString("user");
                        String password = clientData.getString("password");

                        if (!store.isConnected())
                            store = createStoreObject(imapConnectionType);

                        store.connect(host, port, user, password);

                        resultData.put("status", true);
                        resultData.put("connection", store.toString());

                        callbackContext.success(resultData);
                    } else {
                        resultData.put("status", false);
                        resultData.put("exception", "Invalid input data");

                        callbackContext.error(resultData);
                    }
                } catch (Exception ex) {
                    callbackContext.error("" + ex);
                }
            }
        });
    }

    private void disconnect(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    store.close();
                    callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
                } catch (Exception ex) {
                    callbackContext.error("Failed to disconnect " + ex);
                }
            }
        });
    }

    private void isConnected(CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    boolean status = store.isConnected();

                    callbackContext.sendPluginResult(new PluginResult(Status.OK, status));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void listMailFolders(String pattern, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder[] folders = store.getDefaultFolder().list(pattern);

                    JSONArray resultData = new JSONArray(Arrays.stream(folders).map(Folder::getFullName).toArray());

                    callbackContext.success(resultData);
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void getMessageCountByFolderName(String folderName, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    callbackContext.success(emailFolder.getMessageCount());
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void searchMessagesByDatePeriod(String folderName, long dateInMilliseconds, ComparisonTerm comparison, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    final SearchTerm term = new ReceivedDateTerm(comparison.getComparisonTerm(), new Date(dateInMilliseconds));

                    Message[] messages = emailFolder.search(term);

                    JSONArray resultData = new JSONArray();

                    for (Message m : messages) {
                        resultData.put(m.getMessageNumber());
                    }

                    callbackContext.success(resultData);
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void listMessagesHeaders(String folderName, int start, int end, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    Message[] messages = emailFolder.getMessages(start, end);

                    callbackContext.success(parseMessagesHeaders(messages));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void listMessagesHeaders(String folderName, long dateInMilliseconds, ComparisonTerm comparison, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    final SearchTerm term = new ReceivedDateTerm(comparison.getComparisonTerm(), new Date(dateInMilliseconds));

                    Message[] messages = emailFolder.search(term);

                    callbackContext.success(parseMessagesHeaders(messages));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void getFullMessageData(String folderName, int messageNumber, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    Message message = emailFolder.getMessage(messageNumber);

                    callbackContext.success(parseFullMessage(message));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void getFullMessageDataOnNewSession(JSONObject clientData, String folderName, int messageNumber, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    String host = clientData.getString("host");
                    int port = ((clientData.has("port") && !clientData.isNull("port"))) ? Integer.parseInt(clientData.getString("port")) : 993;
                    String imapConnectionType = clientData.has("connectionType") ? clientData.getString("connectionType") : "";
                    String user = clientData.getString("user");
                    String password = clientData.getString("password");

                    Store storeObj = createStoreObject(imapConnectionType);

                    try {
                        storeObj.connect(host, port, user, password);

                        Folder emailFolder = storeObj.getFolder(folderName);
                        emailFolder.open(Folder.READ_ONLY);

                        Message message = emailFolder.getMessage(messageNumber);

                        callbackContext.success(parseFullMessage(message));
                    } catch (Exception ex) {
                        callbackContext.error("Failed " + ex);
                    } finally {
                        storeObj.close();
                    }
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void copyToFolder(String sourceFolder, String destinationFolder, int[] messageNums, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    Folder emailFolder = store.getFolder(sourceFolder);
                    emailFolder.open(Folder.READ_ONLY);

                    Message[] messages = emailFolder.getMessages(messageNums);

                    emailFolder.copyMessages(messages, store.getFolder(destinationFolder));

                    emailFolder.close(true);

                    callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void setFlag(String folderName, int[] messageNums, Flag flag, boolean status, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    JSONObject resultData = new JSONObject();
                    JSONArray modifiedMessages = new JSONArray();

                    try {
                        Folder emailFolder = store.getFolder(folderName);
                        emailFolder.open(Folder.READ_WRITE);

                        int totalMessagesInFolder = emailFolder.getMessageCount();

                        Message[] messages = emailFolder.getMessages(
                                Arrays.stream(messageNums).filter(num -> num <= totalMessagesInFolder).toArray()
                        );

                        for (Message message : messages) {
                            message.setFlag(flag.getFlag(), status);
                            modifiedMessages.put(message.getMessageNumber());
                        }

                        emailFolder.close(true);

                        resultData.put("status", true);
                        resultData.put("modified", modifiedMessages);

                        callbackContext.success(resultData);
                    } catch (Exception ex) {

                        resultData.put("status", false);
                        resultData.put("modified", modifiedMessages);

                        callbackContext.error(resultData);
                    }
                } catch (JSONException ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private void downloadEmailAttachment(String folderName, int messageNo, String path, boolean replaceIfDuplicate,
                                         String contentID, String fileName, CallbackContext callbackContext) {
        cordova.getThreadPool().execute(new Runnable() {
            public void run() {
                try {
                    if (contentID == null && fileName == null)
                        throw new Exception("Failed: Please provide a 'contentID' or 'fileName'");

                    Folder emailFolder = store.getFolder(folderName);
                    emailFolder.open(Folder.READ_ONLY);

                    Message message = emailFolder.getMessage(messageNo);
                    MimeMultipart messageContent = (MimeMultipart) message.getContent();

                    MimeBodyPart attachment = contentID == null ?
                            getAttachmentByName(messageContent, fileName) :
                            (MimeBodyPart) messageContent.getBodyPart(contentID);

                    if (attachment != null && (Part.ATTACHMENT.equalsIgnoreCase(attachment.getDisposition())
                            || attachment.getFileName() != null)) {

                        attachment.saveFile(createFilePathWithFileName(replaceIfDuplicate, path, MimeUtility.decodeText(attachment.getFileName())));

                        callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
                    } else {
                        byte[] result = new InlineAttachmentHandler().getInlineAttachmentContentData(message, fileName);

                        saveByteFile(createFilePathWithFileName(replaceIfDuplicate, path, fileName), result);

                        callbackContext.sendPluginResult(new PluginResult(Status.OK, true));
                    }

                    callbackContext.sendPluginResult(new PluginResult(Status.OK, false));
                } catch (Exception ex) {
                    callbackContext.error("Failed " + ex);
                }
            }
        });
    }

    private MimeBodyPart getAttachmentByName(MimeMultipart messageContent, String fileName) throws Exception {
        try {
            for (int partCount = 0; partCount < messageContent.getCount(); partCount++) {

                MimeBodyPart messagePart = (MimeBodyPart) messageContent.getBodyPart(partCount);

                if (Part.ATTACHMENT.equalsIgnoreCase(messagePart.getDisposition()) &&
                        MimeUtility.decodeText(messagePart.getFileName()).replaceAll("\\s+", "")
                                .equals(fileName.replaceAll("\\s+", ""))) {
                    return messagePart;
                }
            }
            return null;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static String createFilePathWithFileName(boolean replaceIfDuplicate, String path, String fileName) throws Exception {
        try {
            String filePath = path + File.separator + fileName;

            if (replaceIfDuplicate) {
                return filePath;
            }

            File file = new File(filePath);

            int index = 1;

            while (file.exists() && !file.isDirectory()) {

                String[] fileTokens = fileName.split("\\.(?=[^\\.]+$)");

                filePath = path + File.separator + String.format("%s (%d).%s", fileTokens[0], index, fileTokens[1]);

                file = new File(filePath);

                index++;
            }

            return filePath;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private static void saveByteFile(String filePath, byte[] fileBytes) throws Exception {
        try (FileOutputStream stream = new FileOutputStream(filePath)) {
            stream.write(fileBytes);
        } catch (Exception ex) {
            throw ex;
        }
    }
}

enum Flag {
    ANSWERED(Flags.Flag.ANSWERED),
    DRAFT(Flags.Flag.DRAFT),
    FLAGGED(Flags.Flag.FLAGGED),
    RECENT(Flags.Flag.RECENT),
    SEEN(Flags.Flag.SEEN),
    USER(Flags.Flag.USER),
    DELETED(Flags.Flag.DELETED);

    private final Flags.Flag flag;

    Flag(Flags.Flag flag) {
        this.flag = flag;
    }

    public Flags.Flag getFlag() {
        return this.flag;
    }
}

enum ComparisonTerm {
    LE(ReceivedDateTerm.LE), // The less than or equal to operator.
    LT(ReceivedDateTerm.LT), // The less than operator.
    EQ(ReceivedDateTerm.EQ), // The equality operator.
    NE(ReceivedDateTerm.NE), // The not equal to operator.
    GT(ReceivedDateTerm.GT), // The greater than operator.
    GE(ReceivedDateTerm.GE); // The greater than or equal to operator.

    private final int comparisonTerm;

    ComparisonTerm(int term) {
        this.comparisonTerm = term;
    }

    public int getComparisonTerm() {
        return this.comparisonTerm;
    }
}
