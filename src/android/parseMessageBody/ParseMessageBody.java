import org.json.JSONArray;
import org.json.JSONObject;

import javax.mail.Multipart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeUtility;

import java.io.InputStream;


public class ParseMessageBody {

    private final String failedToReadContentError_Message = "Unable to read message content";

    public JSONArray getBodyContent(Message message) throws Exception {
        try {
            if (message.isMimeType("text/*")) {
                return new JSONArray().put(new TextBodyPart().parse(message));
            } else if (message.isMimeType("multipart/mixed")) {
                return parseMultipartMixedBody((MimeMultipart) message.getContent());
            } else if (message.isMimeType("multipart/alternative")) {
                return parseMultipartAlternativeBody((Multipart) message.getContent());
            } else if (message.isMimeType("multipart/related")) {
                return parseMultipartRelatedBody((MimeMultipart) message.getContent());
            } else if (message.isMimeType("multipart/report")) {
                return parseMultipartReportBody((MimeMultipart) message.getContent());
            }

            JSONObject contentData = new JSONObject();
            contentData.put("type", message.getContentType());
            contentData.put("error", failedToReadContentError_Message);

            return new JSONArray().put(contentData);
        } catch (Exception ex) {
            throw ex;
        }
    }

    private JSONArray parseMultipartMixedBody(MimeMultipart bodyContent) throws Exception {
        try {
            JSONArray fullContent = new JSONArray();

            for (int partCount = 0; partCount < bodyContent.getCount(); partCount++) {
                JSONObject contentData = new JSONObject();

                MimeBodyPart bodyPart = (MimeBodyPart) bodyContent.getBodyPart(partCount);

                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition()) || bodyPart.getFileName() != null) {
                    contentData.put("contentID", bodyPart.getContentID());
                    contentData.put("fileName", MimeUtility.decodeText(bodyPart.getFileName()));
                    contentData.put("type", bodyPart.getContentType());
                } else if (bodyPart.isMimeType("text/*")) {
                    contentData = new TextBodyPart().parse(bodyPart);
                } else if (bodyPart.isMimeType("IMAGE/*")) {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("fileName", MimeUtility.decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/related")) {
                    JSONArray result = parseMultipartRelatedBody((MimeMultipart) bodyPart.getContent());

                    for (int i = 0; i < result.length(); i++) {
                        fullContent.put(result.getJSONObject(i));
                    }
                } else if (bodyPart.isMimeType("multipart/alternative")) {
                    JSONArray result = parseMultipartAlternativeBody((Multipart) bodyPart.getContent());

                    for (int i = 0; i < result.length(); i++) {
                        fullContent.put(result.getJSONObject(i));
                    }
                } else {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("error", failedToReadContentError_Message);
                }

                if (contentData.length() > 0) {
                    fullContent.put(contentData);
                }
            }

            return fullContent;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private JSONArray parseMultipartAlternativeBody(Multipart bodyContent) throws Exception {
        try {
            JSONArray fullContent = new JSONArray();

            for (int index = 0; index < bodyContent.getCount(); index++) {
                JSONObject contentData = new JSONObject();

                Part bodyPart = bodyContent.getBodyPart(index);

                if (bodyPart.isMimeType("text/*")) {
                    contentData = new TextBodyPart().parse(bodyPart);
                } else if (bodyPart.isMimeType("IMAGE/*")) {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("fileName", MimeUtility.decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/related")) {
                    JSONArray result = parseMultipartRelatedBody((MimeMultipart) bodyPart.getContent());

                    for (int i = 0; i < result.length(); i++) {
                        fullContent.put(result.getJSONObject(i));
                    }
                } else {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("error", failedToReadContentError_Message);
                }

                if (contentData.length() > 0) {
                    fullContent.put(contentData);
                }
            }

            return fullContent;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private JSONArray parseMultipartRelatedBody(MimeMultipart bodyContent) throws Exception {
        try {
            JSONArray fullContent = new JSONArray();

            for (int index = 0; index < bodyContent.getCount(); index++) {
                JSONObject contentData = new JSONObject();

                Part bodyPart = bodyContent.getBodyPart(index);

                if (bodyPart.isMimeType("text/*")) {
                    contentData = new TextBodyPart().parse(bodyPart);
                } else if (bodyPart.isMimeType("IMAGE/*")) {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("fileName", MimeUtility.decodeText(bodyPart.getFileName()));
                } else if (bodyPart.isMimeType("multipart/alternative")) {
                    JSONArray result = parseMultipartAlternativeBody((Multipart) bodyPart.getContent());

                    for (int i = 0; i < result.length(); i++) {
                        fullContent.put(result.getJSONObject(i));
                    }
                } else {
                    contentData.put("type", bodyContent.getContentType());
                    contentData.put("error", failedToReadContentError_Message);
                }

                if (contentData.length() > 0) {
                    fullContent.put(contentData);
                }
            }

            return fullContent;
        } catch (Exception ex) {
            throw ex;
        }
    }

    private JSONArray parseMultipartReportBody(MimeMultipart bodyContent) throws Exception {
        try {
            JSONArray fullContent = new JSONArray();

            for (int index = 0; index < bodyContent.getCount(); index++) {
                JSONObject contentData = new JSONObject();

                Part bodyPart = bodyContent.getBodyPart(index);

                if (bodyPart.isMimeType("text/*")) {
                    contentData = new TextBodyPart().parse(bodyPart);
                } else if (bodyPart.isMimeType("MESSAGE/*")) {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("content", new ProcessStreamContent().decodeStream((InputStream) bodyPart.getContent()));
                } else {
                    contentData.put("type", bodyPart.getContentType());
                    contentData.put("error", failedToReadContentError_Message);
                }

                if (contentData.length() > 0) {
                    fullContent.put(contentData);
                }
            }

            return fullContent;
        } catch (Exception ex) {
            throw ex;
        }
    }
}